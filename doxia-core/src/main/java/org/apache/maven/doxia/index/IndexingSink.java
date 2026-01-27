/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.doxia.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.doxia.index.IndexEntry.Type;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.BufferingSinkProxyFactory;
import org.apache.maven.doxia.sink.impl.BufferingSinkProxyFactory.BufferingSink;
import org.apache.maven.doxia.sink.impl.SinkAdapter;
import org.apache.maven.doxia.util.DoxiaUtils;

/**
 * A sink wrapper for populating an index tree for particular elements in a document.
 * Currently this only generates {@link IndexEntry} objects for sections.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class IndexingSink extends org.apache.maven.doxia.sink.impl.SinkWrapper {

    /** The current type. */
    private Type type;

    /** The stack. */
    private final Stack<IndexEntry> stack;

    /** A map containing all used ids of index entries as key and how often they are used as value
     * (0-based, i.e. 0 means used 1 time). {@link AtomicInteger} is only used here as it implements
     * a mutable integer (not for its atomicity).
     */
    private final Map<String, AtomicInteger> usedIds;

    private final IndexEntry rootEntry;

    /** Is {@code true} once the sink has been closed. */
    private boolean isComplete;

    private boolean isTitle;

    /** Is {@code true} if the sink is currently populating entry data (i.e. metadata about the current entry is not completely captured yet) */
    private boolean hasOpenEntry;

    /**
     * @deprecated legacy constructor, use {@link #IndexingSink(Sink)} with {@link SinkAdapter} as argument and call {@link #getRootEntry()} to retrieve the index tree afterwards.
     */
    @Deprecated
    public IndexingSink(IndexEntry rootEntry) {
        this(rootEntry, new SinkAdapter());
    }

    public IndexingSink(Sink delegate) {
        this(new IndexEntry("index"), delegate);
    }

    /**
     * Default constructor.
     */
    private IndexingSink(IndexEntry rootEntry, Sink delegate) {
        super(delegate);
        this.rootEntry = rootEntry;
        stack = new Stack<>();
        stack.push(rootEntry);
        usedIds = new HashMap<>();
        usedIds.put(rootEntry.getId(), new AtomicInteger());
        this.type = Type.UNKNOWN;
    }

    /**
     * This should only be called once the sink is closed.
     * Before that the tree might not be complete.
     * @return the tree of entries starting from the root
     * @throws IllegalStateException in case the sink was not closed yet
     */
    public IndexEntry getRootEntry() {
        if (!isComplete) {
            throw new IllegalStateException(
                    "The sink has not been closed yet, i.e. the index tree is not complete yet");
        }
        return rootEntry;
    }
    /**
     * <p>Getter for the field <code>title</code>.</p>
     * Shortcut for {@link #getRootEntry()} followed by {@link IndexEntry#getTitle()}.
     *
     * @return the title
     */
    public String getTitle() {
        return rootEntry.getTitle();
    }

    // ----------------------------------------------------------------------
    // Sink Overrides
    // ----------------------------------------------------------------------

    @Override
    public void title(SinkEventAttributes attributes) {
        isTitle = true;
        super.title(attributes);
    }

    @Override
    public void title_() {
        isTitle = false;
        super.title_();
    }

    @Override
    public void section(int level, SinkEventAttributes attributes) {
        super.section(level, attributes);
        indexEntryComplete(); // make sure the previous entry is complete
        this.type = IndexEntry.Type.fromSectionLevel(level);
        pushNewEntry(type);
    }

    @Override
    public void section_(int level) {
        indexEntryComplete(); // make sure the previous entry is complete
        pop();
        super.section_(level);
    }

    @Override
    public void sectionTitle_(int level) {
        indexEntryComplete();
        super.sectionTitle_(level);
    }

    @Override
    public void text(String text, SinkEventAttributes attributes) {
        if (isTitle) {
            rootEntry.setTitle(text);
        } else {
            switch (this.type) {
                case SECTION_1:
                case SECTION_2:
                case SECTION_3:
                case SECTION_4:
                case SECTION_5:
                case SECTION_6:
                    // -----------------------------------------------------------------------
                    // Sanitize the id. The most important step is to remove any blanks
                    // -----------------------------------------------------------------------

                    // append text to current entry
                    IndexEntry entry = stack.lastElement();

                    String title = entry.getTitle();
                    if (title != null) {
                        title += text;
                    } else {
                        title = text;
                    }
                    title = title.replaceAll("[\\r\\n]+", "");
                    entry.setTitle(title);

                    setEntryId(entry, title);
                    break;
                // Dunno how to handle others yet
                default:
                    break;
            }
        }
        super.text(text, attributes);
    }

    @Override
    public void anchor(String name, SinkEventAttributes attributes) {
        parseAnchor(name);
        super.anchor(name, attributes);
    }

    private boolean parseAnchor(String name) {
        switch (type) {
            case SECTION_1:
            case SECTION_2:
            case SECTION_3:
            case SECTION_4:
            case SECTION_5:
                IndexEntry entry = stack.lastElement();
                entry.setAnchor(true);
                setEntryId(entry, name);
                break;
            default:
                return false;
        }
        return true;
    }

    private void setEntryId(IndexEntry entry, String id) {
        if (entry.getId() != null) {
            usedIds.remove(entry.getId());
        }
        entry.setId(getUniqueId(DoxiaUtils.encodeId(id)));
    }

    /**
     * Converts the given id into a unique one by potentially suffixing it with an index value.
     *
     * @param id
     * @return the unique id
     */
    String getUniqueId(String id) {
        final String uniqueId;

        if (usedIds.containsKey(id)) {
            uniqueId = id + "_" + usedIds.get(id).incrementAndGet();
        } else {
            usedIds.put(id, new AtomicInteger());
            uniqueId = id;
        }
        return uniqueId;
    }

    void indexEntryComplete() {
        if (!hasOpenEntry) {
            return;
        }
        this.type = Type.UNKNOWN;
        // remove buffering sink from pipeline
        BufferingSink bufferingSink = BufferingSinkProxyFactory.castAsBufferingSink(getWrappedSink());
        setWrappedSink(bufferingSink.getBufferedSink());

        onIndexEntry(stack.peek());

        // flush the buffer afterwards
        bufferingSink.flush();
        hasOpenEntry = false;
    }

    /**
     * Called at the beginning of each entry (once all metadata about it is collected).
     * The events for the metadata are buffered and only flushed after this method was called.
     * @param entry the newly collected entry
     */
    protected void onIndexEntry(IndexEntry entry) {}

    /**
     * Creates and pushes a new IndexEntry onto the top of this stack.
     */
    private void pushNewEntry(Type type) {
        IndexEntry entry = new IndexEntry(peek(), null, type);
        stack.push(entry);
        // now buffer everything till the next index metadata is complete
        setWrappedSink(new BufferingSinkProxyFactory().createWrapper(getWrappedSink()));
        hasOpenEntry = true;
    }

    /**
     * Pushes an IndexEntry onto the top of this stack.
     *
     * @param entry to put.
     */
    public void push(IndexEntry entry) {
        stack.push(entry);
    }

    /**
     * Removes the IndexEntry at the top of this stack.
     */
    public void pop() {
        stack.pop();
    }

    /**
     * <p>peek.</p>
     *
     * @return Looks at the IndexEntry at the top of this stack.
     */
    public IndexEntry peek() {
        return stack.peek();
    }

    @Override
    public void close() {
        super.close();
        isComplete = true;
    }
}
