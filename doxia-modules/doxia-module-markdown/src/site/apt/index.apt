 -----
 doxia-module-markdown
 -----
 Vladimir Schneider <vladimir@vladsch.com>, Julien Nicoulaud <julien.nicoulaud@gmail.com>
 ------
 2017-03-04
 ------

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

doxia-module-markdown

  Markdown is a popular lightweight markup language, easy to read and easy to write.
  It is supported by a large panel of websites, text editors/IDEs and converter tools.
  Markdown format is supported both as source (parser) and destination (sink), the latter only since version 1.12.0.

* Metadata

  Although metadata was not part of the original Markdown format it is now widely supported through multiple extensions.
  This modules supports the following two metadata formats:

  * {{{http://fletcher.github.io/MultiMarkdown-5/metadata.html}MultiMarkdown Metadata}}

  * {{{https://github.com/vsch/flexmark-java/wiki/Extensions#yaml-front-matter}YAML front matter}}
  
  Metadata must be placed at the beginning of the document, before any other content.
  
  []

  Currently only the following metadata elements are supported:
  
  * <<<title>>> - the title of the document, used as the sink's title
  
  * <<<author>>> - the author of the document, used as the sink's author
  
  * <<<date>>> - the date of the document, used as the sink's date
  
  * everything else is just written as meta tags in the output document
  

* Extensions

  As the original Markdown specification is simple many extensions have been created to add features to the original Markdown format.
  The following extensions are supported by this module:
  
** GFM (GitHub Flavored Markdown) extensions:
  
  GitHub specified {{{https://github.github.com/gfm}extensions}} to the original Markdown format, which are now widely used. 
  Some of these extensions are also supported by this module.
  
  * {{{https://github.github.com/gfm/#tables-extension-}tables}}
  
  * {{{https://github.github.com/gfm/#strikethrough-extension-}strikethrough text}}

** Others

  * {{{https://github.com/vsch/flexmark-java/wiki/Extensions#wikilinks}wiki links}}
  
  * {{{https://github.com/vsch/flexmark-java/wiki/Footnotes-Extension}footnotes}}
  
  * {{{https://michelf.ca/projects/php-markdown/extra/#abbr}abbreviations}}
  
  * {{{https://michelf.ca/projects/php-markdown/extra/#def-list}definition lists}}
  
* Parser

  The parser will first convert Markdown into HTML and then parse the HTML into Doxia Sink API methods calls leveraging the 
  {{{../doxia-module-xhtml5/index.html}XHTML5 parser}}.

* References

   * {{{http://daringfireball.net/projects/markdown}Markdown project website}}

   * {{{http://en.wikipedia.org/wiki/Markdown}Markdown Wikipedia page}}

   * {{{http://xbeta.org/wiki/show/Markdown}Markdown wiki}}

   * {{{http://github.com/vsch/flexmark-java}flexmark-java}}, the library used by this Doxia module
     in {{{http://pegdown.org}Pegdown}} compatibility mode

   * {{{http://pegdown.org}Pegdown}} a deprecated Markdown processing library used in previous
     doxia-module-markdown versions 1.3 to 1.7
