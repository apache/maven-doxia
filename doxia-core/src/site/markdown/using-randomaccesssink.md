---
title: Using RandomAccessSink
author: 
  - Robert Scholte
date: 2011-01-07
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

# Using the RandomAccessSink

The RandomAccessSink is a Sink with the ability to add hooks. To demonstrate its usage we can have a look at a FML \(FAQ Markup Language\)-page. The simple structure of such a page can be like:

```unknown
  <faq id="1">
    <question/>
    <answer/>
  </faq>
  <faq id="2">
    <question/>
    <answer/>
  </faq>
```

Such structure would be parsed to the following page:

|   |
|:---|
|faq\[&quot;1&quot;\].question + link <br />faq\[&quot;2&quot;\].question + link |
|faq\[&quot;1&quot;\].question + anchor <br />faq\[&quot;1&quot;\].answer <br />faq\[&quot;2&quot;\].question + anchor <br />faq\[&quot;2&quot;\].answer |

With a Sink you can only append and there&apos;s no option to buffer. This would mean that you have to go twice over the structure: once for only the questions and once for the question + answer.

When using the RandomAccesSink we can prepare the structure of the page

```unknown
  RandomAccessSink randomAccessSink = new RandomAccessSink(sinkFactory, outputStream, encoding);
  Sink questions  = randomAccessSink.addSinkHook();
  Sink qAndA      = randomAccessSink.addSinkHook();
```

Now you can append the first question to both sinks, and append the answer to the second one. The same can be done with the second faq-entry. 

