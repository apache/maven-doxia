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


// Verify index.html

// File was produced
File resultFile = new File( basedir, "target/site/index.html" )
assert resultFile.isFile() : "index.html exists"


// Verify metadata.html

// File was produced
resultFile = new File( basedir, "target/site/metadata.html" )
assert resultFile.isFile() : "metadata.html exists"

// Check the content
String content = resultFile.text;

// <title> must contain the specified title in the metadata, not in the first heading
assert content =~ '<title>.*Title from Header.*</title>' : "title set from metadata"

// Author is Bertrand, yours truly
// Apostrophe must have been interpreted properly
assert content =~ '<meta name="Author" content="Bertrand \'Yours, Truly\' Martin" />' : "author is set"

// Keywords do support utf-8 smileys
assert content =~ '<meta name="Keywords" content="smile,&#x1f609;,utf-8" />' : "keywords are set"

// Meta are properly trimmed
assert content =~ '<meta name="Weird" content="Spacing" />' : "header spaces are trimmed"

// Empty is empty
assert content =~ '<meta name="Empty" content="" />' : "empty header can be set"

// No description is provided, as it was not part of the metadata at the beginning of the doc
assert !( content =~ '<meta name="description"' ) : "description is not set in the header"


// Verify quotes.html

resultFile = new File( basedir, "target/site/quotes.html" )
assert resultFile.isFile() : "quotes.html exists"

content = resultFile.text;
assert content =~ /This ain't a quote, but an apostrophe./ : "DOXIA-542: apostrophes remain intact"
assert content =~ /This &#x2018;quoted text&#x2019; isn't surrounded with apostrophes./ : "surrounding quotes are stylized"


// Verify DOXIA-473
content = new File( basedir, "target/site/DOXIA-473.html" ).text
assert !content.contains( ' quotes and double quotes were stripped' ) : "DOXIA-473: quotes are not stripped"


// Verify DOXIA-535
assert new File( basedir, "target/site/DOXIA-535.html" ).exists() : "DOXIA-535: *.markdown source files are processed"

// Verify DOXIA-571
content = new File( basedir, "target/site/DOXIA-571.html" ).text
assert content.contains( '<div class="source"><pre class="prettyprint"><code>code block' ) : "DOXIA-571: code block is pretty"


// Verify DOXIA-597
content = new File( basedir, "target/site/DOXIA-597.html" ).text
assert content.contains( '<code>monospaced</code> support' ) : "DOXIA-597: inline code is marked as such"


// Verify DOXIA-616-fenced-code-block.html

// File was produced
resultFile = new File( basedir, "target/site/DOXIA-616-fenced-code-block.html" )
assert resultFile.isFile() : "DOXIA-616-fenced-code-block.html exists"

// Check the content
content = resultFile.text;

// Our first fenced code block is <div class="source"><pre><code class="language-java">...</code></pre></div>
assert content =~ '<div class="source">.*<pre.*>.*<code.*class=".*language-java.*">.*// Fenced Code Block 1' : "DOXIA-616: Fenced Code Block 1 is pretty"

// Our second fenced code block doesn't specify a language
assert content =~ '<div class="source">.*<pre.*>.*<code.*># Fenced Code Block 2'
assert !( content =~ '<div class="source">.*<pre.*>.*<code.*language-.*># Fenced Code Block 2' ) : "DOXIA-616: Fenced Code Block 2 is pretty"

// Our third code block is indented, and it shows the same way
assert content =~ '<div class="source">.*<pre.*>.*<code.*>// Indented Code Block' : "DOXIA-616: Indented Code Block is pretty"

// Then we have inline code, which must be in simple <code>
assert content =~ 'inline code: <code>System.out.println' : "DOXIA-616: Inline code is marked as such"

// The last one is inline "fenced" code block which must be in simple <code>
assert content =~ 'And what about <code>System.out.println' : "DOXIA-616: Inline 'fenced' code block is marked as code"
