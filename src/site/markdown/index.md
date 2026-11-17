---
title: About
author: 
  - Hervé Boutemy
---

<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->


# Doxia

This is the base component of [Doxia](https://maven.apache.org/doxia/), the document engine behind Maven sites. It
contains the Sink API, the parsers and sinks for each markup format, and the built-in macros. Most users never call
it directly: the [Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/) uses it through
[Doxia Sitetools](https://maven.apache.org/doxia/doxia-sitetools/). For an introduction to the formats and how to use
them, start at the [Doxia home page](https://maven.apache.org/doxia/).

The component consists of the following modules.

| Module | Contents |
|---|---|
| [doxia-sink-api](./doxia-sink-api/) | The `Sink` interface that all parsers write to and all output formats implement. |
| [doxia-core](./doxia-core/) | The `Parser` API, base classes for parsers and sinks, and the `toc`, `snippet` and `echo` macros. |
| [doxia-module-markdown](./doxia-modules/doxia-module-markdown/) | Markdown parser and sink, based on [flexmark-java](https://github.com/vsch/flexmark-java). |
| [doxia-module-apt](./doxia-modules/doxia-module-apt/) | APT (Almost Plain Text) parser and sink. |
| [doxia-module-xdoc](./doxia-modules/doxia-module-xdoc/) | Xdoc parser and sink. |
| [doxia-module-xhtml5](./doxia-modules/doxia-module-xhtml5/) | XHTML5 parser and sink; the sink also writes the pages of a Maven site. |
| [doxia-module-fml](./doxia-modules/doxia-module-fml/) | FML (FAQ Markup Language) parser, deprecated since 2.2.0. |
| [doxia-test-docs](./doxia-test-docs/) | Sample documents shared by the module tests. |

The following diagram shows how the modules depend on each other.

<img src="images/doxia-deps.png" alt="Dependencies between the Doxia modules" usemap="#Doxia_dependencies" />
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
