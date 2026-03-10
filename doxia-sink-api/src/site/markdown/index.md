 -----
 Doxia Sink API
 -----
 Hervé Boutemy
 -----
 2010-05-08
 -----

~~ Licensed to the Apache Software Foundation (ASF) under one
~~ or more contributor license agreements.  See the NOTICE file
~~ distributed with this work for additional information
~~ regarding copyright ownership.  The ASF licenses this file
~~ to you under the Apache License, Version 2.0 (the
~~ "License"); you may not use this file except in compliance
~~ with the License.  You may obtain a copy of the License at
~~
~~   http://www.apache.org/licenses/LICENSE-2.0
~~
~~ Unless required by applicable law or agreed to in writing,
~~ software distributed under the License is distributed on an
~~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~~ KIND, either express or implied.  See the License for the
~~ specific language governing permissions and limitations
~~ under the License.

~~ NOTE: For help with the syntax of this file, see:
~~ http://maven.apache.org/doxia/references/apt-format.html

Doxia Sink API

  The <Sink> interface is a generic markup language interface provided as a Java API.
It contains several methods that encapsulate common text syntax. A start tag is denoted by
<<<xxxx()>>> method and a end of tag by <<<xxxx_()>>> method.

  For instance, you could do things like:

+--
sink.paragraph();
sink.text("my text");
sink.paragraph_();
+--

  similar to this HTML markup:

+--
<p>my text</p>
+--

  To find out more about the Sink API, you can read the
  {{{./apidocs/org/apache/maven/doxia/sink/Sink.html}Javadoc}}.

