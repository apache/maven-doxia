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
# Doxia XHTML5 Module

This parser and sink digests and emits sources compliant with the [XML syntax of HTML5](https://html.spec.whatwg.org/#the-xhtml-syntax).

## Special handling of anchors

Anchor ids/names need to follow a strict syntax in Doxia, therefore both anchor links as well as targets are automatically adjusted to comply with that syntax. Further details in [DoxiaUtils.encodeId(...)](../../doxia-core/apidocs/org/apache/maven/doxia/util/DoxiaUtils.html#encodeId-java.lang.String-).

In order to leave the links unprocessed use attribute `rel` with value `external` like this

```
<a href="testpage#specialanchor()" rel="external">...</a>
```

Otherwise the fragment `specialanchor()` would be converted as `(` and `)` are not valid in Doxia IDs.