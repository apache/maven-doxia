package org.apache.maven.doxia.module.fo;

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
import java.io.Writer;

import org.apache.maven.doxia.document.DocumentTOCItem;
import org.apache.maven.doxia.sink.SinkEventAttributes;

/**
 * A Doxia Sink that is dedicated to extract the chapter structure from the
 * files, in order to enrich the table of contents with sub-chapters. In order
 * to reach this aim, this sink is used by the PdfRenderer during the render
 * process. The documents are parsed into this sink one after another and after
 * each document the substructure can be copied to a DocumentTOCItem by using
 * the function "enrichTOCItemWithSubstructure()"
 *
 * @since 1.5.0.4-SNAPSHOT
 */
public class DocumentStructureExtractionSink extends FoAggregateSink {

    int stackCtr = 0;
    private LocalTOCItem root = null;
    private LocalTOCItem currentItem = null;

    private boolean titleOpen = false;
    private int currentLevel = -1;
    private String titleString = null;

    public DocumentStructureExtractionSink(Writer writer) {
        super(writer);
    }

    /**
     * {@inheritDoc}
     */
    public void head(SinkEventAttributes attributes) {
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void head() {
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void head_() {
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void title(SinkEventAttributes attributes) {
        currentLevel = 0;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void title() {
        currentLevel = 0;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void title_() {
        currentLevel = 0;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void author(SinkEventAttributes attributes) {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void author() {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void author_() {
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void date(SinkEventAttributes attributes) {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void date() {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void date_() {
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void body(SinkEventAttributes attributes) {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void body() {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void body_() {
        stackCtr--;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void sectionTitle() {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle_() {
        currentLevel = 0;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section(int level, SinkEventAttributes attributes) {
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void section_(int level) {
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle(int level, SinkEventAttributes attributes) {
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle_(int level) {
        currentLevel = 0;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void section1() {
        currentLevel = 1;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1() {
        currentLevel = 1;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle1_() {
        currentLevel = 1;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section1_() {
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section2() {
        currentLevel = 2;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2() {
        currentLevel = 2;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle2_() {
        currentLevel = 2;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section2_() {
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section3() {
        currentLevel = 3;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3() {
        currentLevel = 3;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle3_() {
        currentLevel = 3;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section3_() {
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section4() {
        currentLevel = 4;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4() {
        currentLevel = 4;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle4_() {
        currentLevel = 4;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section4_() {
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section5() {
        currentLevel = 5;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5() {
        currentLevel = 5;
        titleOpen = true;
        stackCtr++;
    }

    /**
     * {@inheritDoc}
     */
    public void sectionTitle5_() {
        currentLevel = 5;
        if (titleString != null) {
            createNewTOCItem(titleString);
        }
        titleString = null;
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    /**
     * {@inheritDoc}
     */
    public void section5_() {
        currentLevel = -1;
        titleOpen = false;
        stackCtr--;
    }

    public void write(String str) {
        if (validTitle(str)) {
            if (titleString == null) {
                titleString = str;
            } else {
                titleString = titleString + str;
            }

        }
    }

    private boolean validTitle(String str) {
        boolean res = false;

        if (titleOpen && !str.trim().isEmpty() && !str.startsWith("<") && !str.endsWith(">")) {
            res = true;
        }
        return res;
    }

    public void enrichTOCItemWithSubstructure(DocumentTOCItem docTOCItem) {
        if (root != null && docTOCItem != null) {
            commitChildrenToTOCItem(root, docTOCItem);
        }
    }

    private void commitChildrenToTOCItem(LocalTOCItem lti, DocumentTOCItem matchingTOCItem) {
        if (lti != null && matchingTOCItem != null) {
            java.util.List<LocalTOCItem> ltiChildren = lti.getChildren();
            if (ltiChildren != null) {
                for (LocalTOCItem ltiC : ltiChildren) {
                    if (ltiC != null) {
                        DocumentTOCItem dtiChild = new DocumentTOCItem();
                        dtiChild.setName(ltiC.getName());
                        matchingTOCItem.addItem(dtiChild);
                        commitChildrenToTOCItem(ltiC, dtiChild);
                    }
                }
            }
        }
    }

    private void createNewTOCItem(String name) {
        LocalTOCItem current = currentItem;
        int lvl = currentLevel;

        if (lvl >= 0) {
            if (current != null) {
                while (current != null && current.level > lvl - 1) {
                    current = current.parent;
                }

            }
            addNewTOCItem(lvl, current, name);
        }
    }

    /**
     * Adds a new LocalTOCItem to the local table of content
     *
     * @param level	the chapter-level of the item (e.g. "chapter 1" vs. "chapter
     * 1.1")
     * @param parent the parent TOCItem ("chapter 1" is parent of "chapter 1.1")
     * @param name the name of this TOCItem (/chapter-title)
     */
    private void addNewTOCItem(int level, LocalTOCItem parent, String name) {
        LocalTOCItem lti = new LocalTOCItem(level, parent);
        lti.name = name;

        if (parent == null) {
            if (root == null) {
                root = lti;
            } else {
                root.addChild(lti);
                parent = root;
            }
        } else {
            parent.addChild(lti);
        }
        currentItem = lti;
    }

    /**
     * Resets the parameters to start-values. Has to be called after each
     * parsing of a file, to clean up before the next run.
     */
    public void reset() {
        root = null;
        currentItem = null;
        titleOpen = false;
        currentLevel = -1;
    }

    /**
     * Local class for representing the "table of content".
     *
     */
    private class LocalTOCItem {

        LocalTOCItem parent = null;
        private java.util.Vector<LocalTOCItem> children;
        String name = null;
        int level = -1;

        public LocalTOCItem(int lvl, LocalTOCItem _parent) {
            level = lvl;
            parent = _parent;
            children = new java.util.Vector<LocalTOCItem>();
        }

        private LocalTOCItem getItem(String itemName) {
            LocalTOCItem res = null;

            if (itemName != null && children != null) {
                for (LocalTOCItem ti : children) {
                    if (ti != null && res == null && ti.getName().equals(itemName)) {
                        res = ti;
                    }
                }
            }

            return res;
        }

        public java.util.List<LocalTOCItem> getChildren() {
            return children;
        }

        public void addChild(LocalTOCItem child) {
            addTOCItem(child);
            child.parent = this;
        }

        private boolean addTOCItem(LocalTOCItem ti) {
            boolean res = false;

            if (ti != null && children != null && !children.contains(ti))//&& getItem(ti.getName())==null )
            {
                res = children.add(ti);
            }

            return res;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return getName();
        }
    }
}
