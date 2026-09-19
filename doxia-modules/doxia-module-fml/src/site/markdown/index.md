---
title: Doxia FML Module
author:
  - Sylwester Lachiewicz
date: 2026-09-19
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

# Doxia FML Module

## Deprecation notice

The FML format and this module are deprecated since Doxia 2.2.0 and will be removed in the
next major version. See [apache/maven-doxia#1101](https://github.com/apache/maven-doxia/issues/1101).

FML is a source-only format: an FAQ page is a list of questions and answers whose bodies are
already parsed as XDoc. The same page can be written in Markdown or XDoc without losing
anything, and [doxia-converter](https://maven.apache.org/doxia/doxia-tools/doxia-converter/)
converts existing `.fml` files:

```
java -jar doxia-converter-<version>-jar-with-dependencies.jar \
    -from fml -in src/site/fml/faq.fml -to markdown -out src/site/markdown/faq.md
```

Until it is removed, the module keeps working; `FmlParser` logs a warning for every FML
document it parses.

## Reference

- [FML format](https://maven.apache.org/doxia/references/fml-format.html)
- [Using the FML 1.0.1 schema](using-fml-xsd.html)
