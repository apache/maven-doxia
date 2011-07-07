Markdown test document
======================

This document aggregates all test documents of the Pegdown library, it was generated this way:

    git clone git://github.com/sirthias/pegdown.git
    for doc in pegdown/src/test/resources/**/*.md; do
      echo -e "\n\n$doc\n---\n"  >> test.md
      cat $doc                   >> test.md
    done


pegdown/src/test/resources/docs-php-markdown-todo/Email_auto_links.md
---

<michel.fortin@michelf.com>

International domain names: <help@tūdaliņ.lv>

pegdown/src/test/resources/docs-php-markdown-todo/Emphasis.md
---

Combined emphasis:

1.  ***test test***
2.  ___test test___
3.  *test **test***
4.  **test *test***
5.  ***test* test**
6.  ***test** test*
7.  ***test* test**
8.  **test *test***
9.  *test **test***
10. _test __test___
11. __test _test___
12. ___test_ test__
13. ___test__ test_
14. ___test_ test__
15. __test _test___
16. _test __test___


Incorrect nesting:

1.  *test  **test*  test**
2.  _test  __test_  test__
3.  **test  *test** test*
4.  __test  _test__ test_
5.  *test   *test*  test*
6.  _test   _test_  test_
7.  **test **test** test**
8.  __test __test__ test__



No emphasis:

1.  test*  test  *test
2.  test** test **test
3.  test_  test  _test
4.  test__ test __test



Middle-word emphasis (asterisks):

1.  *a*b
2.   a*b*
3.   a*b*c
4. **a**b
5.   a**b**
6.   a**b**c


Middle-word emphasis (underscore):

1.  _a_b
2.   a_b_
3.   a_b_c
4. __a__b
5.   a__b__
6.   a__b__c

my_precious_file.txt


## Tricky Cases

E**. **Test** TestTestTest

E**. **Test** Test Test Test


pegdown/src/test/resources/docs-php-markdown-todo/Inline_HTML_(Span).md
---

<abbr title="` **Attribute Content Is Not A Code Span** `">ACINACS</abbr>

<abbr title="`first backtick!">SB</abbr>
<abbr title="`second backtick!">SB</abbr>

pegdown/src/test/resources/docs-php-markdown-todo/Ins_and_del.md
---

Here is a block tag ins:

<ins>
<p>Some text</p>
</ins>

<ins>And here it is inside a paragraph.</ins>

And here it is <ins>in the middle of</ins> a paragraph.

<del>
<p>Some text</p>
</del>

<del>And here is ins as a paragraph.</del>

And here it is <del>in the middle of</del> a paragraph.


pegdown/src/test/resources/docs-php-markdown-todo/Links_inline_style.md
---

[silly URL w/ angle brackets](<?}]*+|&)>).


pegdown/src/test/resources/docs-php-markdown-todo/Nesting.md
---

Valid nesting:

**[Link](url)**

[**Link**](url)

**[**Link**](url)**


pegdown/src/test/resources/docs-php-markdown-todo/Parens_in_URL.md
---

[Inline link 1 with parens](/url\(test\) "title").

[Inline link 2 with parens](</url\(test\)> "title").

[Inline link 3 with non-escaped parens](/url(test) "title").

[Inline link 4 with non-escaped parens](</url(test)> "title").

[Reference link 1 with parens][1].

[Reference link 2 with parens][2].

  [1]: /url(test) "title"
  [2]: </url(test)> "title"


pegdown/src/test/resources/docs-pythonmarkdown2/auto_link.md
---

I can has autolink? <http://icanhascheeseburger.com>

Ask garfield: <garfield@example.com>


pegdown/src/test/resources/docs-pythonmarkdown2/auto_link_safe_mode.md
---

I can has autolink? <http://icanhascheeseburger.com>

Ask garfield: <garfield@example.com>


pegdown/src/test/resources/docs-pythonmarkdown2/basic_safe_mode_escape.md
---

blah <img src="dangerous"/> blah

<div>yowzer!</div>

blah


pegdown/src/test/resources/docs-pythonmarkdown2/basic_safe_mode.md
---

blah <img src="dangerous"/> blah

<div>yowzer!</div>

blah


pegdown/src/test/resources/docs-pythonmarkdown2/blockquote.md
---

[Trent wrote]
> no way

[Jeff wrote]
> way


pegdown/src/test/resources/docs-pythonmarkdown2/blockquote_with_pre.md
---

> Markdown indents blockquotes a couple of spaces
> necessitating some tweaks for pre-blocks in that
> blockquote:
>
>     here is a check
>     for that


pegdown/src/test/resources/docs-pythonmarkdown2/codeblock.md
---

    some code

some 'splaining

    some more code
    2 > 1



pegdown/src/test/resources/docs-pythonmarkdown2/code_block_with_tabs.md
---

Test with tabs for `_Detab`:

	Code	'block'	with	some	"tabs"	and	"quotes"


pegdown/src/test/resources/docs-pythonmarkdown2/code_safe_emphasis.md
---

This is *italic* and this is **bold**.
This is NOT _italic_ and this is __bold__ because --code-safe is turned on.


pegdown/src/test/resources/docs-pythonmarkdown2/codespans.md
---

`This` is a code span.
And ``This is one with an `embedded backtick` ``.


pegdown/src/test/resources/docs-pythonmarkdown2/codespans_safe_mode.md
---

`This` is a code span.
And ``This is one with an `embedded backtick` ``.


pegdown/src/test/resources/docs-pythonmarkdown2/emacs_head_vars.md
---

<!-- -*- markdown-extras: code-friendly -*- -->

This sentence talks about the Python __init__ method, which I'd rather not be
interpreted as Markdown's strong.


pegdown/src/test/resources/docs-pythonmarkdown2/emacs_tail_vars.md
---

This sentence talks about the Python __init__ method, which I'd rather not be
interpreted as Markdown's strong.

<!--
  Local Variables:
  markdown-extras: code-friendly
  End:
  -->


pegdown/src/test/resources/docs-pythonmarkdown2/emphasis.md
---

This is *italic* and this is **bold**.
This is also _italic_ and this is __bold__.


pegdown/src/test/resources/docs-pythonmarkdown2/escapes.md
---

\*\*don't shout\*\*

\*don't emphasize\*


pegdown/src/test/resources/docs-pythonmarkdown2/footnotes_letters.md
---

This is a para with a footnote.[^foo]

This is another para with a footnote[^hyphen-ated] in it. Actually it has two[^Capital] of
them.


[^foo]: Here is the body of the first footnote.

[^hyphen-ated]: And of the second footnote.

    This one has multiple paragraphs.

[^Capital]:
    Here is a footnote body that starts on next line.



pegdown/src/test/resources/docs-pythonmarkdown2/footnotes_markup.md
---

This is a para with a footnote.[^1]

This is another para with a footnote.[^2]

