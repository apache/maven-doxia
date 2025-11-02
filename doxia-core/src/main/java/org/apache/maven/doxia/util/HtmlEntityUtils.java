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
package org.apache.maven.doxia.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for escaping and unescaping HTML/XML entities.
 * This class provides methods that were previously supplied by Apache Commons Text.
 *
 * @since 2.1.0
 */
public class HtmlEntityUtils {

    private static final Map<String, String> HTML4_ENTITIES = new HashMap<>();
    private static final Map<String, String> XML_ENTITIES = new HashMap<>();

    static {
        // XML entities (subset also used in HTML)
        XML_ENTITIES.put("quot", "\"");
        XML_ENTITIES.put("amp", "&");
        XML_ENTITIES.put("lt", "<");
        XML_ENTITIES.put("gt", ">");
        XML_ENTITIES.put("apos", "'");

        // Initialize HTML4 entities with common XML entities (but NOT apos)
        HTML4_ENTITIES.put("quot", "\"");
        HTML4_ENTITIES.put("amp", "&");
        HTML4_ENTITIES.put("lt", "<");
        HTML4_ENTITIES.put("gt", ">");

        // Add common HTML4 entities
        HTML4_ENTITIES.put("nbsp", "\u00A0");
        HTML4_ENTITIES.put("iexcl", "\u00A1");
        HTML4_ENTITIES.put("cent", "\u00A2");
        HTML4_ENTITIES.put("pound", "\u00A3");
        HTML4_ENTITIES.put("curren", "\u00A4");
        HTML4_ENTITIES.put("yen", "\u00A5");
        HTML4_ENTITIES.put("brvbar", "\u00A6");
        HTML4_ENTITIES.put("sect", "\u00A7");
        HTML4_ENTITIES.put("uml", "\u00A8");
        HTML4_ENTITIES.put("copy", "\u00A9");
        HTML4_ENTITIES.put("ordf", "\u00AA");
        HTML4_ENTITIES.put("laquo", "\u00AB");
        HTML4_ENTITIES.put("not", "\u00AC");
        HTML4_ENTITIES.put("shy", "\u00AD");
        HTML4_ENTITIES.put("reg", "\u00AE");
        HTML4_ENTITIES.put("macr", "\u00AF");
        HTML4_ENTITIES.put("deg", "\u00B0");
        HTML4_ENTITIES.put("plusmn", "\u00B1");
        HTML4_ENTITIES.put("sup2", "\u00B2");
        HTML4_ENTITIES.put("sup3", "\u00B3");
        HTML4_ENTITIES.put("acute", "\u00B4");
        HTML4_ENTITIES.put("micro", "\u00B5");
        HTML4_ENTITIES.put("para", "\u00B6");
        HTML4_ENTITIES.put("middot", "\u00B7");
        HTML4_ENTITIES.put("cedil", "\u00B8");
        HTML4_ENTITIES.put("sup1", "\u00B9");
        HTML4_ENTITIES.put("ordm", "\u00BA");
        HTML4_ENTITIES.put("raquo", "\u00BB");
        HTML4_ENTITIES.put("frac14", "\u00BC");
        HTML4_ENTITIES.put("frac12", "\u00BD");
        HTML4_ENTITIES.put("frac34", "\u00BE");
        HTML4_ENTITIES.put("iquest", "\u00BF");
        HTML4_ENTITIES.put("Agrave", "\u00C0");
        HTML4_ENTITIES.put("Aacute", "\u00C1");
        HTML4_ENTITIES.put("Acirc", "\u00C2");
        HTML4_ENTITIES.put("Atilde", "\u00C3");
        HTML4_ENTITIES.put("Auml", "\u00C4");
        HTML4_ENTITIES.put("Aring", "\u00C5");
        HTML4_ENTITIES.put("AElig", "\u00C6");
        HTML4_ENTITIES.put("Ccedil", "\u00C7");
        HTML4_ENTITIES.put("Egrave", "\u00C8");
        HTML4_ENTITIES.put("Eacute", "\u00C9");
        HTML4_ENTITIES.put("Ecirc", "\u00CA");
        HTML4_ENTITIES.put("Euml", "\u00CB");
        HTML4_ENTITIES.put("Igrave", "\u00CC");
        HTML4_ENTITIES.put("Iacute", "\u00CD");
        HTML4_ENTITIES.put("Icirc", "\u00CE");
        HTML4_ENTITIES.put("Iuml", "\u00CF");
        HTML4_ENTITIES.put("ETH", "\u00D0");
        HTML4_ENTITIES.put("Ntilde", "\u00D1");
        HTML4_ENTITIES.put("Ograve", "\u00D2");
        HTML4_ENTITIES.put("Oacute", "\u00D3");
        HTML4_ENTITIES.put("Ocirc", "\u00D4");
        HTML4_ENTITIES.put("Otilde", "\u00D5");
        HTML4_ENTITIES.put("Ouml", "\u00D6");
        HTML4_ENTITIES.put("times", "\u00D7");
        HTML4_ENTITIES.put("Oslash", "\u00D8");
        HTML4_ENTITIES.put("Ugrave", "\u00D9");
        HTML4_ENTITIES.put("Uacute", "\u00DA");
        HTML4_ENTITIES.put("Ucirc", "\u00DB");
        HTML4_ENTITIES.put("Uuml", "\u00DC");
        HTML4_ENTITIES.put("Yacute", "\u00DD");
        HTML4_ENTITIES.put("THORN", "\u00DE");
        HTML4_ENTITIES.put("szlig", "\u00DF");
        HTML4_ENTITIES.put("agrave", "\u00E0");
        HTML4_ENTITIES.put("aacute", "\u00E1");
        HTML4_ENTITIES.put("acirc", "\u00E2");
        HTML4_ENTITIES.put("atilde", "\u00E3");
        HTML4_ENTITIES.put("auml", "\u00E4");
        HTML4_ENTITIES.put("aring", "\u00E5");
        HTML4_ENTITIES.put("aelig", "\u00E6");
        HTML4_ENTITIES.put("ccedil", "\u00E7");
        HTML4_ENTITIES.put("egrave", "\u00E8");
        HTML4_ENTITIES.put("eacute", "\u00E9");
        HTML4_ENTITIES.put("ecirc", "\u00EA");
        HTML4_ENTITIES.put("euml", "\u00EB");
        HTML4_ENTITIES.put("igrave", "\u00EC");
        HTML4_ENTITIES.put("iacute", "\u00ED");
        HTML4_ENTITIES.put("icirc", "\u00EE");
        HTML4_ENTITIES.put("iuml", "\u00EF");
        HTML4_ENTITIES.put("eth", "\u00F0");
        HTML4_ENTITIES.put("ntilde", "\u00F1");
        HTML4_ENTITIES.put("ograve", "\u00F2");
        HTML4_ENTITIES.put("oacute", "\u00F3");
        HTML4_ENTITIES.put("ocirc", "\u00F4");
        HTML4_ENTITIES.put("otilde", "\u00F5");
        HTML4_ENTITIES.put("ouml", "\u00F6");
        HTML4_ENTITIES.put("divide", "\u00F7");
        HTML4_ENTITIES.put("oslash", "\u00F8");
        HTML4_ENTITIES.put("ugrave", "\u00F9");
        HTML4_ENTITIES.put("uacute", "\u00FA");
        HTML4_ENTITIES.put("ucirc", "\u00FB");
        HTML4_ENTITIES.put("uuml", "\u00FC");
        HTML4_ENTITIES.put("yacute", "\u00FD");
        HTML4_ENTITIES.put("thorn", "\u00FE");
        HTML4_ENTITIES.put("yuml", "\u00FF");
        // Additional common entities
        HTML4_ENTITIES.put("OElig", "\u0152");
        HTML4_ENTITIES.put("oelig", "\u0153");
        HTML4_ENTITIES.put("Scaron", "\u0160");
        HTML4_ENTITIES.put("scaron", "\u0161");
        HTML4_ENTITIES.put("Yuml", "\u0178");
        HTML4_ENTITIES.put("fnof", "\u0192");
        HTML4_ENTITIES.put("circ", "\u02C6");
        HTML4_ENTITIES.put("tilde", "\u02DC");
        HTML4_ENTITIES.put("Alpha", "\u0391");
        HTML4_ENTITIES.put("Beta", "\u0392");
        HTML4_ENTITIES.put("Gamma", "\u0393");
        HTML4_ENTITIES.put("Delta", "\u0394");
        HTML4_ENTITIES.put("Epsilon", "\u0395");
        HTML4_ENTITIES.put("Zeta", "\u0396");
        HTML4_ENTITIES.put("Eta", "\u0397");
        HTML4_ENTITIES.put("Theta", "\u0398");
        HTML4_ENTITIES.put("Iota", "\u0399");
        HTML4_ENTITIES.put("Kappa", "\u039A");
        HTML4_ENTITIES.put("Lambda", "\u039B");
        HTML4_ENTITIES.put("Mu", "\u039C");
        HTML4_ENTITIES.put("Nu", "\u039D");
        HTML4_ENTITIES.put("Xi", "\u039E");
        HTML4_ENTITIES.put("Omicron", "\u039F");
        HTML4_ENTITIES.put("Pi", "\u03A0");
        HTML4_ENTITIES.put("Rho", "\u03A1");
        HTML4_ENTITIES.put("Sigma", "\u03A3");
        HTML4_ENTITIES.put("Tau", "\u03A4");
        HTML4_ENTITIES.put("Upsilon", "\u03A5");
        HTML4_ENTITIES.put("Phi", "\u03A6");
        HTML4_ENTITIES.put("Chi", "\u03A7");
        HTML4_ENTITIES.put("Psi", "\u03A8");
        HTML4_ENTITIES.put("Omega", "\u03A9");
        HTML4_ENTITIES.put("alpha", "\u03B1");
        HTML4_ENTITIES.put("beta", "\u03B2");
        HTML4_ENTITIES.put("gamma", "\u03B3");
        HTML4_ENTITIES.put("delta", "\u03B4");
        HTML4_ENTITIES.put("epsilon", "\u03B5");
        HTML4_ENTITIES.put("zeta", "\u03B6");
        HTML4_ENTITIES.put("eta", "\u03B7");
        HTML4_ENTITIES.put("theta", "\u03B8");
        HTML4_ENTITIES.put("iota", "\u03B9");
        HTML4_ENTITIES.put("kappa", "\u03BA");
        HTML4_ENTITIES.put("lambda", "\u03BB");
        HTML4_ENTITIES.put("mu", "\u03BC");
        HTML4_ENTITIES.put("nu", "\u03BD");
        HTML4_ENTITIES.put("xi", "\u03BE");
        HTML4_ENTITIES.put("omicron", "\u03BF");
        HTML4_ENTITIES.put("pi", "\u03C0");
        HTML4_ENTITIES.put("rho", "\u03C1");
        HTML4_ENTITIES.put("sigmaf", "\u03C2");
        HTML4_ENTITIES.put("sigma", "\u03C3");
        HTML4_ENTITIES.put("tau", "\u03C4");
        HTML4_ENTITIES.put("upsilon", "\u03C5");
        HTML4_ENTITIES.put("phi", "\u03C6");
        HTML4_ENTITIES.put("chi", "\u03C7");
        HTML4_ENTITIES.put("psi", "\u03C8");
        HTML4_ENTITIES.put("omega", "\u03C9");
        HTML4_ENTITIES.put("thetasym", "\u03D1");
        HTML4_ENTITIES.put("upsih", "\u03D2");
        HTML4_ENTITIES.put("piv", "\u03D6");
        HTML4_ENTITIES.put("ensp", "\u2002");
        HTML4_ENTITIES.put("emsp", "\u2003");
        HTML4_ENTITIES.put("thinsp", "\u2009");
        HTML4_ENTITIES.put("zwnj", "\u200C");
        HTML4_ENTITIES.put("zwj", "\u200D");
        HTML4_ENTITIES.put("lrm", "\u200E");
        HTML4_ENTITIES.put("rlm", "\u200F");
        HTML4_ENTITIES.put("ndash", "\u2013");
        HTML4_ENTITIES.put("mdash", "\u2014");
        HTML4_ENTITIES.put("lsquo", "\u2018");
        HTML4_ENTITIES.put("rsquo", "\u2019");
        HTML4_ENTITIES.put("sbquo", "\u201A");
        HTML4_ENTITIES.put("ldquo", "\u201C");
        HTML4_ENTITIES.put("rdquo", "\u201D");
        HTML4_ENTITIES.put("bdquo", "\u201E");
        HTML4_ENTITIES.put("dagger", "\u2020");
        HTML4_ENTITIES.put("Dagger", "\u2021");
        HTML4_ENTITIES.put("bull", "\u2022");
        HTML4_ENTITIES.put("hellip", "\u2026");
        HTML4_ENTITIES.put("permil", "\u2030");
        HTML4_ENTITIES.put("prime", "\u2032");
        HTML4_ENTITIES.put("Prime", "\u2033");
        HTML4_ENTITIES.put("lsaquo", "\u2039");
        HTML4_ENTITIES.put("rsaquo", "\u203A");
        HTML4_ENTITIES.put("oline", "\u203E");
        HTML4_ENTITIES.put("frasl", "\u2044");
        HTML4_ENTITIES.put("euro", "\u20AC");
        HTML4_ENTITIES.put("image", "\u2111");
        HTML4_ENTITIES.put("weierp", "\u2118");
        HTML4_ENTITIES.put("real", "\u211C");
        HTML4_ENTITIES.put("trade", "\u2122");
        HTML4_ENTITIES.put("alefsym", "\u2135");
        HTML4_ENTITIES.put("larr", "\u2190");
        HTML4_ENTITIES.put("uarr", "\u2191");
        HTML4_ENTITIES.put("rarr", "\u2192");
        HTML4_ENTITIES.put("darr", "\u2193");
        HTML4_ENTITIES.put("harr", "\u2194");
        HTML4_ENTITIES.put("crarr", "\u21B5");
        HTML4_ENTITIES.put("lArr", "\u21D0");
        HTML4_ENTITIES.put("uArr", "\u21D1");
        HTML4_ENTITIES.put("rArr", "\u21D2");
        HTML4_ENTITIES.put("dArr", "\u21D3");
        HTML4_ENTITIES.put("hArr", "\u21D4");
        HTML4_ENTITIES.put("forall", "\u2200");
        HTML4_ENTITIES.put("part", "\u2202");
        HTML4_ENTITIES.put("exist", "\u2203");
        HTML4_ENTITIES.put("empty", "\u2205");
        HTML4_ENTITIES.put("nabla", "\u2207");
        HTML4_ENTITIES.put("isin", "\u2208");
        HTML4_ENTITIES.put("notin", "\u2209");
        HTML4_ENTITIES.put("ni", "\u220B");
        HTML4_ENTITIES.put("prod", "\u220F");
        HTML4_ENTITIES.put("sum", "\u2211");
        HTML4_ENTITIES.put("minus", "\u2212");
        HTML4_ENTITIES.put("lowast", "\u2217");
        HTML4_ENTITIES.put("radic", "\u221A");
        HTML4_ENTITIES.put("prop", "\u221D");
        HTML4_ENTITIES.put("infin", "\u221E");
        HTML4_ENTITIES.put("ang", "\u2220");
        HTML4_ENTITIES.put("and", "\u2227");
        HTML4_ENTITIES.put("or", "\u2228");
        HTML4_ENTITIES.put("cap", "\u2229");
        HTML4_ENTITIES.put("cup", "\u222A");
        HTML4_ENTITIES.put("int", "\u222B");
        HTML4_ENTITIES.put("there4", "\u2234");
        HTML4_ENTITIES.put("sim", "\u223C");
        HTML4_ENTITIES.put("cong", "\u2245");
        HTML4_ENTITIES.put("asymp", "\u2248");
        HTML4_ENTITIES.put("ne", "\u2260");
        HTML4_ENTITIES.put("equiv", "\u2261");
        HTML4_ENTITIES.put("le", "\u2264");
        HTML4_ENTITIES.put("ge", "\u2265");
        HTML4_ENTITIES.put("sub", "\u2282");
        HTML4_ENTITIES.put("sup", "\u2283");
        HTML4_ENTITIES.put("nsub", "\u2284");
        HTML4_ENTITIES.put("sube", "\u2286");
        HTML4_ENTITIES.put("supe", "\u2287");
        HTML4_ENTITIES.put("oplus", "\u2295");
        HTML4_ENTITIES.put("otimes", "\u2297");
        HTML4_ENTITIES.put("perp", "\u22A5");
        HTML4_ENTITIES.put("sdot", "\u22C5");
        HTML4_ENTITIES.put("lceil", "\u2308");
        HTML4_ENTITIES.put("rceil", "\u2309");
        HTML4_ENTITIES.put("lfloor", "\u230A");
        HTML4_ENTITIES.put("rfloor", "\u230B");
        HTML4_ENTITIES.put("lang", "\u2329");
        HTML4_ENTITIES.put("rang", "\u232A");
        HTML4_ENTITIES.put("loz", "\u25CA");
        HTML4_ENTITIES.put("spades", "\u2660");
        HTML4_ENTITIES.put("clubs", "\u2663");
        HTML4_ENTITIES.put("hearts", "\u2665");
        HTML4_ENTITIES.put("diams", "\u2666");
    }

