---
title: About
author: 
  - Hervé Boutemy
---

<!--
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-->

# Doxia

Doxia is a content generation framework that provides powerful techniques for generating static and dynamic content, supporting [a variety of markup languages](/doxia/references/index.html).

The actual component is the base component from the whole Doxia suite, with the core parser and sink APIs and their implementation in supported markup languages.

It is used by the [Doxia Sitetools](../doxia-sitetools) extension, that adds site and documents support.

<img src="images/doxia-deps.png" border="0" usemap="#Doxia_dependencies" />
<map name="Doxia_dependencies">
  <area shape="rect" coords="0,280,122,308"  href="./doxia-test-docs/" />
  <area shape="rect" coords="135,231,286,261" href="./doxia-sink-api/" />
  <area shape="rect" coords="165,180,286,209" href="./doxia-core/" />
  <area shape="rect" coords="41,25,118,88"    href="./doxia-modules/doxia-module-apt/" />
  <area shape="rect" coords="212,75,289,107"   href="./doxia-modules/doxia-module-xdoc/" />
  <area shape="rect" coords="230,25,313,88"   href="./doxia-modules/doxia-module-xhtml5/" />
  <area shape="rect" coords="135,25,213,88"   href="./doxia-modules/doxia-module-fml/" />
  <area shape="rect" coords="304,75,421,107"  href="./doxia-modules/doxia-module-markdown/" />
  <area shape="rect" coords="21,1,432,151"     href="./doxia-modules/" />
  <area shape="rect" coords="308,178,439,208"  href="https://github.com/vsch/flexmark-java" />
  <area shape="rect" coords="164,280,286,310" href="https://codehaus-plexus.github.io/" />
</map>
