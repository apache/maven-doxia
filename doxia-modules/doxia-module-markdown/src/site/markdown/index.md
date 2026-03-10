---
title: doxia-module-markdown
author: 
  - Vladimir Schneider _vladimir@vladsch.com_, Julien Nicoulaud _julien.nicoulaud@gmail.com_
date: 2017-03-04
---

<!-- Licensed to the Apache Software Foundation (ASF) under one-->
<!-- or more contributor license agreements.  See the NOTICE file-->
<!-- distributed with this work for additional information-->
<!-- regarding copyright ownership.  The ASF licenses this file-->
<!-- to you under the Apache License, Version 2.0 (the-->
<!-- "License"); you may not use this file except in compliance-->
<!-- with the License.  You may obtain a copy of the License at-->
<!---->
<!--   http://www.apache.org/licenses/LICENSE-2.0-->
<!---->
<!-- Unless required by applicable law or agreed to in writing,-->
<!-- software distributed under the License is distributed on an-->
<!-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY-->
<!-- KIND, either express or implied.  See the License for the-->
<!-- specific language governing permissions and limitations-->
<!-- under the License.-->
<!-- NOTE: For help with the syntax of this file, see:-->
<!-- http://maven.apache.org/doxia/references/apt-format.html-->

# doxia-module-markdown

Markdown is a popular lightweight markup language, easy to read and easy to write. It is supported by a large panel of websites, text editors/IDEs and converter tools. Markdown format is supported both as source \(parser\) and destination \(sink\), the latter only since version 1\.12\.0\.

## Metadata

Although metadata was not part of the original Markdown format it is now widely supported through multiple extensions. This modules supports the following two metadata formats:

- [MultiMarkdown Metadata](http://fletcher.github.io/MultiMarkdown-5/metadata.html)
- [YAML front matter](https://github.com/vsch/flexmark-java/wiki/Extensions#yaml-front-matter)

*Metadata must be placed at the beginning of the document, before any other content.*

Currently only the following metadata elements are supported:

- `title` - the title of the document, used as the sink&apos;s title
- `author` - the author of the document, used as the sink&apos;s author
- `date` - the date of the document, used as the sink&apos;s date
- everything else is just written as meta tags in the output document

## Extensions

As the original Markdown specification is simple many extensions have been created to add features to the original Markdown format. The following extensions are supported by this module:

### GFM \(GitHub Flavored Markdown\) extensions:

GitHub specified [extensions](https://github.github.com/gfm) to the original Markdown format, which are now widely used. Some of these extensions are also supported by this module.

- [tables](https://github.github.com/gfm/#tables-extension-)
- [strikethrough text](https://github.github.com/gfm/#strikethrough-extension-)

### Others

- [wiki links](https://github.com/vsch/flexmark-java/wiki/Extensions#wikilinks)
- [footnotes](https://github.com/vsch/flexmark-java/wiki/Footnotes-Extension)
- [abbreviations](https://michelf.ca/projects/php-markdown/extra/#abbr)
- [definition lists](https://michelf.ca/projects/php-markdown/extra/#def-list)
## Parser

The parser will first convert Markdown into HTML and then parse the HTML into Doxia Sink API methods calls leveraging the [XHTML5 parser](../doxia-module-xhtml5/index.html).

## References

- [Markdown project website](http://daringfireball.net/projects/markdown)
- [Markdown Wikipedia page](http://en.wikipedia.org/wiki/Markdown)
- [Markdown wiki](http://xbeta.org/wiki/show/Markdown)
- [flexmark-java](http://github.com/vsch/flexmark-java), the library used by this Doxia module in [Pegdown](http://pegdown.org) compatibility mode
- [Pegdown](http://pegdown.org) a deprecated Markdown processing library used in previous doxia-module-markdown versions 1\.3 to 1\.7