    private HtmlEntityUtils() {
        // utility class
    }

    /**
     * Unescapes XML entities in a string.
     *
     * @param text the text to unescape
     * @return the unescaped text
     */
    public static String unescapeXml(String text) {
        return unescapeEntities(text, XML_ENTITIES);
    }

    /**
     * Unescapes HTML4 entities in a string.
     *
     * @param text the text to unescape
     * @return the unescaped text
     */
    public static String unescapeHtml4(String text) {
        return unescapeEntities(text, HTML4_ENTITIES);
    }

    private static String unescapeEntities(String text, Map<String, String> entities) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int len = text.length();

        while (i < len) {
            char c = text.charAt(i);
            if (c == '&') {
                int end = text.indexOf(';', i + 1);
                if (end != -1) {
                    String entityContent = text.substring(i + 1, end);
                    String replacement = null;

                    // Check for numeric entity (&#123; or &#xAB;)
                    if (entityContent.length() > 0 && entityContent.charAt(0) == '#') {
                        try {
                            if (entityContent.length() > 1 && entityContent.charAt(1) == 'x') {
                                // Hexadecimal
                                int codePoint = Integer.parseInt(entityContent.substring(2), 16);
                                replacement = new String(Character.toChars(codePoint));
                            } else {
                                // Decimal
                                int codePoint = Integer.parseInt(entityContent.substring(1));
                                replacement = new String(Character.toChars(codePoint));
                            }
                        } catch (NumberFormatException e) {
                            // Invalid numeric entity, leave as is
                        } catch (IllegalArgumentException e) {
                            // Invalid code point, leave as is
                        }
                    } else {
                        // Named entity
                        replacement = entities.get(entityContent);
                    }

                    if (replacement != null) {
                        result.append(replacement);
                        i = end + 1;
                        continue;
                    }
                }
            }
            result.append(c);
            i++;
        }

        return result.toString();
    }
}
