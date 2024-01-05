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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.maven.doxia.markup.Markup;
import org.apache.maven.doxia.sink.Sink;

/**
 * Representing the index tree within a document with the most important metadata per entry.
 * Currently this only contains entries for sections, but in the future may be extended, therefore it
 * is recommended to use {@link #getType()} to filter out irrelevant entries.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class IndexEntry {
    /**
     * The parent entry.
     */
    private final IndexEntry parent;

    /**
     * The id of the entry.
     */
    private String id;

    /**
     * true if there is already an anchor for this
     */
    private boolean hasAnchor;

    /**
     * The entry title.
     */
    private String title;

    /**
     * The child entries.
     */
    private List<IndexEntry> childEntries = new ArrayList<>();

    public enum Type {
        /**
         * Used for unknown types but also for the root entry
         */
        UNKNOWN(),
        SECTION_1(Sink.SECTION_LEVEL_1),
        SECTION_2(Sink.SECTION_LEVEL_2),
        SECTION_3(Sink.SECTION_LEVEL_3),
        SECTION_4(Sink.SECTION_LEVEL_4),
        SECTION_5(Sink.SECTION_LEVEL_5),
        SECTION_6(),
        DEFINED_TERM(),
        FIGURE(),
        TABLE();

        private final int sectionLevel;

        Type() {
            this(-1);
        }

        Type(int sectionLevel) {
            this.sectionLevel = sectionLevel;
        }

        static Type fromSectionLevel(int level) {
            if (level < Sink.SECTION_LEVEL_1 || level > Sink.SECTION_LEVEL_5) {
                throw new IllegalArgumentException("Level must be between " + Sink.SECTION_LEVEL_1 + " and "
                        + Sink.SECTION_LEVEL_5 + " but is " + level);
            }
            return Arrays.stream(Type.values())
                    .filter(t -> level == t.sectionLevel)
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Could not find enum for sectionLevel " + level));
        }
    };

    /**
     * The type of the entry, one of the types defined by {@link IndexingSink}
     */
    private final Type type;

    /**
     * Constructor for root entry.
     *
     * @param newId The id. May be null.
     */
    public IndexEntry(String newId) {
        this(null, newId);
    }

    /**
     * Constructor.
     *
     * @param newParent The parent. May be null.
     * @param newId     The id. May be null.
     */
    public IndexEntry(IndexEntry newParent, String newId) {
        this(newParent, newId, Type.UNKNOWN);
    }

    /**
     * Constructor.
     *
     * @param newParent The parent. May be null.
     * @param newId     The id. May be null.
     * @param
     */
    public IndexEntry(IndexEntry newParent, String newId, Type type) {
        this.parent = newParent;
        this.id = newId;

        if (parent != null) {
            parent.childEntries.add(this);
        }
        this.type = type;
    }

    /**
     * Returns the parent entry.
     *
     * @return the parent entry.
     */
    public IndexEntry getParent() {
        return parent;
    }

    /**
     * Returns the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id.
     *
     * @param id the id
     * @since 1.1.2
     */
    protected void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the type of this entry. Is one of the types defined by {@link IndexingSink}.
     * @return the type of this entry
     * @since 2.0.0
     */
    public Type getType() {
        return type;
    }

    /** Set if the entry's id already has an anchor in the underlying document.
     *
     * @param hasAnchor {@true} if the id already has an anchor.
     * @since 2.0.0
     */
    public void setAnchor(boolean hasAnchor) {
        this.hasAnchor = hasAnchor;
    }

    /**
     * Returns if the entry's id already has an anchor in the underlying document.
     * @return {@code true} if the id already has an anchor otherwise {@code false}.
     *
     * @since 2.0.0
     */
    public boolean hasAnchor() {
        return hasAnchor;
    }

    /**
     * Returns the title.
     *
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param newTitle the title.
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * Returns an unmodifiableList of the child entries.
     *
     * @return child entries.
     */
    public List<IndexEntry> getChildEntries() {
        return Collections.unmodifiableList(childEntries);
    }

    /**
     * Sets the child entries or creates a new ArrayList if entries == null.
     *
     * @param entries the entries.
     */
    public void setChildEntries(List<IndexEntry> entries) {
        if (entries == null) {
            childEntries = new ArrayList<>();
        }

        this.childEntries = entries;
    }

    // -----------------------------------------------------------------------
    // Utils
    // -----------------------------------------------------------------------

    /**
     * Returns the next entry.
     *
     * @return the next entry, or null if there is none.
     */
    public IndexEntry getNextEntry() {
        if (parent == null) {
            return null;
        }

        List<IndexEntry> entries = parent.getChildEntries();

        int index = entries.indexOf(this);

        if (index + 1 >= entries.size()) {
            return null;
        }

        return entries.get(index + 1);
    }

    /**
     * Returns the previous entry.
     *
     * @return the previous entry, or null if there is none.
     */
    public IndexEntry getPrevEntry() {
        if (parent == null) {
            return null;
        }

        List<IndexEntry> entries = parent.getChildEntries();

        int index = entries.indexOf(this);

        if (index == 0) {
            return null;
        }

        return entries.get(index - 1);
    }

    /**
     * Returns the first entry.
     *
     * @return the first entry, or null if there is none.
     */
    public IndexEntry getFirstEntry() {
        List<IndexEntry> entries = getChildEntries();

        if (entries.size() == 0) {
            return null;
        }

        return entries.get(0);
    }

    /**
     * Returns the last entry.
     *
     * @return the last entry, or null if there is none.
     */
    public IndexEntry getLastEntry() {
        List<IndexEntry> entries = getChildEntries();

        if (entries.size() == 0) {
            return null;
        }

        return entries.get(entries.size() - 1);
    }

    /**
     * Returns the root entry.
     *
     * @return the root entry, or null if there is none.
     */
    public IndexEntry getRootEntry() {
        List<IndexEntry> entries = getChildEntries();

        if (entries.size() == 0) {
            return null;
        } else if (entries.size() > 1) {
            throw new IllegalStateException("This index has more than one root entry");
        } else {
            return entries.get(0);
        }
    }

    // -----------------------------------------------------------------------
    // Object Overrides
    // -----------------------------------------------------------------------

    /**
     * Returns a string representation of the object.
     *
     * @return Returns a string representation of all objects
     */
    public String toString() {
        return toString(0);
    }

    /**
     * Returns a string representation of all objects to the given depth.
     *
     * @param depth The depth to descent to.
     * @return A string.
     */
    public String toString(int depth) {
        StringBuilder message = new StringBuilder();

        message.append("Id: ").append(id);

        if (title != null && !title.isEmpty()) {
            message.append(", title: ").append(title);
        }

        message.append(Markup.EOL);

        StringBuilder indent = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            indent.append(" ");
        }

        for (IndexEntry entry : getChildEntries()) {
            message.append(indent).append(entry.toString(depth + 1));
        }

        return message.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(childEntries, hasAnchor, id, parent, title, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndexEntry other = (IndexEntry) obj;
        return Objects.equals(childEntries, other.childEntries)
                && hasAnchor == other.hasAnchor
                && Objects.equals(id, other.id)
                && Objects.equals(parent, other.parent)
                && Objects.equals(title, other.title)
                && type == other.type;
    }
}
