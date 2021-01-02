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
File resultFile = new File(basedir, "target/site/index.html")
assert resultFile.isFile()


// Verify metadata.html

// File was produced
resultFile = new File(basedir, "target/site/metadata.html")
assert resultFile.isFile()

// Check the content
String content = resultFile.text;

// <title> must contain the specified title in the metadata, not in the first heading
assert content =~ '<title>.*Title from Header.*</title>'

// Author is Bertrand, yours truly
// Apostrophe must have been interpreted properly
assert content =~ '<meta name="Author" content="Bertrand \'Yours, Truly\' Martin" />'

// Keywords do support utf-8 smileys
assert content =~ '<meta name="Keywords" content="smile,&#x1f609;,utf-8" />'

// Meta are properly trimmed
assert content =~ '<meta name="Weird" content="Spacing" />'

// Empty is empty
assert content =~ '<meta name="Empty" content="" />'

// No description is provided, as it was not part of the metadata at the beginning of the doc
assert !(content =~ '<meta name="description"')

