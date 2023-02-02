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
package org.apache.maven.doxia.module.markdown;

import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterBlock;

public class YamlFrontMatterVisitor extends AbstractYamlFrontMatterVisitor {

    int endOffset = 0;

    @Override
    public void visit(YamlFrontMatterBlock node) {
        endOffset = node.getContentChars().getEndOffset();
        super.visit(node);
    }

    /**
     * @return the end of the YAML front matter metadata in the input source
     */
    public int getEndOffset() {
        return endOffset;
    }
}
