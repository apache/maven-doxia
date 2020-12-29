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

 // Verify fenced-code-block.html

// File was produced
File resultFile = new File(basedir, "target/site/fenced-code-block.html")
assert resultFile.isFile()

// Check the content
String content = resultFile.text;

// Our first fenced code block is <div class="source"><pre><code class="language-java">...</code></pre></div>
assert content =~ '<div class="source">.*<pre.*>.*<code.*class=".*language-java.*">.*// Fenced Code Block 1'

// Our second fenced code block doesn't specify a language
assert content =~ '<div class="source">.*<pre.*>.*<code.*># Fenced Code Block 2'
assert !(content =~ '<div class="source">.*<pre.*>.*<code.*language-.*># Fenced Code Block 2')

// Our third code block is indented, and it shows the same way
assert content =~ '<div class="source">.*<pre.*>.*<code.*>// Indented Code Block'

// Then we have inline code, which must be in simple <code>
assert content =~ 'inline code: <code>System.out.println'

// The last one is inline "fenced" code block which must be in simple <code>
assert content =~ 'And what about <code>System.out.println'
