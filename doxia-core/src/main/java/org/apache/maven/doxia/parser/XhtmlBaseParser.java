package org.apache.maven.doxia.parser;

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

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.html.HTML.Attribute;

import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.doxia.util.DoxiaUtils;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Common base parser for xhtml events.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class XhtmlBaseParser
    extends AbstractXmlParser
        implements HtmlMarkup
{
    /**
     * True if a &lt;script&gt;&lt;/script&gt; or &lt;style&gt;&lt;/style&gt; block is read. CDATA sections within are
     * handled as rawText.
     */
    private boolean scriptBlock;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isLink;

    /** Used to distinguish &lt;a href=""&gt; from &lt;a name=""&gt;. */
    private boolean isAnchor;

    /** Used for nested lists. */
    private int orderedListDepth = 0;

    /** Counts section level. */
    private int sectionLevel;

    /** Verbatim flag, true whenever we are inside a &lt;pre&gt; tag. */
    private boolean inVerbatim;

    /** Used to recognize the case of img inside figure. */
    private boolean inFigure;

    /** Used to wrap the definedTerm with its definition, even when one is omitted */
    boolean hasDefinitionListItem = false;

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    private Map<String, Set<String>> warnMessages;

    /** {@inheritDoc} */
    @Override
    public void parse( Reader source, Sink sink )
        throws ParseException
    {
        init();

        try
        {
            super.parse( source, sink );
        }
        finally
        {
            logWarnings();

            setSecondParsing( false );
            init();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Adds all XHTML (HTML 4.0) entities to the parser so that they can be recognized and resolved
     * without additional DTD.
     */
    @Override
    protected void initXmlParser( XmlPullParser parser )
        throws XmlPullParserException
    {
        super.initXmlParser( parser );

        // the entities taken from org.apache.maven.doxia.document.io.xpp3.DocumentXpp3Reader,
        // which is generated automatically

        // ----------------------------------------------------------------------
        // Latin 1 entities
        // ----------------------------------------------------------------------

        parser.defineEntityReplacementText( "nbsp", "\u00a0" );
        parser.defineEntityReplacementText( "iexcl", "\u00a1" );
        parser.defineEntityReplacementText( "cent", "\u00a2" );
        parser.defineEntityReplacementText( "pound", "\u00a3" );
        parser.defineEntityReplacementText( "curren", "\u00a4" );
        parser.defineEntityReplacementText( "yen", "\u00a5" );
        parser.defineEntityReplacementText( "brvbar", "\u00a6" );
        parser.defineEntityReplacementText( "sect", "\u00a7" );
        parser.defineEntityReplacementText( "uml", "\u00a8" );
        parser.defineEntityReplacementText( "copy", "\u00a9" );
        parser.defineEntityReplacementText( "ordf", "\u00aa" );
        parser.defineEntityReplacementText( "laquo", "\u00ab" );
        parser.defineEntityReplacementText( "not", "\u00ac" );
        parser.defineEntityReplacementText( "shy", "\u00ad" );
        parser.defineEntityReplacementText( "reg", "\u00ae" );
        parser.defineEntityReplacementText( "macr", "\u00af" );
        parser.defineEntityReplacementText( "deg", "\u00b0" );
        parser.defineEntityReplacementText( "plusmn", "\u00b1" );
        parser.defineEntityReplacementText( "sup2", "\u00b2" );
        parser.defineEntityReplacementText( "sup3", "\u00b3" );
        parser.defineEntityReplacementText( "acute", "\u00b4" );
        parser.defineEntityReplacementText( "micro", "\u00b5" );
        parser.defineEntityReplacementText( "para", "\u00b6" );
        parser.defineEntityReplacementText( "middot", "\u00b7" );
        parser.defineEntityReplacementText( "cedil", "\u00b8" );
        parser.defineEntityReplacementText( "sup1", "\u00b9" );
        parser.defineEntityReplacementText( "ordm", "\u00ba" );
        parser.defineEntityReplacementText( "raquo", "\u00bb" );
        parser.defineEntityReplacementText( "frac14", "\u00bc" );
        parser.defineEntityReplacementText( "frac12", "\u00bd" );
        parser.defineEntityReplacementText( "frac34", "\u00be" );
        parser.defineEntityReplacementText( "iquest", "\u00bf" );
        parser.defineEntityReplacementText( "Agrave", "\u00c0" );
        parser.defineEntityReplacementText( "Aacute", "\u00c1" );
        parser.defineEntityReplacementText( "Acirc", "\u00c2" );
        parser.defineEntityReplacementText( "Atilde", "\u00c3" );
        parser.defineEntityReplacementText( "Auml", "\u00c4" );
        parser.defineEntityReplacementText( "Aring", "\u00c5" );
        parser.defineEntityReplacementText( "AElig", "\u00c6" );
        parser.defineEntityReplacementText( "Ccedil", "\u00c7" );
        parser.defineEntityReplacementText( "Egrave", "\u00c8" );
        parser.defineEntityReplacementText( "Eacute", "\u00c9" );
        parser.defineEntityReplacementText( "Ecirc", "\u00ca" );
        parser.defineEntityReplacementText( "Euml", "\u00cb" );
        parser.defineEntityReplacementText( "Igrave", "\u00cc" );
        parser.defineEntityReplacementText( "Iacute", "\u00cd" );
        parser.defineEntityReplacementText( "Icirc", "\u00ce" );
        parser.defineEntityReplacementText( "Iuml", "\u00cf" );
        parser.defineEntityReplacementText( "ETH", "\u00d0" );
        parser.defineEntityReplacementText( "Ntilde", "\u00d1" );
        parser.defineEntityReplacementText( "Ograve", "\u00d2" );
        parser.defineEntityReplacementText( "Oacute", "\u00d3" );
        parser.defineEntityReplacementText( "Ocirc", "\u00d4" );
        parser.defineEntityReplacementText( "Otilde", "\u00d5" );
        parser.defineEntityReplacementText( "Ouml", "\u00d6" );
        parser.defineEntityReplacementText( "times", "\u00d7" );
        parser.defineEntityReplacementText( "Oslash", "\u00d8" );
        parser.defineEntityReplacementText( "Ugrave", "\u00d9" );
        parser.defineEntityReplacementText( "Uacute", "\u00da" );
        parser.defineEntityReplacementText( "Ucirc", "\u00db" );
        parser.defineEntityReplacementText( "Uuml", "\u00dc" );
        parser.defineEntityReplacementText( "Yacute", "\u00dd" );
        parser.defineEntityReplacementText( "THORN", "\u00de" );
        parser.defineEntityReplacementText( "szlig", "\u00df" );
        parser.defineEntityReplacementText( "agrave", "\u00e0" );
        parser.defineEntityReplacementText( "aacute", "\u00e1" );
        parser.defineEntityReplacementText( "acirc", "\u00e2" );
        parser.defineEntityReplacementText( "atilde", "\u00e3" );
        parser.defineEntityReplacementText( "auml", "\u00e4" );
        parser.defineEntityReplacementText( "aring", "\u00e5" );
        parser.defineEntityReplacementText( "aelig", "\u00e6" );
        parser.defineEntityReplacementText( "ccedil", "\u00e7" );
        parser.defineEntityReplacementText( "egrave", "\u00e8" );
        parser.defineEntityReplacementText( "eacute", "\u00e9" );
        parser.defineEntityReplacementText( "ecirc", "\u00ea" );
        parser.defineEntityReplacementText( "euml", "\u00eb" );
        parser.defineEntityReplacementText( "igrave", "\u00ec" );
        parser.defineEntityReplacementText( "iacute", "\u00ed" );
        parser.defineEntityReplacementText( "icirc", "\u00ee" );
        parser.defineEntityReplacementText( "iuml", "\u00ef" );
        parser.defineEntityReplacementText( "eth", "\u00f0" );
        parser.defineEntityReplacementText( "ntilde", "\u00f1" );
        parser.defineEntityReplacementText( "ograve", "\u00f2" );
        parser.defineEntityReplacementText( "oacute", "\u00f3" );
        parser.defineEntityReplacementText( "ocirc", "\u00f4" );
        parser.defineEntityReplacementText( "otilde", "\u00f5" );
        parser.defineEntityReplacementText( "ouml", "\u00f6" );
        parser.defineEntityReplacementText( "divide", "\u00f7" );
        parser.defineEntityReplacementText( "oslash", "\u00f8" );
        parser.defineEntityReplacementText( "ugrave", "\u00f9" );
        parser.defineEntityReplacementText( "uacute", "\u00fa" );
        parser.defineEntityReplacementText( "ucirc", "\u00fb" );
        parser.defineEntityReplacementText( "uuml", "\u00fc" );
        parser.defineEntityReplacementText( "yacute", "\u00fd" );
        parser.defineEntityReplacementText( "thorn", "\u00fe" );
        parser.defineEntityReplacementText( "yuml", "\u00ff" );

        // ----------------------------------------------------------------------
        // Special entities
        // ----------------------------------------------------------------------

        parser.defineEntityReplacementText( "OElig", "\u0152" );
        parser.defineEntityReplacementText( "oelig", "\u0153" );
        parser.defineEntityReplacementText( "Scaron", "\u0160" );
        parser.defineEntityReplacementText( "scaron", "\u0161" );
        parser.defineEntityReplacementText( "Yuml", "\u0178" );
        parser.defineEntityReplacementText( "circ", "\u02c6" );
        parser.defineEntityReplacementText( "tilde", "\u02dc" );
        parser.defineEntityReplacementText( "ensp", "\u2002" );
        parser.defineEntityReplacementText( "emsp", "\u2003" );
        parser.defineEntityReplacementText( "thinsp", "\u2009" );
        parser.defineEntityReplacementText( "zwnj", "\u200c" );
        parser.defineEntityReplacementText( "zwj", "\u200d" );
        parser.defineEntityReplacementText( "lrm", "\u200e" );
        parser.defineEntityReplacementText( "rlm", "\u200f" );
        parser.defineEntityReplacementText( "ndash", "\u2013" );
        parser.defineEntityReplacementText( "mdash", "\u2014" );
        parser.defineEntityReplacementText( "lsquo", "\u2018" );
        parser.defineEntityReplacementText( "rsquo", "\u2019" );
        parser.defineEntityReplacementText( "sbquo", "\u201a" );
        parser.defineEntityReplacementText( "ldquo", "\u201c" );
        parser.defineEntityReplacementText( "rdquo", "\u201d" );
        parser.defineEntityReplacementText( "bdquo", "\u201e" );
        parser.defineEntityReplacementText( "dagger", "\u2020" );
        parser.defineEntityReplacementText( "Dagger", "\u2021" );
        parser.defineEntityReplacementText( "permil", "\u2030" );
        parser.defineEntityReplacementText( "lsaquo", "\u2039" );
        parser.defineEntityReplacementText( "rsaquo", "\u203a" );
        parser.defineEntityReplacementText( "euro", "\u20ac" );

        // ----------------------------------------------------------------------
        // Symbol entities
        // ----------------------------------------------------------------------

        parser.defineEntityReplacementText( "fnof", "\u0192" );
        parser.defineEntityReplacementText( "Alpha", "\u0391" );
        parser.defineEntityReplacementText( "Beta", "\u0392" );
        parser.defineEntityReplacementText( "Gamma", "\u0393" );
        parser.defineEntityReplacementText( "Delta", "\u0394" );
        parser.defineEntityReplacementText( "Epsilon", "\u0395" );
        parser.defineEntityReplacementText( "Zeta", "\u0396" );
        parser.defineEntityReplacementText( "Eta", "\u0397" );
        parser.defineEntityReplacementText( "Theta", "\u0398" );
        parser.defineEntityReplacementText( "Iota", "\u0399" );
        parser.defineEntityReplacementText( "Kappa", "\u039a" );
        parser.defineEntityReplacementText( "Lambda", "\u039b" );
        parser.defineEntityReplacementText( "Mu", "\u039c" );
        parser.defineEntityReplacementText( "Nu", "\u039d" );
        parser.defineEntityReplacementText( "Xi", "\u039e" );
        parser.defineEntityReplacementText( "Omicron", "\u039f" );
        parser.defineEntityReplacementText( "Pi", "\u03a0" );
        parser.defineEntityReplacementText( "Rho", "\u03a1" );
        parser.defineEntityReplacementText( "Sigma", "\u03a3" );
        parser.defineEntityReplacementText( "Tau", "\u03a4" );
        parser.defineEntityReplacementText( "Upsilon", "\u03a5" );
        parser.defineEntityReplacementText( "Phi", "\u03a6" );
        parser.defineEntityReplacementText( "Chi", "\u03a7" );
        parser.defineEntityReplacementText( "Psi", "\u03a8" );
        parser.defineEntityReplacementText( "Omega", "\u03a9" );
        parser.defineEntityReplacementText( "alpha", "\u03b1" );
        parser.defineEntityReplacementText( "beta", "\u03b2" );
        parser.defineEntityReplacementText( "gamma", "\u03b3" );
        parser.defineEntityReplacementText( "delta", "\u03b4" );
        parser.defineEntityReplacementText( "epsilon", "\u03b5" );
        parser.defineEntityReplacementText( "zeta", "\u03b6" );
        parser.defineEntityReplacementText( "eta", "\u03b7" );
        parser.defineEntityReplacementText( "theta", "\u03b8" );
        parser.defineEntityReplacementText( "iota", "\u03b9" );
        parser.defineEntityReplacementText( "kappa", "\u03ba" );
        parser.defineEntityReplacementText( "lambda", "\u03bb" );
        parser.defineEntityReplacementText( "mu", "\u03bc" );
        parser.defineEntityReplacementText( "nu", "\u03bd" );
        parser.defineEntityReplacementText( "xi", "\u03be" );
        parser.defineEntityReplacementText( "omicron", "\u03bf" );
        parser.defineEntityReplacementText( "pi", "\u03c0" );
        parser.defineEntityReplacementText( "rho", "\u03c1" );
        parser.defineEntityReplacementText( "sigmaf", "\u03c2" );
        parser.defineEntityReplacementText( "sigma", "\u03c3" );
        parser.defineEntityReplacementText( "tau", "\u03c4" );
        parser.defineEntityReplacementText( "upsilon", "\u03c5" );
        parser.defineEntityReplacementText( "phi", "\u03c6" );
        parser.defineEntityReplacementText( "chi", "\u03c7" );
        parser.defineEntityReplacementText( "psi", "\u03c8" );
        parser.defineEntityReplacementText( "omega", "\u03c9" );
        parser.defineEntityReplacementText( "thetasym", "\u03d1" );
        parser.defineEntityReplacementText( "upsih", "\u03d2" );
        parser.defineEntityReplacementText( "piv", "\u03d6" );
        parser.defineEntityReplacementText( "bull", "\u2022" );
        parser.defineEntityReplacementText( "hellip", "\u2026" );
        parser.defineEntityReplacementText( "prime", "\u2032" );
        parser.defineEntityReplacementText( "Prime", "\u2033" );
        parser.defineEntityReplacementText( "oline", "\u203e" );
        parser.defineEntityReplacementText( "frasl", "\u2044" );
        parser.defineEntityReplacementText( "weierp", "\u2118" );
        parser.defineEntityReplacementText( "image", "\u2111" );
        parser.defineEntityReplacementText( "real", "\u211c" );
        parser.defineEntityReplacementText( "trade", "\u2122" );
        parser.defineEntityReplacementText( "alefsym", "\u2135" );
        parser.defineEntityReplacementText( "larr", "\u2190" );
        parser.defineEntityReplacementText( "uarr", "\u2191" );
        parser.defineEntityReplacementText( "rarr", "\u2192" );
        parser.defineEntityReplacementText( "darr", "\u2193" );
        parser.defineEntityReplacementText( "harr", "\u2194" );
        parser.defineEntityReplacementText( "crarr", "\u21b5" );
        parser.defineEntityReplacementText( "lArr", "\u21d0" );
        parser.defineEntityReplacementText( "uArr", "\u21d1" );
        parser.defineEntityReplacementText( "rArr", "\u21d2" );
        parser.defineEntityReplacementText( "dArr", "\u21d3" );
        parser.defineEntityReplacementText( "hArr", "\u21d4" );
        parser.defineEntityReplacementText( "forall", "\u2200" );
        parser.defineEntityReplacementText( "part", "\u2202" );
        parser.defineEntityReplacementText( "exist", "\u2203" );
        parser.defineEntityReplacementText( "empty", "\u2205" );
        parser.defineEntityReplacementText( "nabla", "\u2207" );
        parser.defineEntityReplacementText( "isin", "\u2208" );
        parser.defineEntityReplacementText( "notin", "\u2209" );
        parser.defineEntityReplacementText( "ni", "\u220b" );
        parser.defineEntityReplacementText( "prod", "\u220f" );
        parser.defineEntityReplacementText( "sum", "\u2211" );
        parser.defineEntityReplacementText( "minus", "\u2212" );
        parser.defineEntityReplacementText( "lowast", "\u2217" );
        parser.defineEntityReplacementText( "radic", "\u221a" );
        parser.defineEntityReplacementText( "prop", "\u221d" );
        parser.defineEntityReplacementText( "infin", "\u221e" );
        parser.defineEntityReplacementText( "ang", "\u2220" );
        parser.defineEntityReplacementText( "and", "\u2227" );
        parser.defineEntityReplacementText( "or", "\u2228" );
        parser.defineEntityReplacementText( "cap", "\u2229" );
        parser.defineEntityReplacementText( "cup", "\u222a" );
        parser.defineEntityReplacementText( "int", "\u222b" );
        parser.defineEntityReplacementText( "there4", "\u2234" );
        parser.defineEntityReplacementText( "sim", "\u223c" );
        parser.defineEntityReplacementText( "cong", "\u2245" );
        parser.defineEntityReplacementText( "asymp", "\u2248" );
        parser.defineEntityReplacementText( "ne", "\u2260" );
        parser.defineEntityReplacementText( "equiv", "\u2261" );
        parser.defineEntityReplacementText( "le", "\u2264" );
        parser.defineEntityReplacementText( "ge", "\u2265" );
        parser.defineEntityReplacementText( "sub", "\u2282" );
        parser.defineEntityReplacementText( "sup", "\u2283" );
        parser.defineEntityReplacementText( "nsub", "\u2284" );
        parser.defineEntityReplacementText( "sube", "\u2286" );
        parser.defineEntityReplacementText( "supe", "\u2287" );
        parser.defineEntityReplacementText( "oplus", "\u2295" );
        parser.defineEntityReplacementText( "otimes", "\u2297" );
        parser.defineEntityReplacementText( "perp", "\u22a5" );
        parser.defineEntityReplacementText( "sdot", "\u22c5" );
        parser.defineEntityReplacementText( "lceil", "\u2308" );
        parser.defineEntityReplacementText( "rceil", "\u2309" );
        parser.defineEntityReplacementText( "lfloor", "\u230a" );
        parser.defineEntityReplacementText( "rfloor", "\u230b" );
        parser.defineEntityReplacementText( "lang", "\u2329" );
        parser.defineEntityReplacementText( "rang", "\u232a" );
        parser.defineEntityReplacementText( "loz", "\u25ca" );
        parser.defineEntityReplacementText( "spades", "\u2660" );
        parser.defineEntityReplacementText( "clubs", "\u2663" );
        parser.defineEntityReplacementText( "hearts", "\u2665" );
        parser.defineEntityReplacementText( "diams", "\u2666" );
    }

    /**
     * <p>
     *   Goes through a common list of possible html start tags. These include only tags that can go into
     *   the body of a xhtml document and so should be re-usable by different xhtml-based parsers.
     * </p>
     * <p>
     *   The currently handled tags are:
     * </p>
     * <p>
     *   <code>
     *      &lt;h2&gt;, &lt;h3&gt;, &lt;h4&gt;, &lt;h5&gt;, &lt;h6&gt;, &lt;p&gt;, &lt;pre&gt;,
     *      &lt;ul&gt;, &lt;ol&gt;, &lt;li&gt;, &lt;dl&gt;, &lt;dt&gt;, &lt;dd&gt;, &lt;b&gt;, &lt;strong&gt;,
     *      &lt;i&gt;, &lt;em&gt;, &lt;code&gt;, &lt;samp&gt;, &lt;tt&gt;, &lt;a&gt;, &lt;table&gt;, &lt;tr&gt;,
     *      &lt;th&gt;, &lt;td&gt;, &lt;caption&gt;, &lt;br/&gt;, &lt;hr/&gt;, &lt;img/&gt;.
     *   </code>
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, i.e. the tag was recognized, false otherwise.
     */
    protected boolean baseStartTag( XmlPullParser parser, Sink sink )
    {
        boolean visited = true;

        SinkEventAttributeSet attribs = getAttributesFromParser( parser );

        if ( parser.getName().equals( HtmlMarkup.H2.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_1, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H3.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_2, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H4.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_3, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H5.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_4, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.H6.toString() ) )
        {
            handleSectionStart( sink, Sink.SECTION_LEVEL_5, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.U.toString() ) )
        {
            attribs.addAttributes( SinkEventAttributeSet.Semantics.ANNOTATION );
            sink.inline( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.S.toString() )
                || parser.getName().equals( HtmlMarkup.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            attribs.addAttributes( SinkEventAttributeSet.Semantics.LINE_THROUGH );
            sink.inline( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.SUB.toString() ) )
        {
            attribs.addAttributes( SinkEventAttributeSet.Semantics.SUBSCRIPT );
            sink.inline( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.SUP.toString() ) )
        {
            attribs.addAttributes( SinkEventAttributeSet.Semantics.SUPERSCRIPT );
            sink.inline( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.P.toString() ) )
        {
            handlePStart( sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
        {
            visited = handleDivStart( parser, attribs, sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.PRE.toString() ) )
        {
            handlePreStart( attribs, sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.UL.toString() ) )
        {
            sink.list( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.OL.toString() ) )
        {
            handleOLStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.LI.toString() ) )
        {
            handleLIStart( sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DL.toString() ) )
        {
            sink.definitionList( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DT.toString() ) )
        {
            if ( hasDefinitionListItem )
            {
                // close previous listItem
                sink.definitionListItem_();
            }
            sink.definitionListItem( attribs );
            hasDefinitionListItem = true;
            sink.definedTerm( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.DD.toString() ) )
        {
            if ( !hasDefinitionListItem )
            {
                sink.definitionListItem( attribs );
            }
            sink.definition( attribs );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.B.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.STRONG.toString() ) ) )
        {
            sink.inline( SinkEventAttributeSet.Semantics.BOLD );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.I.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.EM.toString() ) ) )
        {
            handleFigureCaptionStart( sink, attribs );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.CODE.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.SAMP.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.TT.toString() ) ) )
        {
            sink.inline( SinkEventAttributeSet.Semantics.MONOSPACED );
        }
        else if ( parser.getName().equals( HtmlMarkup.A.toString() ) )
        {
            handleAStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TABLE.toString() ) )
        {
            handleTableStart( sink, attribs, parser );
        }
        else if ( parser.getName().equals( HtmlMarkup.TR.toString() ) )
        {
            sink.tableRow( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TH.toString() ) )
        {
            sink.tableHeaderCell( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.TD.toString() ) )
        {
            sink.tableCell( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.CAPTION.toString() ) )
        {
            sink.tableCaption( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.BR.toString() ) )
        {
            sink.lineBreak( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.HR.toString() ) )
        {
            sink.horizontalRule( attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.IMG.toString() ) )
        {
            handleImgStart( parser, sink, attribs );
        }
        else if ( parser.getName().equals( HtmlMarkup.SCRIPT.toString() )
            || parser.getName().equals( HtmlMarkup.STYLE.toString() ) )
        {
            handleUnknown( parser, sink, TAG_TYPE_START );
            scriptBlock = true;
        }
        else
        {
            visited = false;
        }

        return visited;
    }

    /**
     * <p>
     *   Goes through a common list of possible html end tags.
     *   These should be re-usable by different xhtml-based parsers.
     *   The tags handled here are the same as for {@link #baseStartTag(XmlPullParser,Sink)},
     *   except for the empty elements ({@code<br/>, <hr/>, <img/>}).
     * </p>
     *
     * @param parser A parser.
     * @param sink the sink to receive the events.
     * @return True if the event has been handled by this method, false otherwise.
     */
    protected boolean baseEndTag( XmlPullParser parser, Sink sink )
    {
        boolean visited = true;

        if ( parser.getName().equals( HtmlMarkup.P.toString() ) )
        {
            if ( !inFigure )
            {
                sink.paragraph_();
            }
        }
        else if ( parser.getName().equals( HtmlMarkup.U.toString() )
                || parser.getName().equals( HtmlMarkup.S.toString() )
                || parser.getName().equals( HtmlMarkup.STRIKE.toString() )
                || parser.getName().equals( "del" ) )
        {
            sink.inline_();
        }
        else if ( parser.getName().equals( HtmlMarkup.SUB.toString() )
                || parser.getName().equals( HtmlMarkup.SUP.toString() ) )
        {
            sink.inline_();
        }
        else if ( parser.getName().equals( HtmlMarkup.DIV.toString() ) )
        {
            if ( inFigure )
            {
                sink.figure_();
                this.inFigure = false;
            }
            else
            {
                visited = false;
            }
        }
        else if ( parser.getName().equals( HtmlMarkup.PRE.toString() ) )
        {
            verbatim_();

            sink.verbatim_();
        }
        else if ( parser.getName().equals( HtmlMarkup.UL.toString() ) )
        {
            sink.list_();
        }
        else if ( parser.getName().equals( HtmlMarkup.OL.toString() ) )
        {
            sink.numberedList_();
            orderedListDepth--;
        }
        else if ( parser.getName().equals( HtmlMarkup.LI.toString() ) )
        {
            handleListItemEnd( sink );
        }
        else if ( parser.getName().equals( HtmlMarkup.DL.toString() ) )
        {
            if ( hasDefinitionListItem )
            {
                sink.definitionListItem_();
                hasDefinitionListItem = false;
            }
            sink.definitionList_();
        }
        else if ( parser.getName().equals( HtmlMarkup.DT.toString() ) )
        {
            sink.definedTerm_();
        }
        else if ( parser.getName().equals( HtmlMarkup.DD.toString() ) )
        {
            sink.definition_();
            sink.definitionListItem_();
            hasDefinitionListItem = false;
        }
        else if ( ( parser.getName().equals( HtmlMarkup.B.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.STRONG.toString() ) ) )
        {
            sink.inline_();
        }
        else if ( ( parser.getName().equals( HtmlMarkup.I.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.EM.toString() ) ) )
        {
            handleFigureCaptionEnd( sink );
        }
        else if ( ( parser.getName().equals( HtmlMarkup.CODE.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.SAMP.toString() ) )
                || ( parser.getName().equals( HtmlMarkup.TT.toString() ) ) )
        {
            sink.inline_();
        }
        else if ( parser.getName().equals( HtmlMarkup.A.toString() ) )
        {
            handleAEnd( sink );
        }

        // ----------------------------------------------------------------------
        // Tables
        // ----------------------------------------------------------------------

        else if ( parser.getName().equals( HtmlMarkup.TABLE.toString() ) )
        {
            sink.tableRows_();

            sink.table_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TR.toString() ) )
        {
            sink.tableRow_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TH.toString() ) )
        {
            sink.tableHeaderCell_();
        }
        else if ( parser.getName().equals( HtmlMarkup.TD.toString() ) )
        {
            sink.tableCell_();
        }
        else if ( parser.getName().equals( HtmlMarkup.CAPTION.toString() ) )
        {
            sink.tableCaption_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H2.toString() ) )
        {
            sink.sectionTitle1_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H3.toString() ) )
        {
            sink.sectionTitle2_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H4.toString() ) )
        {
            sink.sectionTitle3_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H5.toString() ) )
        {
            sink.sectionTitle4_();
        }
        else if ( parser.getName().equals( HtmlMarkup.H6.toString() ) )
        {
            sink.sectionTitle5_();
        }
        else if ( parser.getName().equals( HtmlMarkup.SCRIPT.toString() )
            || parser.getName().equals( HtmlMarkup.STYLE.toString() ) )
        {
            handleUnknown( parser, sink, TAG_TYPE_END );

            scriptBlock = false;
        }
        else
        {
            visited = false;
        }

        return visited;
    }

    /**
     * {@inheritDoc}
     *
     * Just calls {@link #baseStartTag(XmlPullParser,Sink)}, this should be
     * overridden by implementing parsers to include additional tags.
     */
    protected void handleStartTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( !baseStartTag( parser, sink ) )
        {
            if ( getLog().isWarnEnabled() )
            {
                String position = "[" + parser.getLineNumber() + ":"
                    + parser.getColumnNumber() + "]";
                String tag = "<" + parser.getName() + ">";

                getLog().warn( "Unrecognized xml tag: " + tag + " at " + position );
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Just calls {@link #baseEndTag(XmlPullParser,Sink)}, this should be
     * overridden by implementing parsers to include additional tags.
     */
    protected void handleEndTag( XmlPullParser parser, Sink sink )
        throws XmlPullParserException, MacroExecutionException
    {
        if ( !baseEndTag( parser, sink ) )
        {
            // unrecognized tag is already logged in StartTag
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleText( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        /*
         * NOTE: Don't do any whitespace trimming here. Whitespace normalization has already been performed by the
         * parser so any whitespace that makes it here is significant.
         *
         * NOTE: text within script tags is ignored, scripting code should be embedded in CDATA.
         */
        if ( StringUtils.isNotEmpty( text ) && !isScriptBlock() )
        {
            sink.text( text );
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleComment( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        if ( "PB".equals( text.trim() ) )
        {
            sink.pageBreak();
        }
        else
        {
            if ( isEmitComments() )
            {
                sink.comment( text );
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void handleCdsect( XmlPullParser parser, Sink sink )
        throws XmlPullParserException
    {
        String text = getText( parser );

        if ( isScriptBlock() )
        {
            sink.unknown( CDATA, new Object[] { CDATA_TYPE, text }, null );
        }
        else
        {
            sink.text( text );
        }
    }

    /**
     * Make sure sections are nested consecutively.
     *
     * <p>
     * HTML doesn't have any sections, only sectionTitles (&lt;h2&gt; etc), that means we have to
     * open close any sections that are missing in between.
     * </p>
     *
     * <p>
     * For instance, if the following sequence is parsed:
     * </p>
     * <pre>
     * &lt;h3&gt;&lt;/h3&gt;
     * &lt;h6&gt;&lt;/h6&gt;
     * </pre>
     * <p>
     * we have to insert two section starts before we open the <code>&lt;h6&gt;</code>.
     * In the following sequence
     * </p>
     * <pre>
     * &lt;h6&gt;&lt;/h6&gt;
     * &lt;h3&gt;&lt;/h3&gt;
     * </pre>
     * <p>
     * we have to close two sections before we open the <code>&lt;h3&gt;</code>.
     * </p>
     *
     * <p>The current level is set to newLevel afterwards.</p>
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    protected void consecutiveSections( int newLevel, Sink sink )
    {
        closeOpenSections( newLevel, sink );
        openMissingSections( newLevel, sink );

        this.sectionLevel = newLevel;
    }

    /**
     * Close open sections.
     *
     * @param newLevel the new section level, all upper levels have to be closed.
     * @param sink the sink to receive the events.
     */
    private void closeOpenSections( int newLevel, Sink sink )
    {
        while ( this.sectionLevel >= newLevel )
        {
            if ( sectionLevel == Sink.SECTION_LEVEL_5 )
            {
                sink.section5_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4 )
            {
                sink.section4_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3 )
            {
                sink.section3_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_2 )
            {
                sink.section2_();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_1 )
            {
                sink.section1_();
            }

            this.sectionLevel--;
        }
    }

    /**
     * Open missing sections.
     *
     * @param newLevel the new section level, all lower levels have to be opened.
     * @param sink the sink to receive the events.
     */
    private void openMissingSections( int newLevel, Sink sink )
    {
        while ( this.sectionLevel < newLevel - 1 )
        {
            this.sectionLevel++;

            if ( sectionLevel == Sink.SECTION_LEVEL_5 )
            {
                sink.section5();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_4 )
            {
                sink.section4();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_3 )
            {
                sink.section3();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_2 )
            {
                sink.section2();
            }
            else if ( sectionLevel == Sink.SECTION_LEVEL_1 )
            {
                sink.section1();
            }
        }
    }

    /**
     * Return the current section level.
     *
     * @return the current section level.
     */
    protected int getSectionLevel()
    {
        return this.sectionLevel;
    }

    /**
     * Set the current section level.
     *
     * @param newLevel the new section level.
     */
    protected void setSectionLevel( int newLevel )
    {
        this.sectionLevel = newLevel;
    }

    /**
     * Stop verbatim mode.
     */
    protected void verbatim_()
    {
        this.inVerbatim = false;
    }

    /**
     * Start verbatim mode.
     */
    protected void verbatim()
    {
        this.inVerbatim = true;
    }

    /**
     * Checks if we are currently inside a &lt;pre&gt; tag.
     *
     * @return true if we are currently in verbatim mode.
     */
    protected boolean isVerbatim()
    {
        return this.inVerbatim;
    }

    /**
     * Checks if we are currently inside a &lt;script&gt; tag.
     *
     * @return true if we are currently inside <code>&lt;script&gt;</code> tags.
     *
     * @since 1.1.1.
     */
    protected boolean isScriptBlock()
    {
        return this.scriptBlock;
    }

    /**
     * Checks if the given id is a valid Doxia id and if not, returns a transformed one.
     *
     * @param id The id to validate.
     * @return A transformed id or the original id if it was already valid.
     * @see DoxiaUtils#encodeId(String)
     */
    protected String validAnchor( String id )
    {
        if ( !DoxiaUtils.isValidId( id ) )
        {
            String linkAnchor = DoxiaUtils.encodeId( id, true );

            String msg = "Modified invalid link: '" + id + "' to '" + linkAnchor + "'";
            logMessage( "modifiedLink", msg );

            return linkAnchor;
        }

        return id;
    }

    /** {@inheritDoc} */
    @Override
    protected void init()
    {
        super.init();

        this.scriptBlock = false;
        this.isLink = false;
        this.isAnchor = false;
        this.orderedListDepth = 0;
        this.sectionLevel = 0;
        this.inVerbatim = false;
        this.inFigure = false;
        this.warnMessages = null;
    }

    private void handleAEnd( Sink sink )
    {
        if ( isLink )
        {
            sink.link_();
            isLink = false;
        }
        else if ( isAnchor )
        {
            sink.anchor_();
            isAnchor = false;
        }
    }

    private void handleAStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        String href = parser.getAttributeValue( null, Attribute.HREF.toString() );

        if ( href != null )
        {
            int hashIndex = href.indexOf( '#' );
            if ( hashIndex != -1 && !DoxiaUtils.isExternalLink( href ) )
            {
                String hash = href.substring( hashIndex + 1 );

                if ( !DoxiaUtils.isValidId( hash ) )
                {
                    href = href.substring( 0, hashIndex ) + "#" + DoxiaUtils.encodeId( hash, true );

                    String msg = "Modified invalid link: '" + hash + "' to '" + href + "'";
                    logMessage( "modifiedLink", msg );
                }
            }
            sink.link( href, attribs );
            isLink = true;
        }
        else
        {
            String name = parser.getAttributeValue( null, Attribute.NAME.toString() );

            if ( name != null )
            {
                sink.anchor( validAnchor( name ), attribs );
                isAnchor = true;
            }
            else
            {
                String id = parser.getAttributeValue( null, Attribute.ID.toString() );
                if ( id != null )
                {
                    sink.anchor( validAnchor( id ), attribs );
                    isAnchor = true;
                }
            }
        }
    }

    private boolean handleDivStart( XmlPullParser parser, SinkEventAttributeSet attribs, Sink sink )
    {
        boolean visited = true;

        String divclass = parser.getAttributeValue( null, Attribute.CLASS.toString() );

        if ( "figure".equals( divclass ) )
        {
            this.inFigure = true;
            SinkEventAttributeSet atts = new SinkEventAttributeSet( attribs );
            atts.removeAttribute( SinkEventAttributes.CLASS );
            sink.figure( atts );
        }
        else
        {
            visited = false;
        }

        return visited;
    }

    private void handleFigureCaptionEnd( Sink sink )
    {
        if ( inFigure )
        {
            sink.figureCaption_();
        }
        else
        {
            sink.inline_();
        }
    }

    private void handleFigureCaptionStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( inFigure )
        {
            sink.figureCaption( attribs );
        }
        else
        {
            sink.inline( SinkEventAttributeSet.Semantics.ITALIC );
        }
    }

    private void handleImgStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        String src = parser.getAttributeValue( null, Attribute.SRC.toString() );

        if ( src != null )
        {
            sink.figureGraphics( src, attribs );
        }
    }

    private void handleLIStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( orderedListDepth == 0 )
        {
            sink.listItem( attribs );
        }
        else
        {
            sink.numberedListItem( attribs );
        }
    }

    private void handleListItemEnd( Sink sink )
    {
        if ( orderedListDepth == 0 )
        {
            sink.listItem_();
        }
        else
        {
            sink.numberedListItem_();
        }
    }

    private void handleOLStart( XmlPullParser parser, Sink sink, SinkEventAttributeSet attribs )
    {
        int numbering = Sink.NUMBERING_DECIMAL;
        // this will have to be generalized if we handle styles
        String style = parser.getAttributeValue( null, Attribute.STYLE.toString() );

        if ( style != null )
        {
            switch ( style )
            {
                case "list-style-type: upper-alpha":
                    numbering = Sink.NUMBERING_UPPER_ALPHA;
                    break;
                case "list-style-type: lower-alpha":
                    numbering = Sink.NUMBERING_LOWER_ALPHA;
                    break;
                case "list-style-type: upper-roman":
                    numbering = Sink.NUMBERING_UPPER_ROMAN;
                    break;
                case "list-style-type: lower-roman":
                    numbering = Sink.NUMBERING_LOWER_ROMAN;
                    break;
                case "list-style-type: decimal":
                    numbering = Sink.NUMBERING_DECIMAL;
                    break;
                default:
                    // ignore all other
            }
        }

        sink.numberedList( numbering, attribs );
        orderedListDepth++;
    }

    private void handlePStart( Sink sink, SinkEventAttributeSet attribs )
    {
        if ( !inFigure )
        {
            sink.paragraph( attribs );
        }
    }

    /*
     * The PRE element tells visual user agents that the enclosed text is
     * "preformatted". When handling preformatted text, visual user agents:
     * - May leave white space intact.
     * - May render text with a fixed-pitch font.
     * - May disable automatic word wrap.
     * - Must not disable bidirectional processing.
     * Non-visual user agents are not required to respect extra white space
     * in the content of a PRE element.
     */
    private void handlePreStart( SinkEventAttributeSet attribs, Sink sink )
    {
        verbatim();
        sink.verbatim( attribs );
    }

    private void handleSectionStart( Sink sink, int level, SinkEventAttributeSet attribs )
    {
        consecutiveSections( level, sink );
        sink.section( level, attribs );
        sink.sectionTitle( level, attribs );
    }

    private void handleTableStart( Sink sink, SinkEventAttributeSet attribs, XmlPullParser parser )
    {
        sink.table( attribs );
        String border = parser.getAttributeValue( null, Attribute.BORDER.toString() );
        boolean grid = true;

        if ( border == null || "0".equals( border ) )
        {
            grid = false;
        }

        String align = parser.getAttributeValue( null, Attribute.ALIGN.toString() );
        int[] justif = {Sink.JUSTIFY_LEFT};

        if ( "center".equals( align ) )
        {
            justif[0] = Sink.JUSTIFY_CENTER;
        }
        else if ( "right".equals( align ) )
        {
            justif[0] = Sink.JUSTIFY_RIGHT;
        }

        sink.tableRows( justif, grid );
    }

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #parse(Reader, Sink)
     * @since 1.1.1
     */
    private void logMessage( String key, String msg )
    {
        final String log = "[XHTML Parser] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( log );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap<>();
        }

        Set<String> set = warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet<>();
        }
        set.add( log );
        warnMessages.put( key, set );
    }

    /**
     * @since 1.1.1
     */
    private void logWarnings()
    {
        if ( getLog().isWarnEnabled() && this.warnMessages != null && !isSecondParsing() )
        {
            for ( Map.Entry<String, Set<String>> entry : this.warnMessages.entrySet() )
            {
                for ( String msg : entry.getValue() )
                {
                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }
    }
}