[^1]: And the **body** of the footnote has `markup`. For example,
    a [link to digg](http://digg.com). And some code:

        print "Hello, World!"

[^2]: This body has markup too, *but* doesn't end with a code block.


pegdown/src/test/resources/docs-pythonmarkdown2/footnotes.md
---

This is a para with a footnote.[^1]

This is another para with a footnote[^2] in it. Actually it has two[^3] of
them. No, three[^4].


[^1]: Here is the body of the first footnote.

[^2]: And of the second footnote.

    This one has multiple paragraphs.

[^3]:
    Here is a footnote body that starts on next line.

[^4]: quickie "that looks like a link ref if not careful"




pegdown/src/test/resources/docs-pythonmarkdown2/footnotes_safe_mode_escape.md
---

This is a para with a footnote.[^1]

[^1]: Here is the <em>body</em> of <span class="yo">the</span> footnote.

    <div class="blah">And here is the second para of the footnote.</div>


pegdown/src/test/resources/docs-pythonmarkdown2/hr.md
---

Dashes:

---

 ---

  ---

   ---

	---



pegdown/src/test/resources/docs-pythonmarkdown2/img_in_link.md
---

This example from
<http://orestis.gr/en/blog/2007/05/28/python-markdown-problems/>:

[![the google logo][logo]][google]
[logo]: http://www.google.com/images/logo.gif
[google]: http://www.google.com/ "click to visit Google.com"



pegdown/src/test/resources/docs-pythonmarkdown2/inline_links.md
---

an inline [link](/url/)

a [link "with" title](/url/ "title")

an inline ![image link](/url/)

an ![image "with" title](/url/ "title")


pegdown/src/test/resources/docs-pythonmarkdown2/issue2_safe_mode_borks_markup.md
---

## Heading 2

blah <script>alert('this should be removed')</script> **blah**

<script>alert('as should this')</script>



pegdown/src/test/resources/docs-pythonmarkdown2/link_defn_alt_title_delims.md
---

Alternative delimiters for [link definitions][link1] are allowed -- as of
Markdown 1.0.2, I think. Hence, [this link][link2] and [this link][link3] work
too.

[link1]: http://daringfireball.net/projects/markdown/syntax#link "link syntax"
[link2]: http://daringfireball.net/projects/markdown/syntax#link 'link syntax'
[link3]: http://daringfireball.net/projects/markdown/syntax#link (link syntax)


pegdown/src/test/resources/docs-pythonmarkdown2/link_patterns_double_hit.md
---

There once was a Mozilla bug 123 and a Komodo bug 123.


pegdown/src/test/resources/docs-pythonmarkdown2/link_patterns_edge_cases.md
---

Blah 123 becomes a line with two underscores.


pegdown/src/test/resources/docs-pythonmarkdown2/link_patterns.md
---

Recipe 123 and Komodo bug 234 are related.


pegdown/src/test/resources/docs-pythonmarkdown2/lists.md
---

count:

* one
* two
* three

count in spanish:

1. uno
2. dos
3. tres


pegdown/src/test/resources/docs-pythonmarkdown2/mismatched_footnotes.md
---

This is sentence has a footnote foo[^foo] and whamo[^whamo].

This is another para with a numbered footnote[^6].


[^foo]: Here is the body of the footnote foo.
[^bar]: Here is the body of the footnote bar.
[^6]: Here is the body of the footnote 6.



pegdown/src/test/resources/docs-pythonmarkdown2/missing_link_defn.md
---


This is a [missing link][missing] and a [used link][used].


[used]: http://foo.com
[unused]: http://foo.com



pegdown/src/test/resources/docs-pythonmarkdown2/nested_list.md
---

shopping list:

- veggies
    + carrots
    + lettuce
- fruits
    + oranges
    + apples
    + *peaches*


pegdown/src/test/resources/docs-pythonmarkdown2/nested_list_safe_mode.md
---

shopping list:

- veggies
    + carrots
    + lettuce
- fruits
    + oranges
    + apples
    + *peaches*


pegdown/src/test/resources/docs-pythonmarkdown2/parens_in_url_4.md
---

[Inline link 4 with non-escaped parens](</url(test)> "title").


pegdown/src/test/resources/docs-pythonmarkdown2/raw_html.md
---


Hi, <span foo="*bar*">*there*</span>. <!-- *blah* --> blah

<div>
    **ack**
</div>


pegdown/src/test/resources/docs-pythonmarkdown2/ref_links.md
---

[Google][] is fast ![star][].

[google]: http://www.google.com/
[star]: /img/star.png




pegdown/src/test/resources/docs-pythonmarkdown2/sublist-para.md
---

Some quick thoughts from a coder's perspective:

- The source will be available in a Mercurial ...

- Komodo is a Mozilla-based application...

  - Get a slightly tweaked mozilla build (C++, JavaScript, XUL).
  - Get a slightly tweaks Python build (C).
  - Add a bunch of core logic (Python)...
  - Add Komodo chrome (XUL, JavaScript, CSS, DTDs).

  What this means is that work on and add significant functionality...

- Komodo uses the same extension mechanisms as Firefox...

- Komodo builds and runs on Windows, Linux and ...


pegdown/src/test/resources/docs-pythonmarkdown2/syntax_color.md
---

Here is some sample code:

    :::python
    import sys
    def main(argv=sys.argv):
        logging.basicConfig()
        log.info('hi')

and:

    :::ruby
    use 'zlib'
    sub main(argv)
        puts 'hi'
    end


pegdown/src/test/resources/docs-pythonmarkdown2/underline_in_autolink.md
---

Eric wrote up a (long) intro to writing UDL definitions a while back on
his blog: <http://blogs.activestate.com/ericp/2007/01/kid_adding_a_ne.html>


pegdown/src/test/resources/MarkdownTest103/Amps and angle encoding.md
---

AT&T has an ampersand in their name.

AT&amp;T is another way to write it.

This & that.

4 < 5.

6 > 5.

Here's a [link] [1] with an ampersand in the URL.

Here's a link with an amersand in the link text: [AT&T] [2].

Here's an inline [link](/script?foo=1&bar=2).

Here's an inline [link](</script?foo=1&bar=2>).


[1]: http://example.com/?foo=1&bar=2
[2]: http://att.com/  "AT&T"


pegdown/src/test/resources/MarkdownTest103/Auto links.md
---

Link: <http://example.com/>.

With an ampersand: <http://example.com/?foo=1&bar=2>

* In a list?
* <http://example.com/>
* It should.

> Blockquoted: <http://example.com/>

Auto-links should not occur here: `<http://example.com/>`

	or here: <http://example.com/>

pegdown/src/test/resources/MarkdownTest103/Backslash escapes.md
---

These should all get escaped:

Backslash: \\

Backtick: \`

Asterisk: \*

Underscore: \_

Left brace: \{

Right brace: \}

Left bracket: \[

Right bracket: \]

Left paren: \(

Right paren: \)

Greater-than: \>

Hash: \#

Period: \.

Bang: \!

Plus: \+

Minus: \-



These should not, because they occur within a code block:

	Backslash: \\

	Backtick: \`

	Asterisk: \*

	Underscore: \_

	Left brace: \{

	Right brace: \}

	Left bracket: \[

	Right bracket: \]

	Left paren: \(

	Right paren: \)

	Greater-than: \>

	Hash: \#

	Period: \.

	Bang: \!

	Plus: \+

	Minus: \-


Nor should these, which occur in code spans:

Backslash: `\\`

Backtick: `` \` ``

Asterisk: `\*`

Underscore: `\_`

Left brace: `\{`

Right brace: `\}`

Left bracket: `\[`

Right bracket: `\]`

Left paren: `\(`

Right paren: `\)`

Greater-than: `\>`

Hash: `\#`

Period: `\.`

Bang: `\!`

Plus: `\+`

Minus: `\-`


These should get escaped, even though they're matching pairs for
other Markdown constructs:

\*asterisks\*

\_underscores\_

\`backticks\`

This is a code span with a literal backslash-backtick sequence: `` \` ``

This is a tag with unescaped backticks <span attr='`ticks`'>bar</span>.

This is a tag with backslashes <span attr='\\backslashes\\'>bar</span>.


pegdown/src/test/resources/MarkdownTest103/Blockquotes with code blocks.md
---

> Example:
>
>     sub status {
>         print "working";
>     }
>
> Or:
>
>     sub status {
>         return "working";
>     }


pegdown/src/test/resources/MarkdownTest103/Code Blocks.md
---

	code block on the first line

Regular text.

    code block indented by spaces

Regular text.

	the lines in this block
	all contain trailing spaces

Regular Text.

	code block on the last line

pegdown/src/test/resources/MarkdownTest103/Code Spans.md
---

`<test a="` content of attribute `">`

Fix for backticks within HTML tag: <span attr='`ticks`'>like this</span>

Here's how you put `` `backticks` `` in a code span.



pegdown/src/test/resources/MarkdownTest103/Hard-wrapped paragraphs with list-like lines.md
---

In Markdown 1.0.0 and earlier. Version
8. This line turns into a list item.
Because a hard-wrapped line in the
middle of a paragraph looked like a
list item.

Here's one with a bullet.
* criminey.


pegdown/src/test/resources/MarkdownTest103/Horizontal rules.md
---

Dashes:

---

 ---

  ---

   ---

	---

- - -

 - - -

  - - -

   - - -

	- - -


Asterisks:

***

 ***

  ***

   ***

	***

* * *

 * * *

  * * *

   * * *

	* * *


Underscores:

___

 ___

  ___

   ___

    ___

_ _ _

 _ _ _

  _ _ _

   _ _ _

    _ _ _


pegdown/src/test/resources/MarkdownTest103/Inline HTML (Advanced).md
---

Simple block on one line:

<div>foo</div>

And nested without indentation:

<div>
<div>
<div>
foo
</div>
<div style=">"/>
</div>
<div>bar</div>
</div>


pegdown/src/test/resources/MarkdownTest103/Inline HTML comments.md
---

Paragraph one.

<!-- This is a simple comment -->

<!--
	This is another comment.
-->

Paragraph two.

The end.


pegdown/src/test/resources/MarkdownTest103/Inline HTML (Simple).md
---

Here's a simple block:

<div>
	foo
</div>

This should be a code block, though:

	<div>
		foo
	</div>

As should this:

	<div>foo</div>

Now, nested:

<div>
	<div>
		<div>
			foo
		</div>
	</div>
</div>

This should just be an HTML comment:

<!-- Comment -->

Multiline:

<!--
Blah
Blah
-->

Code block:

	<!-- Comment -->

Just plain comment, with trailing spaces on the line:

<!-- foo -->

Code:

	<hr />

Hr's:

<hr/>

<hr />

<hr/>

<hr />

<hr class="foo" id="bar" />

<hr class="foo" id="bar"/>


pegdown/src/test/resources/MarkdownTest103/Links, inline style.md
---

Just a [URL](/url/).

[URL and title](/url/ "title").

[URL and title](/url/  "title preceded by two spaces").

[URL and title](/url/	"title preceded by a tab").

[URL and title](/url/ "title has spaces afterward"  ).


[Empty]().


pegdown/src/test/resources/MarkdownTest103/Links, reference style.md
---

Foo [bar] [1].

Foo [bar][1].

Foo [bar]
[1].

[1]: /url/  "Title"


With [embedded [brackets]] [b].


Indented [once][].

Indented [twice][].

Indented [thrice][].

Indented [four][] times.

 [once]: /url

  [twice]: /url

   [thrice]: /url

    [four]: /url


[b]: /url/

* * *

[this] [this] should work

So should [this][this].

And [this] [].

And [this][].

And [this].

But not [that] [].

Nor [that][].

Nor [that].

[Something in brackets like [this][] should work]

[Same with [this].]

In this case, [this](/somethingelse/) points to something else.

Backslashing should suppress \[this] and [this\].

[this]: foo


* * *

Here's one where the [link
breaks] across lines.

Here's another where the [link
breaks] across lines, but with a line-ending space.


[link breaks]: /url/


pegdown/src/test/resources/MarkdownTest103/Links, shortcut references.md
---

This is the [simple case].

[simple case]: /simple



This one has a [line
break].

This one has a [line
break] with a line-ending space.

[line break]: /foo


[this] [that] and the [other]

[this]: /this
[that]: /that
[other]: /other


pegdown/src/test/resources/MarkdownTest103/Literal quotes in titles.md
---

Foo [bar][].

Foo [bar](/url/ "Title with "quotes" inside").


  [bar]: /url/ "Title with "quotes" inside"



pegdown/src/test/resources/MarkdownTest103/Markdown Documentation - Basics.md
---

Markdown: Basics
================

<ul id="ProjectSubmenu">
    <li><a href="/projects/markdown/" title="Markdown Project Page">Main</a></li>
    <li><a class="selected" title="Markdown Basics">Basics</a></li>
    <li><a href="/projects/markdown/syntax" title="Markdown Syntax Documentation">Syntax</a></li>
    <li><a href="/projects/markdown/license" title="Pricing and License Information">License</a></li>
    <li><a href="/projects/markdown/dingus" title="Online Markdown Web Form">Dingus</a></li>
</ul>


Getting the Gist of Markdown's Formatting Syntax
------------------------------------------------

This page offers a brief overview of what it's like to use Markdown.
The [syntax page] [s] provides complete, detailed documentation for
every feature, but Markdown should be very easy to pick up simply by
looking at a few examples of it in action. The examples on this page
are written in a before/after style, showing example syntax and the
HTML output produced by Markdown.

It's also helpful to simply try Markdown out; the [Dingus] [d] is a
web application that allows you type your own Markdown-formatted text
and translate it to XHTML.

**Note:** This document is itself written using Markdown; you
can [see the source for it by adding '.text' to the URL] [src].

  [s]: /projects/markdown/syntax  "Markdown Syntax"
  [d]: /projects/markdown/dingus  "Markdown Dingus"
  [src]: /projects/markdown/basics.text


## Paragraphs, Headers, Blockquotes ##

A paragraph is simply one or more consecutive lines of text, separated
by one or more blank lines. (A blank line is any line that looks like a
blank line -- a line containing nothing spaces or tabs is considered
blank.) Normal paragraphs should not be intended with spaces or tabs.

Markdown offers two styles of headers: *Setext* and *atx*.
Setext-style headers for `<h1>` and `<h2>` are created by
"underlining" with equal signs (`=`) and hyphens (`-`), respectively.
To create an atx-style header, you put 1-6 hash marks (`#`) at the
beginning of the line -- the number of hashes equals the resulting
HTML header level.

Blockquotes are indicated using email-style '`>`' angle brackets.

Markdown:

    A First Level Header
    ====================

    A Second Level Header
    ---------------------

    Now is the time for all good men to come to
    the aid of their country. This is just a
    regular paragraph.

    The quick brown fox jumped over the lazy
    dog's back.

    ### Header 3

    > This is a blockquote.
    >
    > This is the second paragraph in the blockquote.
    >
    > ## This is an H2 in a blockquote


Output:

    <h1>A First Level Header</h1>

    <h2>A Second Level Header</h2>

    <p>Now is the time for all good men to come to
    the aid of their country. This is just a
    regular paragraph.</p>

    <p>The quick brown fox jumped over the lazy
    dog's back.</p>

    <h3>Header 3</h3>

    <blockquote>
        <p>This is a blockquote.</p>

        <p>This is the second paragraph in the blockquote.</p>

        <h2>This is an H2 in a blockquote</h2>
    </blockquote>



### Phrase Emphasis ###

Markdown uses asterisks and underscores to indicate spans of emphasis.

Markdown:

    Some of these words *are emphasized*.
    Some of these words _are emphasized also_.

    Use two asterisks for **strong emphasis**.
    Or, if you prefer, __use two underscores instead__.

Output:

    <p>Some of these words <em>are emphasized</em>.
    Some of these words <em>are emphasized also</em>.</p>

    <p>Use two asterisks for <strong>strong emphasis</strong>.
    Or, if you prefer, <strong>use two underscores instead</strong>.</p>



## Lists ##

Unordered (bulleted) lists use asterisks, pluses, and hyphens (`*`,
`+`, and `-`) as list markers. These three markers are
interchangable; this:

    *   Candy.
    *   Gum.
    *   Booze.

this:

    +   Candy.
    +   Gum.
    +   Booze.

and this:

    -   Candy.
    -   Gum.
    -   Booze.

all produce the same output:

    <ul>
    <li>Candy.</li>
    <li>Gum.</li>
    <li>Booze.</li>
    </ul>

Ordered (numbered) lists use regular numbers, followed by periods, as
list markers:

    1.  Red
    2.  Green
    3.  Blue

Output:

    <ol>
    <li>Red</li>
    <li>Green</li>
    <li>Blue</li>
    </ol>

If you put blank lines between items, you'll get `<p>` tags for the
list item text. You can create multi-paragraph list items by indenting
the paragraphs by 4 spaces or 1 tab:

    *   A list item.

        With multiple paragraphs.

    *   Another item in the list.

Output:

    <ul>
    <li><p>A list item.</p>
    <p>With multiple paragraphs.</p></li>
    <li><p>Another item in the list.</p></li>
    </ul>



### Links ###

Markdown supports two styles for creating links: *inline* and
*reference*. With both styles, you use square brackets to delimit the
text you want to turn into a link.

Inline-style links use parentheses immediately after the link text.
For example:

    This is an [example link](http://example.com/).

Output:

    <p>This is an <a href="http://example.com/">
    example link</a>.</p>

Optionally, you may include a title attribute in the parentheses:

    This is an [example link](http://example.com/ "With a Title").

Output:

    <p>This is an <a href="http://example.com/" title="With a Title">
    example link</a>.</p>

Reference-style links allow you to refer to your links by names, which
you define elsewhere in your document:

    I get 10 times more traffic from [Google][1] than from
    [Yahoo][2] or [MSN][3].

    [1]: http://google.com/        "Google"
    [2]: http://search.yahoo.com/  "Yahoo Search"
    [3]: http://search.msn.com/    "MSN Search"

Output:

    <p>I get 10 times more traffic from <a href="http://google.com/"
    title="Google">Google</a> than from <a href="http://search.yahoo.com/"
    title="Yahoo Search">Yahoo</a> or <a href="http://search.msn.com/"
    title="MSN Search">MSN</a>.</p>

The title attribute is optional. Link names may contain letters,
numbers and spaces, but are *not* case sensitive:

    I start my morning with a cup of coffee and
    [The New York Times][NY Times].

    [ny times]: http://www.nytimes.com/

Output:

    <p>I start my morning with a cup of coffee and
    <a href="http://www.nytimes.com/">The New York Times</a>.</p>


### Images ###

Image syntax is very much like link syntax.

Inline (titles are optional):

    ![alt text](/path/to/img.jpg "Title")

Reference-style:

    ![alt text][id]

    [id]: /path/to/img.jpg "Title"

Both of the above examples produce the same output:

    <img src="/path/to/img.jpg" alt="alt text" title="Title" />



### Code ###

In a regular paragraph, you can create code span by wrapping text in
backtick quotes. Any ampersands (`&`) and angle brackets (`<` or
`>`) will automatically be translated into HTML entities. This makes
it easy to use Markdown to write about HTML example code:

    I strongly recommend against using any `<blink>` tags.

    I wish SmartyPants used named entities like `&mdash;`
    instead of decimal-encoded entites like `&#8212;`.

Output:

    <p>I strongly recommend against using any
    <code>&lt;blink&gt;</code> tags.</p>

    <p>I wish SmartyPants used named entities like
    <code>&amp;mdash;</code> instead of decimal-encoded
    entites like <code>&amp;#8212;</code>.</p>


To specify an entire block of pre-formatted code, indent every line of
the block by 4 spaces or 1 tab. Just like with code spans, `&`, `<`,
and `>` characters will be escaped automatically.

Markdown:

    If you want your page to validate under XHTML 1.0 Strict,
    you've got to put paragraph tags in your blockquotes:

        <blockquote>
            <p>For example.</p>
        </blockquote>

Output:

    <p>If you want your page to validate under XHTML 1.0 Strict,
    you've got to put paragraph tags in your blockquotes:</p>

    <pre><code>&lt;blockquote&gt;
        &lt;p&gt;For example.&lt;/p&gt;
    &lt;/blockquote&gt;
    </code></pre>


pegdown/src/test/resources/MarkdownTest103/Markdown Documentation - Syntax.md
---

Markdown: Syntax
================

<ul id="ProjectSubmenu">
    <li><a href="/projects/markdown/" title="Markdown Project Page">Main</a></li>
    <li><a href="/projects/markdown/basics" title="Markdown Basics">Basics</a></li>
    <li><a class="selected" title="Markdown Syntax Documentation">Syntax</a></li>
    <li><a href="/projects/markdown/license" title="Pricing and License Information">License</a></li>
    <li><a href="/projects/markdown/dingus" title="Online Markdown Web Form">Dingus</a></li>
</ul>


*   [Overview](#overview)
    *   [Philosophy](#philosophy)
    *   [Inline HTML](#html)
    *   [Automatic Escaping for Special Characters](#autoescape)
*   [Block Elements](#block)
    *   [Paragraphs and Line Breaks](#p)
    *   [Headers](#header)
    *   [Blockquotes](#blockquote)
    *   [Lists](#list)
    *   [Code Blocks](#precode)
    *   [Horizontal Rules](#hr)
*   [Span Elements](#span)
    *   [Links](#link)
    *   [Emphasis](#em)
    *   [Code](#code)
    *   [Images](#img)
*   [Miscellaneous](#misc)
    *   [Backslash Escapes](#backslash)
    *   [Automatic Links](#autolink)


**Note:** This document is itself written using Markdown; you
can [see the source for it by adding '.text' to the URL][src].

  [src]: /projects/markdown/syntax.text

* * *

<h2 id="overview">Overview</h2>

<h3 id="philosophy">Philosophy</h3>

Markdown is intended to be as easy-to-read and easy-to-write as is feasible.

Readability, however, is emphasized above all else. A Markdown-formatted
document should be publishable as-is, as plain text, without looking
like it's been marked up with tags or formatting instructions. While
Markdown's syntax has been influenced by several existing text-to-HTML
filters -- including [Setext] [1], [atx] [2], [Textile] [3], [reStructuredText] [4],
[Grutatext] [5], and [EtText] [6] -- the single biggest source of
inspiration for Markdown's syntax is the format of plain text email.

  [1]: http://docutils.sourceforge.net/mirror/setext.html
  [2]: http://www.aaronsw.com/2002/atx/
  [3]: http://textism.com/tools/textile/
  [4]: http://docutils.sourceforge.net/rst.html
  [5]: http://www.triptico.com/software/grutatxt.html
  [6]: http://ettext.taint.org/doc/

To this end, Markdown's syntax is comprised entirely of punctuation
characters, which punctuation characters have been carefully chosen so
as to look like what they mean. E.g., asterisks around a word actually
look like \*emphasis\*. Markdown lists look like, well, lists. Even
blockquotes look like quoted passages of text, assuming you've ever
used email.



<h3 id="html">Inline HTML</h3>

Markdown's syntax is intended for one purpose: to be used as a
format for *writing* for the web.

Markdown is not a replacement for HTML, or even close to it. Its
syntax is very small, corresponding only to a very small subset of
HTML tags. The idea is *not* to create a syntax that makes it easier
to insert HTML tags. In my opinion, HTML tags are already easy to
insert. The idea for Markdown is to make it easy to read, write, and
edit prose. HTML is a *publishing* format; Markdown is a *writing*
format. Thus, Markdown's formatting syntax only addresses issues that
can be conveyed in plain text.

For any markup that is not covered by Markdown's syntax, you simply
use HTML itself. There's no need to preface it or delimit it to
indicate that you're switching from Markdown to HTML; you just use
the tags.

The only restrictions are that block-level HTML elements -- e.g. `<div>`,
`<table>`, `<pre>`, `<p>`, etc. -- must be separated from surrounding
content by blank lines, and the start and end tags of the block should
not be indented with tabs or spaces. Markdown is smart enough not
to add extra (unwanted) `<p>` tags around HTML block-level tags.

For example, to add an HTML table to a Markdown article:

    This is a regular paragraph.

    <table>
        <tr>
            <td>Foo</td>
        </tr>
    </table>

    This is another regular paragraph.

Note that Markdown formatting syntax is not processed within block-level
HTML tags. E.g., you can't use Markdown-style `*emphasis*` inside an
HTML block.

Span-level HTML tags -- e.g. `<span>`, `<cite>`, or `<del>` -- can be
used anywhere in a Markdown paragraph, list item, or header. If you
want, you can even use HTML tags instead of Markdown formatting; e.g. if
you'd prefer to use HTML `<a>` or `<img>` tags instead of Markdown's
link or image syntax, go right ahead.

Unlike block-level HTML tags, Markdown syntax *is* processed within
span-level tags.


<h3 id="autoescape">Automatic Escaping for Special Characters</h3>

In HTML, there are two characters that demand special treatment: `<`
and `&`. Left angle brackets are used to start tags; ampersands are
used to denote HTML entities. If you want to use them as literal
characters, you must escape them as entities, e.g. `&lt;`, and
`&amp;`.

Ampersands in particular are bedeviling for web writers. If you want to
write about 'AT&T', you need to write '`AT&amp;T`'. You even need to
escape ampersands within URLs. Thus, if you want to link to:

    http://images.google.com/images?num=30&q=larry+bird

you need to encode the URL as:

    http://images.google.com/images?num=30&amp;q=larry+bird

in your anchor tag `href` attribute. Needless to say, this is easy to
forget, and is probably the single most common source of HTML validation
errors in otherwise well-marked-up web sites.

Markdown allows you to use these characters naturally, taking care of
all the necessary escaping for you. If you use an ampersand as part of
an HTML entity, it remains unchanged; otherwise it will be translated
into `&amp;`.

So, if you want to include a copyright symbol in your article, you can write:

    &copy;

and Markdown will leave it alone. But if you write:

    AT&T

Markdown will translate it to:

    AT&amp;T

Similarly, because Markdown supports [inline HTML](#html), if you use
angle brackets as delimiters for HTML tags, Markdown will treat them as
such. But if you write:

    4 < 5

Markdown will translate it to:

    4 &lt; 5

However, inside Markdown code spans and blocks, angle brackets and
ampersands are *always* encoded automatically. This makes it easy to use
Markdown to write about HTML code. (As opposed to raw HTML, which is a
terrible format for writing about HTML syntax, because every single `<`
and `&` in your example code needs to be escaped.)


* * *


<h2 id="block">Block Elements</h2>


<h3 id="p">Paragraphs and Line Breaks</h3>

A paragraph is simply one or more consecutive lines of text, separated
by one or more blank lines. (A blank line is any line that looks like a
blank line -- a line containing nothing but spaces or tabs is considered
blank.) Normal paragraphs should not be intended with spaces or tabs.

The implication of the "one or more consecutive lines of text" rule is
that Markdown supports "hard-wrapped" text paragraphs. This differs
significantly from most other text-to-HTML formatters (including Movable
Type's "Convert Line Breaks" option) which translate every line break
character in a paragraph into a `<br />` tag.

When you *do* want to insert a `<br />` break tag using Markdown, you
end a line with two or more spaces, then type return.

Yes, this takes a tad more effort to create a `<br />`, but a simplistic
"every line break is a `<br />`" rule wouldn't work for Markdown.
Markdown's email-style [blockquoting][bq] and multi-paragraph [list items][l]
work best -- and look better -- when you format them with hard breaks.

  [bq]: #blockquote
  [l]:  #list



<h3 id="header">Headers</h3>

Markdown supports two styles of headers, [Setext] [1] and [atx] [2].

Setext-style headers are "underlined" using equal signs (for first-level
headers) and dashes (for second-level headers). For example:

    This is an H1
    =============

    This is an H2
    -------------

Any number of underlining `=`'s or `-`'s will work.

Atx-style headers use 1-6 hash characters at the start of the line,
corresponding to header levels 1-6. For example:

    # This is an H1

    ## This is an H2

    ###### This is an H6

Optionally, you may "close" atx-style headers. This is purely
cosmetic -- you can use this if you think it looks better. The
closing hashes don't even need to match the number of hashes
used to open the header. (The number of opening hashes
determines the header level.) :

    # This is an H1 #

    ## This is an H2 ##

    ### This is an H3 ######


<h3 id="blockquote">Blockquotes</h3>

Markdown uses email-style `>` characters for blockquoting. If you're
familiar with quoting passages of text in an email message, then you
know how to create a blockquote in Markdown. It looks best if you hard
wrap the text and put a `>` before every line:

    > This is a blockquote with two paragraphs. Lorem ipsum dolor sit amet,
    > consectetuer adipiscing elit. Aliquam hendrerit mi posuere lectus.
    > Vestibulum enim wisi, viverra nec, fringilla in, laoreet vitae, risus.
    >
    > Donec sit amet nisl. Aliquam semper ipsum sit amet velit. Suspendisse
    > id sem consectetuer libero luctus adipiscing.

Markdown allows you to be lazy and only put the `>` before the first
line of a hard-wrapped paragraph:

    > This is a blockquote with two paragraphs. Lorem ipsum dolor sit amet,
    consectetuer adipiscing elit. Aliquam hendrerit mi posuere lectus.
    Vestibulum enim wisi, viverra nec, fringilla in, laoreet vitae, risus.

    > Donec sit amet nisl. Aliquam semper ipsum sit amet velit. Suspendisse
    id sem consectetuer libero luctus adipiscing.

Blockquotes can be nested (i.e. a blockquote-in-a-blockquote) by
adding additional levels of `>`:

    > This is the first level of quoting.
    >
    > > This is nested blockquote.
    >
    > Back to the first level.

Blockquotes can contain other Markdown elements, including headers, lists,
and code blocks:

	> ## This is a header.
	>
	> 1.   This is the first list item.
	> 2.   This is the second list item.
	>
	> Here's some example code:
	>
	>     return shell_exec("echo $input | $markdown_script");

Any decent text editor should make email-style quoting easy. For
example, with BBEdit, you can make a selection and choose Increase
Quote Level from the Text menu.


<h3 id="list">Lists</h3>

Markdown supports ordered (numbered) and unordered (bulleted) lists.

Unordered lists use asterisks, pluses, and hyphens -- interchangably
-- as list markers:

    *   Red
    *   Green
    *   Blue

is equivalent to:

    +   Red
    +   Green
    +   Blue

and:

    -   Red
    -   Green
    -   Blue

Ordered lists use numbers followed by periods:

    1.  Bird
    2.  McHale
    3.  Parish

It's important to note that the actual numbers you use to mark the
list have no effect on the HTML output Markdown produces. The HTML
Markdown produces from the above list is:

    <ol>
    <li>Bird</li>
    <li>McHale</li>
    <li>Parish</li>
    </ol>

If you instead wrote the list in Markdown like this:

    1.  Bird
    1.  McHale
    1.  Parish

or even:

    3. Bird
    1. McHale
    8. Parish

you'd get the exact same HTML output. The point is, if you want to,
you can use ordinal numbers in your ordered Markdown lists, so that
the numbers in your source match the numbers in your published HTML.
But if you want to be lazy, you don't have to.

If you do use lazy list numbering, however, you should still start the
list with the number 1. At some point in the future, Markdown may support
starting ordered lists at an arbitrary number.

List markers typically start at the left margin, but may be indented by
up to three spaces. List markers must be followed by one or more spaces
or a tab.

To make lists look nice, you can wrap items with hanging indents:

    *   Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
        Aliquam hendrerit mi posuere lectus. Vestibulum enim wisi,
        viverra nec, fringilla in, laoreet vitae, risus.
    *   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
        Suspendisse id sem consectetuer libero luctus adipiscing.

But if you want to be lazy, you don't have to:

    *   Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
    Aliquam hendrerit mi posuere lectus. Vestibulum enim wisi,
    viverra nec, fringilla in, laoreet vitae, risus.
    *   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
    Suspendisse id sem consectetuer libero luctus adipiscing.

If list items are separated by blank lines, Markdown will wrap the
items in `<p>` tags in the HTML output. For example, this input:

    *   Bird
    *   Magic

will turn into:

    <ul>
    <li>Bird</li>
    <li>Magic</li>
    </ul>

But this:

    *   Bird

    *   Magic

will turn into:

    <ul>
    <li><p>Bird</p></li>
    <li><p>Magic</p></li>
    </ul>

List items may consist of multiple paragraphs. Each subsequent
paragraph in a list item must be intended by either 4 spaces
or one tab:

    1.  This is a list item with two paragraphs. Lorem ipsum dolor
        sit amet, consectetuer adipiscing elit. Aliquam hendrerit
        mi posuere lectus.

        Vestibulum enim wisi, viverra nec, fringilla in, laoreet
        vitae, risus. Donec sit amet nisl. Aliquam semper ipsum
        sit amet velit.

    2.  Suspendisse id sem consectetuer libero luctus adipiscing.

It looks nice if you indent every line of the subsequent
paragraphs, but here again, Markdown will allow you to be
lazy:

    *   This is a list item with two paragraphs.

        This is the second paragraph in the list item. You're
    only required to indent the first line. Lorem ipsum dolor
    sit amet, consectetuer adipiscing elit.

    *   Another item in the same list.

To put a blockquote within a list item, the blockquote's `>`
delimiters need to be indented:

    *   A list item with a blockquote:

        > This is a blockquote
        > inside a list item.

To put a code block within a list item, the code block needs
to be indented *twice* -- 8 spaces or two tabs:

    *   A list item with a code block:

            <code goes here>


It's worth noting that it's possible to trigger an ordered list by
accident, by writing something like this:

    1986. What a great season.

In other words, a *number-period-space* sequence at the beginning of a
line. To avoid this, you can backslash-escape the period:

    1986\. What a great season.



<h3 id="precode">Code Blocks</h3>

Pre-formatted code blocks are used for writing about programming or
markup source code. Rather than forming normal paragraphs, the lines
of a code block are interpreted literally. Markdown wraps a code block
in both `<pre>` and `<code>` tags.

To produce a code block in Markdown, simply indent every line of the
block by at least 4 spaces or 1 tab. For example, given this input:

    This is a normal paragraph:

        This is a code block.

Markdown will generate:

    <p>This is a normal paragraph:</p>

    <pre><code>This is a code block.
    </code></pre>

One level of indentation -- 4 spaces or 1 tab -- is removed from each
line of the code block. For example, this:

    Here is an example of AppleScript:

        tell application "Foo"
            beep
        end tell

will turn into:

    <p>Here is an example of AppleScript:</p>

    <pre><code>tell application "Foo"
        beep
    end tell
    </code></pre>

A code block continues until it reaches a line that is not indented
(or the end of the article).

Within a code block, ampersands (`&`) and angle brackets (`<` and `>`)
are automatically converted into HTML entities. This makes it very
easy to include example HTML source code using Markdown -- just paste
it and indent it, and Markdown will handle the hassle of encoding the
ampersands and angle brackets. For example, this:

        <div class="footer">
            &copy; 2004 Foo Corporation
        </div>

will turn into:

    <pre><code>&lt;div class="footer"&gt;
        &amp;copy; 2004 Foo Corporation
    &lt;/div&gt;
    </code></pre>

Regular Markdown syntax is not processed within code blocks. E.g.,
asterisks are just literal asterisks within a code block. This means
it's also easy to use Markdown to write about Markdown's own syntax.



<h3 id="hr">Horizontal Rules</h3>

You can produce a horizontal rule tag (`<hr />`) by placing three or
more hyphens, asterisks, or underscores on a line by themselves. If you
wish, you may use spaces between the hyphens or asterisks. Each of the
following lines will produce a horizontal rule:

    * * *

    ***

    *****

    - - -

    ---------------------------------------

	_ _ _


* * *

<h2 id="span">Span Elements</h2>

<h3 id="link">Links</h3>

Markdown supports two style of links: *inline* and *reference*.

In both styles, the link text is delimited by [square brackets].

To create an inline link, use a set of regular parentheses immediately
after the link text's closing square bracket. Inside the parentheses,
put the URL where you want the link to point, along with an *optional*
title for the link, surrounded in quotes. For example:

    This is [an example](http://example.com/ "Title") inline link.

    [This link](http://example.net/) has no title attribute.

Will produce:

    <p>This is <a href="http://example.com/" title="Title">
    an example</a> inline link.</p>

    <p><a href="http://example.net/">This link</a> has no
    title attribute.</p>

If you're referring to a local resource on the same server, you can
use relative paths:

    See my [About](/about/) page for details.

Reference-style links use a second set of square brackets, inside
which you place a label of your choosing to identify the link:

    This is [an example][id] reference-style link.

You can optionally use a space to separate the sets of brackets:

    This is [an example] [id] reference-style link.

Then, anywhere in the document, you define your link label like this,
on a line by itself:

    [id]: http://example.com/  "Optional Title Here"

That is:

*   Square brackets containing the link identifier (optionally
    indented from the left margin using up to three spaces);
*   followed by a colon;
*   followed by one or more spaces (or tabs);
*   followed by the URL for the link;
*   optionally followed by a title attribute for the link, enclosed
    in double or single quotes.

The link URL may, optionally, be surrounded by angle brackets:

    [id]: <http://example.com/>  "Optional Title Here"

You can put the title attribute on the next line and use extra spaces
or tabs for padding, which tends to look better with longer URLs:

    [id]: http://example.com/longish/path/to/resource/here
        "Optional Title Here"

Link definitions are only used for creating links during Markdown
processing, and are stripped from your document in the HTML output.

Link definition names may constist of letters, numbers, spaces, and punctuation -- but they are *not* case sensitive. E.g. these two links:

	[link text][a]
	[link text][A]

are equivalent.

The *implicit link name* shortcut allows you to omit the name of the
link, in which case the link text itself is used as the name.
Just use an empty set of square brackets -- e.g., to link the word
"Google" to the google.com web site, you could simply write:

	[Google][]

And then define the link:

	[Google]: http://google.com/

Because link names may contain spaces, this shortcut even works for
multiple words in the link text:

	Visit [Daring Fireball][] for more information.

And then define the link:

	[Daring Fireball]: http://daringfireball.net/

Link definitions can be placed anywhere in your Markdown document. I
tend to put them immediately after each paragraph in which they're
used, but if you want, you can put them all at the end of your
document, sort of like footnotes.

Here's an example of reference links in action:

    I get 10 times more traffic from [Google] [1] than from
    [Yahoo] [2] or [MSN] [3].

      [1]: http://google.com/        "Google"
      [2]: http://search.yahoo.com/  "Yahoo Search"
      [3]: http://search.msn.com/    "MSN Search"

Using the implicit link name shortcut, you could instead write:

    I get 10 times more traffic from [Google][] than from
    [Yahoo][] or [MSN][].

      [google]: http://google.com/        "Google"
      [yahoo]:  http://search.yahoo.com/  "Yahoo Search"
      [msn]:    http://search.msn.com/    "MSN Search"

Both of the above examples will produce the following HTML output:

    <p>I get 10 times more traffic from <a href="http://google.com/"
    title="Google">Google</a> than from
    <a href="http://search.yahoo.com/" title="Yahoo Search">Yahoo</a>
    or <a href="http://search.msn.com/" title="MSN Search">MSN</a>.</p>

For comparison, here is the same paragraph written using
Markdown's inline link style:

    I get 10 times more traffic from [Google](http://google.com/ "Google")
    than from [Yahoo](http://search.yahoo.com/ "Yahoo Search") or
    [MSN](http://search.msn.com/ "MSN Search").

The point of reference-style links is not that they're easier to
write. The point is that with reference-style links, your document
source is vastly more readable. Compare the above examples: using
reference-style links, the paragraph itself is only 81 characters
long; with inline-style links, it's 176 characters; and as raw HTML,
it's 234 characters. In the raw HTML, there's more markup than there
is text.

With Markdown's reference-style links, a source document much more
closely resembles the final output, as rendered in a browser. By
allowing you to move the markup-related metadata out of the paragraph,
you can add links without interrupting the narrative flow of your
prose.


<h3 id="em">Emphasis</h3>

Markdown treats asterisks (`*`) and underscores (`_`) as indicators of
emphasis. Text wrapped with one `*` or `_` will be wrapped with an
HTML `<em>` tag; double `*`'s or `_`'s will be wrapped with an HTML
`<strong>` tag. E.g., this input:

    *single asterisks*

    _single underscores_

    **double asterisks**

    __double underscores__

will produce:

    <em>single asterisks</em>

    <em>single underscores</em>

    <strong>double asterisks</strong>

    <strong>double underscores</strong>

You can use whichever style you prefer; the lone restriction is that
the same character must be used to open and close an emphasis span.

Emphasis can be used in the middle of a word:

    un*fucking*believable

But if you surround an `*` or `_` with spaces, it'll be treated as a
literal asterisk or underscore.

To produce a literal asterisk or underscore at a position where it
would otherwise be used as an emphasis delimiter, you can backslash
escape it:

    \*this text is surrounded by literal asterisks\*



<h3 id="code">Code</h3>

To indicate a span of code, wrap it with backtick quotes (`` ` ``).
Unlike a pre-formatted code block, a code span indicates code within a
normal paragraph. For example:

    Use the `printf()` function.

will produce:

    <p>Use the <code>printf()</code> function.</p>

To include a literal backtick character within a code span, you can use
multiple backticks as the opening and closing delimiters:

    ``There is a literal backtick (`) here.``

which will produce this:

    <p><code>There is a literal backtick (`) here.</code></p>

The backtick delimiters surrounding a code span may include spaces --
one after the opening, one before the closing. This allows you to place
literal backtick characters at the beginning or end of a code span:

	A single backtick in a code span: `` ` ``

	A backtick-delimited string in a code span: `` `foo` ``

will produce:

	<p>A single backtick in a code span: <code>`</code></p>

	<p>A backtick-delimited string in a code span: <code>`foo`</code></p>

With a code span, ampersands and angle brackets are encoded as HTML
entities automatically, which makes it easy to include example HTML
tags. Markdown will turn this:

    Please don't use any `<blink>` tags.

into:

    <p>Please don't use any <code>&lt;blink&gt;</code> tags.</p>

You can write this:

    `&#8212;` is the decimal-encoded equivalent of `&mdash;`.

to produce:

    <p><code>&amp;#8212;</code> is the decimal-encoded
    equivalent of <code>&amp;mdash;</code>.</p>



<h3 id="img">Images</h3>

Admittedly, it's fairly difficult to devise a "natural" syntax for
placing images into a plain text document format.

Markdown uses an image syntax that is intended to resemble the syntax
for links, allowing for two styles: *inline* and *reference*.

Inline image syntax looks like this:

    ![Alt text](/path/to/img.jpg)

    ![Alt text](/path/to/img.jpg "Optional title")

That is:

*   An exclamation mark: `!`;
*   followed by a set of square brackets, containing the `alt`
    attribute text for the image;
*   followed by a set of parentheses, containing the URL or path to
    the image, and an optional `title` attribute enclosed in double
    or single quotes.

Reference-style image syntax looks like this:

    ![Alt text][id]

Where "id" is the name of a defined image reference. Image references
are defined using syntax identical to link references:

    [id]: url/to/image  "Optional title attribute"

As of this writing, Markdown has no syntax for specifying the
dimensions of an image; if this is important to you, you can simply
use regular HTML `<img>` tags.


* * *


<h2 id="misc">Miscellaneous</h2>

<h3 id="autolink">Automatic Links</h3>

Markdown supports a shortcut style for creating "automatic" links for URLs and email addresses: simply surround the URL or email address with angle brackets. What this means is that if you want to show the actual text of a URL or email address, and also have it be a clickable link, you can do this:

    <http://example.com/>

Markdown will turn this into:

    <a href="http://example.com/">http://example.com/</a>

Automatic links for email addresses work similarly, except that
Markdown will also perform a bit of randomized decimal and hex
entity-encoding to help obscure your address from address-harvesting
spambots. For example, Markdown will turn this:

    <address@example.com>

into something like this:

    <a href="&#x6D;&#x61;i&#x6C;&#x74;&#x6F;:&#x61;&#x64;&#x64;&#x72;&#x65;
    &#115;&#115;&#64;&#101;&#120;&#x61;&#109;&#x70;&#x6C;e&#x2E;&#99;&#111;
    &#109;">&#x61;&#x64;&#x64;&#x72;&#x65;&#115;&#115;&#64;&#101;&#120;&#x61;
    &#109;&#x70;&#x6C;e&#x2E;&#99;&#111;&#109;</a>

which will render in a browser as a clickable link to "address@example.com".

(This sort of entity-encoding trick will indeed fool many, if not
most, address-harvesting bots, but it definitely won't fool all of
them. It's better than nothing, but an address published in this way
will probably eventually start receiving spam.)



<h3 id="backslash">Backslash Escapes</h3>

Markdown allows you to use backslash escapes to generate literal
characters which would otherwise have special meaning in Markdown's
formatting syntax. For example, if you wanted to surround a word with
literal asterisks (instead of an HTML `<em>` tag), you can backslashes
before the asterisks, like this:

    \*literal asterisks\*

Markdown provides backslash escapes for the following characters:

    \   backslash
    `   backtick
    *   asterisk
    _   underscore
    {}  curly braces
    []  square brackets
    ()  parentheses
    #   hash mark
	+	plus sign
	-	minus sign (hyphen)
    .   dot
    !   exclamation mark



pegdown/src/test/resources/MarkdownTest103/Nested blockquotes.md
---

> foo
>
> > bar
>
> foo


pegdown/src/test/resources/MarkdownTest103/Ordered and unordered lists.md
---

## Unordered

Asterisks tight:

*	asterisk 1
*	asterisk 2
*	asterisk 3


Asterisks loose:

*	asterisk 1

*	asterisk 2

*	asterisk 3

* * *

Pluses tight:

+	Plus 1
+	Plus 2
+	Plus 3


Pluses loose:

+	Plus 1

+	Plus 2

+	Plus 3

* * *


Minuses tight:

-	Minus 1
-	Minus 2
-	Minus 3


Minuses loose:

-	Minus 1

-	Minus 2

-	Minus 3


## Ordered

Tight:

1.	First
2.	Second
3.	Third

and:

1. One
2. Two
3. Three


Loose using tabs:

1.	First

2.	Second

3.	Third

and using spaces:

1. One

2. Two

3. Three

Multiple paragraphs:

1.	Item 1, graf one.

	Item 2. graf two. The quick brown fox jumped over the lazy dog's
	back.

2.	Item 2.

3.	Item 3.



## Nested

*	Tab
	*	Tab
		*	Tab

Here's another:

1. First
2. Second:
	* Fee
	* Fie
	* Foe
3. Third

Same thing but with paragraphs:

1. First

2. Second:
	* Fee
	* Fie
	* Foe

3. Third


This was an error in Markdown 1.0.1:

*	this

	*	sub

	that


pegdown/src/test/resources/MarkdownTest103/Strong and em together.md
---

***This is strong and em.***

So is ***this*** word.

___This is strong and em.___

So is ___this___ word.


pegdown/src/test/resources/MarkdownTest103/Tabs.md
---

+	this is a list item
	indented with tabs

+   this is a list item
    indented with spaces

Code:

	this code block is indented by one tab

And:

		this code block is indented by two tabs

And:

	+	this is an example list item
		indented with tabs

	+   this is an example list item
	    indented with spaces


pegdown/src/test/resources/MarkdownTest103/Tidyness.md
---

> A list within a blockquote:
>
> *	asterisk 1
> *	asterisk 2
> *	asterisk 3


pegdown/src/test/resources/Maruku/abbreviations.md
---


The HTML specification is maintained by the W3C.

*[HTML]: Hyper Text Markup Language
*[W3C]:  World Wide Web Consortium



Operation Tigra Genesis is going well.

*[Tigra Genesis]:


pegdown/src/test/resources/Maruku/alt.md
---

 ![bar](/foo.jpg)




pegdown/src/test/resources/Maruku/blank.md
---


Linea 1

Linea 2


pegdown/src/test/resources/Maruku/blanks_in_code.md
---

This block is composed of three lines:

	one

	three

This block is composed of 5


	one


	four


This block is composed of 2


	two





pegdown/src/test/resources/Maruku/bug_def.md
---

[test][]:



pegdown/src/test/resources/Maruku/bug_table.md
---



hello
{: summary="Table summary" .class1 style="color:red"}

h         | h
----------|--
{:t}  c1  | c2
{: summary="Table summary" .class1 style="color:red"}



{:t: scope="row"}


pegdown/src/test/resources/Maruku/code2.md
---

> Code
>
>     Ciao


pegdown/src/test/resources/Maruku/code3.md
---


This is code (4 spaces):

    Code
This is not code

    Code

This is code (1 tab):

	Code
This is not code

	Code





pegdown/src/test/resources/Maruku/code.md
---

Here is an example of AppleScript:

    tell application "Foo"
        beep
    end tell
    	tab



pegdown/src/test/resources/Maruku/data_loss.md
---

1. abcd
efgh
ijkl



pegdown/src/test/resources/Maruku/easy.md
---

*Hello!* how are **you**?


pegdown/src/test/resources/Maruku/email.md
---



This is an email address: <andrea@invalid.it>



pegdown/src/test/resources/Maruku/entities.md
---

Maruku translates HTML entities to the equivalent in LaTeX:

Entity      | Result
------------|----------
`&copy;`    |  &copy;
`&pound;`   |  &pound;
`a&nbsp;b`  |  a&nbsp;b
`&lambda;`  |  &lambda;
`&mdash;`   |  &mdash;


Entity-substitution does not happen in code blocks or inline code.

The following should not be translated:

	&copy;

It should read just like this: `&copy;`.




pegdown/src/test/resources/Maruku/escaping.md
---

 Hello: ! \! \` \{ \} \[ \] \( \) \# \. \! * \* *


Ora, *emphasis*, **bold**, * <- due asterischi-> * , un underscore-> _ , _emphasis_,
 incre*dible*e!

This is ``Code with a special: -> ` <- ``(after)

`Start ` of paragraph

End of `paragraph `


pegdown/src/test/resources/Maruku/extra_dl.md
---

CSS: style.css


Apple
:   Pomaceous fruit of plants of the genus Malus in
    the family Rosaceae.

Orange
:   The fruit of an evergreen tree of the genus Citrus.



pegdown/src/test/resources/Maruku/extra_header_id.md
---

Header 1            {#header1}
========

Header 2            {#header2}
--------

### Header 3 ###      {#header3}

Then you can create links to different parts of the same document like this:

[Link back to header 1](#header1),
[Link back to header 2](#header2),
[Link back to header 3](#header3)



pegdown/src/test/resources/Maruku/extra_table1.md
---


First Header  | Second Header
------------- | -------------
Content Cell  | Content Cell
Content Cell  | Content Cell



pegdown/src/test/resources/Maruku/footnotes.md
---

That's some text with a footnote [^b] and another [^c] and another [^a].

[^a]: And that's the footnote.

    That's the second paragraph of the footnote.


[^b]: And that's the footnote.
This is second sentence (same paragraph).

[^c]:
    This is the very long one.

    That's the second paragraph.


This is not a footnote.


pegdown/src/test/resources/Maruku/headers.md
---

A title with *emphasis*
=======================

A title with *emphasis*
-----------------------


#### A title with *emphasis* ####





pegdown/src/test/resources/Maruku/hex_entities.md
---

Examples of numeric character references include &#169; or &#xA9; for the copyright symbol, &#913; or &#x391; for the Greek capital letter alpha, and &#1575; or &#x627; for the Arabic letter alef.




pegdown/src/test/resources/Maruku/hrule.md
---

* * *



pegdown/src/test/resources/Maruku/html2.md
---

One
<div></div>123

<div></div>123


pegdown/src/test/resources/Maruku/html3.md
---

taking part in <a href="http://sied.dis.uniroma1.it/">some arcane conspirations</a> which
involve <b href="http://www.flickr.com/photos/censi/70893277/">coffee</b>,
<a href="http://flickr.com/photos/censi/42775664/in/set-936677/">robots</a>,
<a href="http://www.flickr.com/photos/censi/42775888/in/set-936677/">sushi</a>,



pegdown/src/test/resources/Maruku/html4.md
---

<div class="frame">
	<a  class="photo" href="http://www.flickr.com/photos/censi/54757256/"><img alt=""
  moz-do-not-send="true"
  src="http://static.flickr.com/27/54757256_1a2c1d2a95_m.jpg" /></a>
</div>




pegdown/src/test/resources/Maruku/html5.md
---

 <div class="frame">
 <a class="photo" href="http://www.flickr.com/photos/censi/88561568/" ><img moz-do-not-send="true" src="http://static.flickr.com/28/88561568_ab84d28245_m.jpg" width="240" height="180" alt="Aperitif" /></a>
 </div>




pegdown/src/test/resources/Maruku/ie.md
---

`<p>here's an apostrophe & a quote "</p>`

	<p>here's an apostrophe & a quote "</p>
{:}

	<p>here's an apostrophe & a quote "</p>
{:lang=xml}

	<p>here's an apostrophe & a quote "</p>
{:html_use_syntax=true lang=not_supported}

	<p>here's an apostrophe & a quote "</p>
{:html_use_syntax=true lang=xml}




pegdown/src/test/resources/Maruku/images2.md
---


This is an ![image][].

This is an ![image].

[image]: image.jpg



pegdown/src/test/resources/Maruku/images.md
---


This page does not uilizes ![Cascading Style Sheets](http://jigsaw.w3.org/css-validator/images/vcss)


Please mouseover to see the title: ![Cascading Style Sheets](http://jigsaw.w3.org/css-validator/images/vcss "Title ok!")

Please mouseover to see the title: ![Cascading Style Sheets](http://jigsaw.w3.org/css-validator/images/vcss 'Title ok!')


I'll say it one more time: this page does not use ![Cascading Style Sheets] [css]

This is double size: ![Cascading Style Sheets] [css2]



[css]: http://jigsaw.w3.org/css-validator/images/vcss "Optional title attribute"

[css2]: http://jigsaw.w3.org/css-validator/images/vcss "Optional title attribute"

pegdown/src/test/resources/Maruku/inline_html2.md
---

<div markdown="1">Test **bold**</div>
<p markdown="1">Test **bold**</p>


pegdown/src/test/resources/Maruku/inline_html.md
---

CSS: style.css

Input:

	<em>Emphasis</em>

Result: <em>Emphasis</em>

Input:

	<img src="http://jigsaw.w3.org/css-validator/images/vcss"/>

Result on span: <img src="http://jigsaw.w3.org/css-validator/images/vcss"/>

Result alone:

<img src="http://jigsaw.w3.org/css-validator/images/vcss"/>

<div markdown="1">
	This is *true* markdown text (paragraph)

	<p markdown="1">
		This is *true* markdown text (no paragraph)
	</p>
	<p markdown="block">
		This is *true* markdown text (block paragraph)
	</p>
</div>

<table>
<tr>
<td markdown="1">This is *true* markdown text. (no par)</td>
<td markdown="block">This is *true* markdown text. (par)</td>
</tr>
</table>




pegdown/src/test/resources/Maruku/links.md
---


Search on [Google][]

Search on [Google] []

Search on [Google] [google]

Search on [Google] [Google]

Search on [Google images][]

Inline: [Google images](http://google.com)

Inline with title: [Google images](http://google.com "Title")

Inline with title: [Google images]( http://google.com  "Title" )


Search on <http://www.gogole.com> or <http://Here.com> or ask <bill@google.com>
or you might ask bill@google.com.

If all else fails, ask [Google](http://www.google.com)

[google]: http://www.google.com

[google2]: http://www.google.com 'Single quotes'

[google3]: http://www.google.com "Double quotes"

[google4]: http://www.google.com (Parenthesis)

[Google Search]:
 http://www.google.com "Google search"

[Google Images]:
 http://images.google.com  (Google images)


pegdown/src/test/resources/Maruku/list1.md
---

*   A list item with a blockquote:

    > This is a blockquote
    > inside a list item.



pegdown/src/test/resources/Maruku/list2.md
---

*   This is a list item with two paragraphs.

    This is the second paragraph in the list item. You're
only required to indent the first line. Lorem ipsum dolor
sit amet, consectetuer adipiscing elit.

*   other



pegdown/src/test/resources/Maruku/list3.md
---

*   A list item with a blockquote:

    > This is a blockquote
    > inside a list item.

*   A list item with a code block:

        <code goes here>


pegdown/src/test/resources/Maruku/list4.md
---

This is a list:
* one
* two

This is not a list:
* one
ciao

This is a list:
1. one
1. two

This is not a list:
1987. one
ciao



pegdown/src/test/resources/Maruku/lists11.md
---

- ένα



pegdown/src/test/resources/Maruku/lists6.md
---




pegdown/src/test/resources/Maruku/lists7b.md
---

* a
    * a1
    * a2
* b




pegdown/src/test/resources/Maruku/lists7.md
---

Ciao

*	Tab
	*	Tab
		*	Tab



pegdown/src/test/resources/Maruku/lists8.md
---

Here is a paragraph.


   * Item 1
   * Item 2
   * Item 3



pegdown/src/test/resources/Maruku/lists9.md
---

- Due
  1. tre
  1. tre
  1. tre
- Due


pegdown/src/test/resources/Maruku/lists_after_paragraph.md
---

Paragraph, list with no space:
* ciao

Paragraph, list with 1 space:
 * ciao

Paragraph, list with 3 space:
   * ciao

Paragraph, list with 4 spaces:
    * ciao

Paragraph, list with 1 tab:
	* ciao

Paragraph (1 space after), list with no space:
* ciao

Paragraph (2 spaces after), list with no space:
* ciao

Paragraph (3 spaces after), list with no space:
* ciao

Paragraph with block quote:
> Quoted

Paragraph with header:
### header ###

Paragraph with header on two lines:
header
------


Paragraph with html after
<div></div>

Paragraph with html after, indented:
     <em>Emphasis</em>

Paragraph with html after, indented: <em>Emphasis</em> *tralla* <em>Emph</em>

Paragraph with html after, indented: <em>Emphasis *tralla* Emph</em>



pegdown/src/test/resources/Maruku/lists.md
---

*   Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
    Aliquam hendrerit mi posuere lectus. Vestibulum enim wisi,
    viverra nec, fringilla in, laoreet vitae, risus.
*   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
    Suspendisse id sem consectetuer libero luctus adipiscing.
*   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
Suspendisse id sem consectetuer libero luctus adipiscing.
 *  Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
Suspendisse id sem consectetuer libero luctus adipiscing.
 *  Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
 Suspendisse id sem consectetuer libero luctus adipiscing.

Ancora

*   This is a list item with two paragraphs. Lorem ipsum dolor
    sit amet, consectetuer adipiscing elit. Aliquam hendrerit
    mi posuere lectus.

    ATTENZIONE!

*  Suspendisse id sem consectetuer libero luctus adipiscing.


Ancora

*   This is a list item with two paragraphs.

    This is the second paragraph in the list item. You're
only required to indent the first line. Lorem ipsum dolor
sit amet, consectetuer adipiscing elit.

*   Another item in the same list.


pegdown/src/test/resources/Maruku/lists_ol.md
---

1.   Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
    Aliquam hendrerit mi posuere lectus. Vestibulum enim wisi,
    viverra nec, fringilla in, laoreet vitae, risus.
 2.   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
    Suspendisse id sem consectetuer libero luctus adipiscing.
3.   Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
Suspendisse id sem consectetuer libero luctus adipiscing.
 3.  Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
Suspendisse id sem consectetuer libero luctus adipiscing.
 4.  Donec sit amet nisl. Aliquam semper ipsum sit amet velit.
 Suspendisse id sem consectetuer libero luctus adipiscing.

Ancora

1.  This is a list item with two paragraphs. Lorem ipsum dolor
    sit amet, consectetuer adipiscing elit. Aliquam hendrerit
    mi posuere lectus.

    ATTENZIONE!

    - Uno
    - Due
        1. tre
        1. tre
        1. tre
    - Due

2.  Suspendisse id sem consectetuer libero luctus adipiscing.


Ancora

*   This is a list item with two paragraphs.

    This is the second paragraph in the list item. You're
only required to indent the first line. Lorem ipsum dolor
sit amet, consectetuer adipiscing elit.

*   Another item in the same list.


pegdown/src/test/resources/Maruku/loss.md
---

<br/>123



pegdown/src/test/resources/Maruku/misc_sw.md
---

Subject: Software not painful to use
Subject_short: painless software
Topic: /misc/coolsw
Archive: no
Date: Nov 20 2006
Order: -9.5
inMenu: true


### General ###

* *Operating System* : [Mac OS X][switch]: heaven, after the purgatory of Linux
  and the hell of Windows.
* *Browser*: [Firefox][firefox]. On a Mac, [Camino][camino].
* *Email*: [GMail][gmail], "search, don't sort" really works.
* *Text Editor*: [TextMate][textmate], you have to buy it, but it's worth every
  penny. There are rumours that it's been converting (recovering) Emacs
  users (addicts). Unfortunately, it's Mac only. An alternative is
  [jedit][jedit] (GPL, Java).

### Development ###

* *Build system*: [cmake][cmake], throw the [autotools][autotools] away.
* *Source code control system*: ditch CVS for [subversion][subversion].
* *Project management*: [Trac][trac] tracks everything.
* *Scripting language*: [Ruby][ruby] is Japanese pragmatism (and has a [poignant][poignant] guide).
   Python, you say? Python is too academic and snob:

      $ python
      Python 2.4.1 (\#1, Jun  4 2005, 00:54:33)
      Type "help", "copyright", "credits" or "license" for more information.
      >>> exit
      'Use Ctrl-D (i.e. EOF) to exit.'
      >>> quit
      'Use Ctrl-D (i.e. EOF) to exit.'

* *Java IDE*: [JBuilder][jbuilder] is great software and has a free version (IMHO better than Eclipse). Java
 is not a pain anymore since it gained [generics][java-generics] and got opensourced.
* *Mark-up language*: HTML is so 2001, why don't you take at look at [Markdown][markdown]? [Look at the source of this page](data/misc_markdown.png).
* *C++ libraries*:
    * [QT][qt] for GUIs.
    * [GSL][gsl] for math.
    * [Magick++][magick] for manipulating images.
    * [Cairo][cairo] for creating PDFs.
    * [Boost][boost] for just about everything else.


### Research ###

* *Writing papers*: [LaTeX][latex]
* *Writing papers & enjoying the process*: [LyX][lyx]
* *Handsome figures in your papers*: [xfig][xfig] or, better, [jfig][jfig].
* *The occasional presentation with many graphical content*:
  [OpenOffice Impress][impress] (using the [OOOlatex plugin][ooolatex]);
  the alternative is PowerPoint with the [TexPoint][texpoint] plugin.
* *Managing BibTeX*: [jabref][jabref]: multi-platform, for all your bibtex needs.
* *IEEExplore and BibTeX*: convert citations using [BibConverter][bibconverter].

### Cool websites ###

* *Best site in the wwworld*: [Wikipedia][wikipedia]
* [Mutopia][mutopia] for sheet music; [the Gutenberg Project][gutenberg] for books; [LiberLiber][liberliber] for books in italian.
* *Blogs*: [Bloglines][bloglines]
* *Sharing photos*: [flickr][flickr] exposes an API you can use.


[firefox]:   http://getfirefox.com/
[gmail]:     http://gmail.com/
[bloglines]: http://bloglines.com/
[wikipedia]: http://en.wikipedia.org/
[ruby]:      http://www.ruby-lang.org/
[poignant]:  http://poignantguide.net/ruby/
[webgen]:    http://webgen.rubyforge.org/
[markdown]:  http://daringfireball.net/projects/markdown/
[latex]:     http://en.wikipedia.org/wiki/LaTeX
[lyx]:       http://www.lyx.org
[impress]:   http://www.openoffice.org/product/impress.html
[ooolatex]:  http://ooolatex.sourceforge.net/
[texpoint]:  http://texpoint.necula.org/
[jabref]:    http://jabref.sourceforge.net/
[camino]:    http://www.caminobrowser.org/
[switch]:    http://www.apple.com/getamac/
[textmate]:  http://www.apple.com/getamac/
[cmake]:     http://www.cmake.org/
[xfig]:      http://www.xfig.org/
[jfig]:         http://tams-www.informatik.uni-hamburg.de/applets/jfig/
[subversion]:   http://subversion.tigris.org
[jbuilder]:     http://www.borland.com/us/products/jbuilder/index.html
[flickr]:       http://www.flickr.com/
[myflickr]:     http://www.flickr.com/photos/censi
[bibconverter]: http://www.bibconverter.net/ieeexplore/
[autotools]:    http://sources.redhat.com/autobook/
[jedit]:        http://www.jedit.org/
[qt]:           http://www.trolltech.no/
[gsl]:          http://www.gnu.org/software/gsl/
[magick]:       http://www.imagemagick.org/Magick++/
[cairo]:        http://cairographics.org/
[boost]:        http://www.boost.org/
[markdown]:     http://en.wikipedia.org/wiki/Markdown
[trac]:         http://trac.edgewall.org/
[mutopia]:      http://www.mutopiaproject.org/
[liberliber]:   http://www.liberliber.it/
[gutenberg]:    http://www.gutenberg.org/
[java-generics]: http://java.sun.com/j2se/1.5.0/docs/guide/language/generics.html




pegdown/src/test/resources/Maruku/olist.md
---

This is a list:

2. one
2. two
3. three


pegdown/src/test/resources/Maruku/one.md
---

One line


pegdown/src/test/resources/Maruku/paragraph.md
---

Paragraph



pegdown/src/test/resources/Maruku/paragraphs.md
---

Paragraph 1

Paragraph 2


Paragraph 3
Paragraph 4
Paragraph Br->
Paragraph 5




pegdown/src/test/resources/Maruku/smartypants.md
---

	'Twas a "test" to 'remember' in the '90s.
'Twas a "test" to 'remember' in the '90s.

	It was --- in a sense --- really... interesting.
It was --- in a sense --- really... interesting.

	I -- too -- met << some curly quotes >> there or <<here>>No space.
I -- too -- met << some curly quotes >> there or <<here>>No space.


	She was 6\"12\'.
> She was 6\"12\'.



pegdown/src/test/resources/Maruku/syntax_hl.md
---

This is ruby code:

	require 'maruku'

	puts Maruku.new($stdin).to_html

This is ruby code:

	require 'maruku'
{: lang=ruby html_use_syntax}

	puts Maruku.new($stdin).to_html


pegdown/src/test/resources/Maruku/table_attributes.md
---


h         | h
----------|--
{:t}  c1  | c2
{: summary="Table summary" .class1 style="color:red" border=3 width="50%" frame=lhs rules=cols  cellspacing=2em cellpadding=4px}

{:t: scope="row"}


pegdown/src/test/resources/Maruku/test.md
---


           $ python





pegdown/src/test/resources/Maruku/wrapping.md
---

Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Break:
Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet.

* Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet
  Lorem ipsum Break:
  Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet
* Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet. Lorem ipsum dolor amet



pegdown/src/test/resources/Maruku/xml2.md
---

<!--
<
-->


pegdown/src/test/resources/Maruku/xml3.md
---

<table markdown='1'>
	Blah
	<thead>
		<td>*em*</td>
	</thead>
</table>



pegdown/src/test/resources/Maruku/xml_instruction.md
---


<? noTarget?>
<?php ?>
<?xml ?>
<?mrk ?>

Targets <? noTarget?> <?php ?> <?xml ?> <?mrk ?>

Inside: <?mrk puts "Inside: Hello" ?> last




pegdown/src/test/resources/Maruku/xml.md
---


<svg:svg/>

<svg:svg
width="600px" height="400px">
  <svg:g id="group">
	<svg:circle id="circ1" r="1cm" cx="3cm" cy="3cm" style="fill:red;"></svg:circle>
	<svg:circle id="circ2" r="1cm" cx="7cm" cy="3cm" style="fill:red;" />
  </svg:g>
</svg:svg>



pegdown/src/test/resources/pegdown/Abbreviations.md
---


The HTML specification is maintained by the W3C.

*[HTML]: Hyper Text Markup Language
*[W3C]:  World Wide Web Consortium



Operation Tigra Genesis is going well.

*[Tigra Genesis]:


pegdown/src/test/resources/pegdown/AstText.md
---

# Ast Test

This _is_ a **simple** & small text to

> test
> yes, test

the functionality of

* AST index creation
    - one more level

* nothing more

        Some code!

* multi
  paragraph

  list item!

And:
  ~ a definition!

Another one
:   With more

    than one paragraph!

Everything

    is expected
    to be fine

A Blockquote

> easy
>
> easy easy

A code block starting with a tab:

	> bla bla

And more text

pegdown/src/test/resources/pegdown/Autolinks.md
---

# Autolinks

Autolinks are simple URIs like http://www.parboiled.org,
which will be automatically "activated" by pegdown.

pegdown tries to be smart and not include trailing
punctuation marks like commas and such in the email
and URI links (joe@somewhere.com is such an example).
ftp://somesite.org:1234: this would be another one!


pegdown/src/test/resources/pegdown/Bug_in_0.8.5.1.md
---

**A**ll**F**ather wo**R**ld **O**rder!

pegdown/src/test/resources/pegdown/Bug_in_0.8.5.4.md
---

  * Hello World
    * Worwe qworijwetor

pegdown/src/test/resources/pegdown/Bug_in_1.0.0.md
---

The following list contains items with code blocks:

* List Item A

        This is a verbatim line
        and another one

        all of these should become
        part of the

        same verbatim block

    only these lines here

        should interrupt
        the

        verbatims blocks

* another List Items

and something completely different here

pegdown/src/test/resources/pegdown/HTML suppression.md
---

HTML <b>SUPPRESSION</b>
=======================

This is a paragraph containing a <strong>strong</strong> inline HTML element and:

<div>
    <p>an actual block of HTML!</p>
</div>


pegdown/src/test/resources/pegdown/Linebreaks.md
---

Linebreaks
==========

With the HARDWRAPS extension
enabled all these linebreaks
should be kept as is in the
created HTML

These ones here
as
well!


pegdown/src/test/resources/pegdown/No Follow Links.md
---

# No Follow Links

Autolinks are simple URIs like http://www.parboiled.org,
which will be automatically "activated" by pegdown.

pegdown tries to be smart and not include trailing
punctuation marks like commas and such in the email
and URI links (joe@somewhere.com is such an example).
ftp://somesite.org:1234: this would be another one!

This is a [regular](http://regular.com) link and
[this one here][] is a reference link.

  [this one here]: http://blabla.com

pegdown/src/test/resources/pegdown/Parens_in_URL.md
---

[Inline link 1 with parens](/url\(test\) "title").

[Inline link 2 with parens](</url\(test\)> "title").

[Reference link 1 with parens][1].

[Reference link 2 with parens][2].

  [1]: /url(test) "title"
  [2]: </url(test)> "title"


pegdown/src/test/resources/pegdown/Quoted Blockquote.md
---

    > Line A
    > Line B
    >
    > Line after blank line.


pegdown/src/test/resources/pegdown/Smartypants.md
---

# Smart quotes, ellipses, dashes

"Hello," said the spider. "'Shelob' is my name."

'A', 'B', and 'C' are letters.

'Oak,' 'elm,' and 'beech' are names of trees.
So is 'pine.'

'He said, "I want to go."'
Were you alive in the 70's?

Here is some quoted '`code`' and a "[quoted link][1]".

I've alwayed thought I'd rather not do it. But then we're all screwed, since I'm the only one!

Some dashes: one---two --- three--four -- five.

Dashes between numbers: 5-7, 255-66, 1987-1999.

Ellipses...and. . .and . . . .

pegdown/src/test/resources/pegdown/Special Chars.md
---

# Special character handling

Quote from <http://henkelmann.eu/2011/01/06/actuarius_release_note>

Better handling of escapes than PegDown: PegDown does not escape
special HTML characters like <, >, &, " and ' in normal text paragraphs, just in code blocks.

Maybe if>they are&not "stand-alone"?

Well, I don't think so :)
(at least not as of **pegdown 0.9.2**!)

pegdown/src/test/resources/pegdown/Tables.md
---

Tables
------

Simple Table:

First | Second
------|-------
Cool  | Shit

Without header:

|--------|-------|
|Cool    | Shit  |
|is this | really

With some alignment:

:-----|-----:|----|:---:
Cool  | Shit | in | here

And now to some colspan:

Names ||
 Name | Firstname | Age
------|-----------|----:
  Fox | Peter     | 42
  Guy | Ritchie   | ca. 60


Multimarkdown example:

|             |          Grouping           ||
First Header  | Second Header | Third Header |
 ------------ | :-----------: | -----------: |
Content       |          *Long Cell*        ||
Content       |   **Cell**    |         Cell |
New section   |     More      |         Data |
And more      |               |   And more   |



pegdown/src/test/resources/PhpMarkdown/Backslash_escapes.md
---

Tricky combinaisons:

backslash with \-- two dashes

backslash with \> greater than

\[test](not a link)

\*no emphasis*

pegdown/src/test/resources/PhpMarkdown/Code_block_in_a_list_item.md
---


*	List Item:

		code block

		with a blank line

	within a list item.

pegdown/src/test/resources/PhpMarkdown/Code_Spans.md
---

From `<!--` to `-->`
on two lines.

From `<!--`
to `-->`
on three lines.


pegdown/src/test/resources/PhpMarkdown/Email_auto_links.md
---

<michel.fortin@michelf.com>

International domain names: <help@tūdaliņ.lv>

pegdown/src/test/resources/PhpMarkdown/Emphasis.md
---

Combined emphasis:

1.  ***test test***
2.  ___test test___
3.  *test **test***
4.  **test *test***
5.  ***test* test**
6.  ***test** test*
7.  ***test* test**
8.  **test *test***
9.  *test **test***
10. _test __test___
11. __test _test___
12. ___test_ test__
13. ___test__ test_
14. ___test_ test__
15. __test _test___
16. _test __test___


Incorrect nesting:

1.  *test  **test*  test**
2.  _test  __test_  test__
3.  **test  *test** test*
4.  __test  _test__ test_
5.  *test   *test*  test*
6.  _test   _test_  test_
7.  **test **test** test**
8.  __test __test__ test__



No emphasis:

1.  test*  test  *test
2.  test** test **test
3.  test_  test  _test
4.  test__ test __test



Middle-word emphasis (asterisks):

1.  *a*b
2.   a*b*
3.   a*b*c
4. **a**b
5.   a**b**
6.   a**b**c


Middle-word emphasis (underscore):

1.  _a_b
2.   a_b_
3.   a_b_c
4. __a__b
5.   a__b__
6.   a__b__c

my_precious_file.txt


## Tricky Cases

E**. **Test** TestTestTest

E**. **Test** Test Test Test


pegdown/src/test/resources/PhpMarkdownExtra/Abbr.md
---

Some text about HTML, SGML and HTML4.

Let's talk about the U.S.A., (É.U. or É.-U. d'A. in French).

*[HTML4]: Hyper Text Markup Language version 4
*[HTML]: Hyper Text Markup Language
*[SGML]: Standard Generalized Markup Language
*[U.S.A.]: United States of America
*[É.U.] : États-Unis d'Amérique
*[É.-U. d'A.] : États-Unis d'Amérique

And here we have a CD, some CDs, and some other CD's.

*[CD]: Compact Disk

Let's transfert documents through TCP/IP, using TCP packets.

*[IP]: Internet Protocol
*[TCP]: Transmission Control Protocol

 ---

Bienvenue sur [CMS](http://www.bidulecms.com "Bidule CMS").

*[CMS]: Content Management System

pegdown/src/test/resources/PhpMarkdownExtra/Emphasis.md
---

Combined emphasis:

1.  ***test test***
2.  ___test test___
3.  *test **test***
4.  **test *test***
5.  ***test* test**
6.  ***test** test*
7.  ***test* test**
8.  **test *test***
9.  *test **test***
10. _test __test___
11. __test _test___
12. ___test_ test__
13. ___test__ test_
14. ___test_ test__
15. __test _test___
16. _test __test___


Incorrect nesting:

1.  *test  **test*  test**
2.  _test  __test_  test__
3.  **test  *test** test*
4.  __test  _test__ test_
5.  *test   *test*  test*
6.  _test   _test_  test_
7.  **test **test** test**
8.  __test __test__ test__



No emphasis:

1.  test*  test  *test
2.  test** test **test
3.  test_  test  _test
4.  test__ test __test



Middle-word emphasis (asterisks):

1.  *a*b
2.   a*b*
3.   a*b*c
4. **a**b
5.   a**b**
6.   a**b**c


Middle-word emphasis (underscore):

1.  _a_b
2.   a_b_
3.   a_b_c
4. __a__b
5.   a__b__
6.   a__b__c

my_precious_file.txt


## Tricky Cases

E**. **Test** TestTestTest

E**. **Test** Test Test Test


pegdown/src/test/resources/PhpMarkdownExtra/Fenced_Code_Blocks.md
---

~~~
Fenced
~~~

Code block starting and ending with empty lines:

~~~


Fenced


~~~

Indented code block containing fenced code block sample:

	~~~
	Fenced
	~~~

Fenced code block with indented code block sample:

~~~
Some text

	Indented code block sample code
~~~

Fenced code block with long markers:

~~~~~~~~~~~~~~~~~~
Fenced
~~~~~~~~~~~~~~~~~~

Empty Fenced code block:

~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~

Fenced code block with fenced code block markers of different length in it:

~~~~
In code block
~~~
Still in code block
~~~~~
Still in code block
~~~~

Fenced code block with Markdown header and horizontal rule:

~~~
#test
---
~~~

Fenced code block with link definitions, footnote definition and
abbreviation definitions:

~~~
[example]: http://example.com/

[^1]: Footnote def

*[HTML]: HyperText Markup Language
~~~

pegdown/src/test/resources/PhpMarkdownExtra/Footnotes.md
---

This is the first paragraph.[^first]

[^first]:  This is the first note.

* List item one.[^second]
* List item two.[^third]

[^third]: This is the third note, defined out of order.
[^second]: This is the second note.
[^fourth]: This is the fourth note.

# Header[^fourth]

Some paragraph with a footnote[^1], and another[^2].

[^1]: Content for fifth footnote.
[^2]: Content for sixth footnote spaning on
    three lines, with some span-level markup like
    _emphasis_, a [link][].

[link]: http://www.michelf.com/

Another paragraph with a named footnote[^fn-name].

[^fn-name]:
    Footnote beginning on the line next to the marker.

This paragraph should not have a footnote marker since
the footnote is undefined.[^3]

This paragraph should not have a footnote marker since
the footnote has already been used before.[^1]

This paragraph links to a footnote with plenty of
block-level content.[^block]

[^block]:
	Paragraph.

	*   List item

	> Blockquote

	    Code block

This paragraph host the footnote reference within a
footnote test[^reference].

[^reference]:
	This footnote attemps to refer to another footnote. This
	should be impossible.[^impossible]

[^impossible]:
	This footnote should not appear, as it is refered from
	another footnote, which is not allowed.


pegdown/src/test/resources/PhpMarkdownExtra/Inline_HTML_with_Markdown_content.md
---

# Markdown inside code blocks

<div markdown="1">
foo
</div>

<table>
<tr><td markdown="1">test _emphasis_ (span)</td></tr>
</table>

<table>
<tr><td markdown="span">test _emphasis_ (span)</td></tr>
</table>

<table>
<tr><td markdown="block">test _emphasis_ (block)</td></tr>
</table>

## More complicated

<table>
<tr><td markdown="1">
* this is _not_ a list item</td></tr>
<tr><td markdown="span">
* this is _not_ a list item</td></tr>
<tr><td markdown="block">
* this _is_ a list item
</td></tr>
</table>

## With indent

<div>
    <div markdown="1">

    Markdown content in HTML blocks is assumed to be
    indented the same as the block opening tag.

    **This should be the third paragraph after the header.**
    </div>
</div>

## Code block with rogue `</div>`s in Markdown code span and block

<div>
  <div markdown="1">
    * List item, not a code block

Some text

      This is a code block.
  </div>
</div>

## No code block in markdown span mode

<p markdown="1">
    This is not a code block since Markdown parse paragraph
    content as span. Code spans like `</p>` are allowed though.
</p>

<p markdown="1">_Hello_ _world_</p>

## Preserving attributes and tags on more than one line:

<p class="test" markdown="1"
id="12">
Some _span_ content.
</p>


## Header confusion bug

<table class="canvas">
<tr>
<td id="main" markdown="1">Hello World!
============

Hello World!</td>
</tr>
</table>


pegdown/src/test/resources/PhpMarkdownExtra/Tables.md
---

# Simple tables

Header 1  | Header 2
--------- | ---------
Cell 1    | Cell 2
Cell 3    | Cell 4

With leading pipes:

| Header 1  | Header 2
| --------- | ---------
| Cell 1    | Cell 2
| Cell 3    | Cell 4

With tailing pipes:

Header 1  | Header 2  |
--------- | --------- |
Cell 1    | Cell 2    |
Cell 3    | Cell 4    |

With leading and tailing pipes:

| Header 1  | Header 2  |
| --------- | --------- |
| Cell 1    | Cell 2    |
| Cell 3    | Cell 4    |

* * *

# One-column one-row table

With leading pipes:

| Header
| -------
| Cell

With tailing pipes:

Header  |
------- |
Cell    |

With leading and tailing pipes:

| Header  |
| ------- |
| Cell    |

* * *

Table alignement:

| Default   | Right     |  Center   |     Left  |
| --------- |:--------- |:---------:| ---------:|
| Long Cell | Long Cell | Long Cell | Long Cell |
| Cell      | Cell      |   Cell    |     Cell  |

Table alignement (alternate spacing):

| Default   | Right     |  Center   |     Left  |
| --------- | :-------- | :-------: | --------: |
| Long Cell | Long Cell | Long Cell | Long Cell |
| Cell      | Cell      |   Cell    |     Cell  |

* * *

# Empty cells

| Header 1  | Header 2  |
| --------- | --------- |
| A         | B         |
| C         |           |

Header 1  | Header 2
--------- | ---------
A         | B
          | D

* * *

# Missing tailing pipe

Header 1  | Header 2
--------- | --------- |
Cell      | Cell      |
Cell      | Cell      |

Header 1  | Header 2  |
--------- | ---------
Cell      | Cell      |
Cell      | Cell      |

Header 1  | Header 2  |
--------- | --------- |
Cell      | Cell
Cell      | Cell      |

Header 1  | Header 2  |
--------- | --------- |
Cell      | Cell      |
Cell      | Cell



pegdown/src/test/resources/PhpMarkdown/Headers.md
---

Header
======

Header
------

### Header

 - - -

Header
======
Paragraph

Header
------
Paragraph

### Header
Paragraph

 - - -

Paragraph
Header
======
Paragraph

Paragraph
Header
------
Paragraph

Paragraph
### Header
Paragraph

pegdown/src/test/resources/PhpMarkdown/Horizontal_Rules.md
---

Horizontal rules:

- - -

* * *

***

---

___

Not horizontal rules (testing for a bug in 1.0.1j):

+++

,,,

===

???

AAA

jjj

j j j

n n n


pegdown/src/test/resources/PhpMarkdown/Inline_HTML_comments.md
---

Paragraph one.

Paragraph two.

<!-- enclosed tag </div> -->

The end.


pegdown/src/test/resources/PhpMarkdown/Inline_HTML_(Simple).md
---

With some attributes:

<div id="test">
	foo
</div>

<div id="test"
     class="nono">
	foo
</div>


pegdown/src/test/resources/PhpMarkdown/Inline_HTML_(Span).md
---

<abbr title="` **Attribute Content Is Not A Code Span** `">ACINACS</abbr>

<abbr title="`first backtick!">SB</abbr>
<abbr title="`second backtick!">SB</abbr>

pegdown/src/test/resources/PhpMarkdown/Ins_and_del.md
---

Here is a block tag ins:

<ins>
<p>Some text</p>
</ins>

<ins>And here it is inside a paragraph.</ins>

And here it is <ins>in the middle of</ins> a paragraph.

<del>
<p>Some text</p>
</del>

<del>And here is ins as a paragraph.</del>

And here it is <del>in the middle of</del> a paragraph.


pegdown/src/test/resources/PhpMarkdown/Links_inline_style.md
---

[silly URL w/ angle brackets](<?}]*+|&)>).


pegdown/src/test/resources/PhpMarkdown/MD5_Hashes.md
---

# Character Escapes

The MD5 value for `+` is "26b17225b626fb9238849fd60eabdf60".

# HTML Blocks

<p>test</p>

The MD5 value for `<p>test</p>` is:

6205333b793f34273d75379350b36826

pegdown/src/test/resources/PhpMarkdown/Nesting.md
---

Valid nesting:

**[Link](url)**

[**Link**](url)

**[**Link**](url)**

pegdown/src/test/resources/PhpMarkdown/Parens_in_URL.md
---

[Inline link 1 with parens](/url\(test\) "title").

[Inline link 2 with parens](</url\(test\)> "title").

[Inline link 3 with non-escaped parens](/url(test) "title").

[Inline link 4 with non-escaped parens](</url(test)> "title").

[Reference link 1 with parens][1].

[Reference link 2 with parens][2].

  [1]: /url(test) "title"
  [2]: </url(test)> "title"


pegdown/src/test/resources/PhpMarkdown/PHP-Specific_Bugs.md
---

This tests for a bug where quotes escaped by PHP when using
`preg_replace` with the `/e` modifier must be correctly unescaped
(hence the `_UnslashQuotes` function found only in PHP Markdown).



Headers below should appear exactly as they are typed (no backslash
added or removed).

Header "quoted\" again \\""
===========================

Header "quoted\" again \\""
---------------------------

### Header "quoted\" again \\"" ###



Test with tabs for `_Detab`:

	Code	'block'	with	some	"tabs"	and	"quotes"


pegdown/src/test/resources/PhpMarkdown/Tight_blocks.md
---

Paragraph and no space:
* ciao

Paragraph and 1 space:
 * ciao

Paragraph and 3 spaces:
  * ciao

Paragraph and 4 spaces:
   * ciao

Paragraph before header:
#Header

Paragraph before blockquote:
>Some quote.

pegdown/src/test/resources/textmarkdown/CoreDumps5.8.md
---

* Unordered
1. Ordered

Text

* Unordered
1. Ordered


pegdown/src/test/resources/textmarkdown/Emphasis.md
---

_M*A*S*H_ here I am going with original Markdown..

foo_bar_bas I am going with PHP Markdown Extra here (by default, there is an option for original style behavior - see
docs)..


pegdown/src/test/resources/textmarkdown/HTML-Comment-encoding.md
---

A markdown paragraph with a comment that *will* be processed by original Markdown. However MultiMarkdown and Pandoc do not convert the & sigil in the comment..

A paragraph <!-- This & *will* be converted by original Markdown -->

<p><!-- This & will *not* be converted --></p>


pegdown/src/test/resources/textmarkdown/Links_brackets.md
---

[ZIP archives](http://en.wikipedia.org/wiki/ZIP_(file_format) "ZIP (file format) - Wikipedia, the free encyclopedia")



pegdown/src/test/resources/textmarkdown/Links_multiline_bugs_1.md
---

<http://bugs.debian.org/459885>

[link
text] [link
id]

[link id]: /someurl/


pegdown/src/test/resources/textmarkdown/Links_multiline_bugs_2.md
---

<http://bugs.debian.org/459885>

Bla, bla, bla, bla, bla, bla, bla, bla, bla, bla bla. This is [my
University][].

  [my university]: http://www.ua.es


pegdown/src/test/resources/textmarkdown/Links_reference_style.md
---

Foo [bar] [1].

Foo [bar][1].

Foo [bar]
[1].

[1]: /url/  "Title"


With [embedded [brackets]] [b].


Indented [once][].

Indented [twice][].

Indented [thrice][].

Indented [four][] times.

 [once]: /url

  [twice]: /url

   [thrice]: /url

    [four]: /url


[b]: /url/

* * *

[this] [this] should work

So should [this][this].

And [this] [].

And [this][].

And [this].

But not [that] [].

Nor [that][].

Nor [that].

[Something in brackets like [this][] should work]

[Same with [this].]

In this case, [this](/somethingelse/) points to something else.

Backslashing should suppress \[this] and [this\].

[this]: foo


* * *

Here's one where the [link
breaks] across lines.

Here's another where the [link
breaks] across lines, but with a line-ending space.


[link breaks]: /url/

More multi line edge cases. First a broken link id

[link
text] [link
id]

[link id]: /someurl/

Then a line with 2 chars of trailing whitespace and a line break [my
University][].

The a shortcut reference link with 2 chars of trailing whitespace and a line break [my
University].

  [my university]: http://www.ua.es

pegdown/src/test/resources/textmarkdown/Lists-multilevel-md5-edgecase.md
---

# text1

  * text2

    * text3

    text4

## text5

  * text6

    * text7

  text8

## text9

  * text10

    * text11

    text12

    text13
