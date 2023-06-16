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

import javax.imageio.ImageIO;
import javax.swing.text.MutableAttributeSet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;

/**
 * General Doxia utility methods. The methods in this class should not assume
 * any specific Doxia module or document format.
 *
 * @author ltheussl
 * @since 1.1
 */
public class DoxiaUtils {
    /**
     * Checks if the given string corresponds to an internal link,
     * ie it is a link to an anchor within the same document.
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     * @return True if the link starts with "#".
     *
     * @throws NullPointerException if link is null.
     * @see #isExternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isInternalLink(final String link) {
        return link.startsWith("#");
    }

    /**
     * Checks if the given string corresponds to an external URI,
     * ie is not a link within the same document nor a relative link
     * to another document (a local link) of the same site.
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     * @return True if the link (ignoring case) starts with either "http:/",
     * "https:/", "ftp:/", "mailto:", "file:/", or contains the string "://".
     * Note that Windows style separators "\" are not allowed
     * for URIs, see  http://www.ietf.org/rfc/rfc2396.txt , section 2.4.3.
     *
     * @throws NullPointerException if link is null.
     *
     * @see #isInternalLink(String)
     * @see #isLocalLink(String)
     */
    public static boolean isExternalLink(final String link) {
        String text = link.toLowerCase(Locale.ENGLISH);

        return (text.startsWith("http:/")
                || text.startsWith("https:/")
                || text.startsWith("ftp:/")
                || text.startsWith("mailto:")
                || text.startsWith("file:/")
                || text.contains("://"));
    }

    /**
     * Checks if the given string corresponds to a relative link to another document
     * within the same site, ie it is neither an {@link #isInternalLink(String) internal}
     * nor an {@link #isExternalLink(String) external} link.
     * If link is not null, then exactly one of the three methods
     * {@link #isInternalLink(java.lang.String)}, {@link #isExternalLink(java.lang.String)} and
     * {@link #isLocalLink(java.lang.String)} will return true.
     *
     * @param link The link to check. Not null.
     * @return True if the link is neither an external nor an internal link.
     *
     * @throws NullPointerException if link is null.
     *
     * @see #isExternalLink(String)
     * @see #isInternalLink(String)
     */
    public static boolean isLocalLink(final String link) {
        return (!isExternalLink(link) && !isInternalLink(link));
    }

    /**
     * Construct a valid Doxia id.
     *
     * <p>
     *   A valid Doxia id corresponds to an XML id which is a {code NCName} which is in turn identical
     *   to a <a href="https://www.w3.org/TR/REC-xml/#NT-Name">{@code Name}</a>, but without a colon
     *   and without any character above {@code 0x7F}.
     * </p>
     * <p>
     *   To achieve this we need to convert the <i>id</i> String. Two conversions
     *   are necessary and one is done to get prettier ids:
     * </p>
     * <ol>
     *   <li>Trim with {@link String#trim()} before starting to process,</li>
     *   <li>if the first character is not a {@code NameStartChar} prepend the letter 'a',</li>
     *   <li>any space character ({@code 0x20}) is replaced with an underscore,</li>
     *   <li>
     *     any character not matching the above pattern is either dropped,
     *     or replaced with its UTF-8 encoding where each byte is prepended with a dot.
     *   </li>
     * </ol>
     *
     * <p>
     * Here are some examples:
     * </p>
     * <pre>
     * DoxiaUtils.encodeId(null)        = null
     * DoxiaUtils.encodeId("")          = null
     * DoxiaUtils.encodeId("  ")        = null
     * DoxiaUtils.encodeId(" _ ")       = "_"
     * DoxiaUtils.encodeId("1")         = "a1"
     * DoxiaUtils.encodeId("1anchor")   = "a1anchor"
     * DoxiaUtils.encodeId("_anchor")   = "_anchor"
     * DoxiaUtils.encodeId("a b-c123 ") = "a_b-c123"
     * DoxiaUtils.encodeId("   anchor") = "anchor"
     * DoxiaUtils.encodeId("myAnchor")  = "myAnchor"
     * DoxiaUtils.encodeId("€")         = "a.E2.82.AC"
     * </pre>
     *
     * @param text The text to be encoded.
     *      May be null, empty or blank in which case null is returned.
     * @return The trimmed and encoded id, or null if id is null.
     * If id is not null, the return value is guaranteed to be a valid Doxia id.
     * @see #isValidId(java.lang.String)
     * @since 1.1.1
     */
    public static String encodeId(final String text) {
        if (text == null) {
            return null;
        }

        final String textt = text.trim();
        int length = textt.length();

        if (length == 0) {
            return null;
        }

        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            char c = textt.charAt(i);

            if ((i == 0) && !(isAsciiLetter(c) || c == '_')) {
                buffer.append('a');
            }

            if (c == ' ') {
                buffer.append('_');
            } else if (isAsciiLetter(c) || isAsciiDigit(c) || (c == '-') || (c == '_') || (c == '.')) {
                buffer.append(c);
            } else {

                byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);

                for (byte aByte : bytes) {
                    buffer.append('.');
                    buffer.append(String.format("%02X", aByte));
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Determines if the specified text is a valid id according to the rules
     * laid out in {@link #encodeId(String)}.
     *
     * @param text The id to be tested.
     *      May be null or empty in which case false is returned.
     * @return <code>true</code> if the text is a valid id, otherwise <code>false</code>.
     * @see #encodeId(String)
     */
    public static boolean isValidId(final String text) {
        if (text == null || text.length() == 0) {
            return false;
        }

        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);

            if (isAsciiLetter(c) || c == '_') {
                continue;
            }

            if ((i == 0) || (!isAsciiDigit(c) && c != '-' && c != '.')) {
                return false;
            }
        }

        return true;
    }

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("", Locale.ENGLISH);
    private static final ParsePosition DATE_PARSE_POSITION = new ParsePosition(0);
    private static final String[] DATE_PATTERNS = new String[] {
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyyMMdd",
        "yyyy",
        "dd.MM.yyyy",
        "dd MMM yyyy",
        "dd MMM. yyyy",
        "MMMM yyyy",
        "MMM. dd, yyyy",
        "MMM. yyyy",
        "MMMM dd, yyyy",
        "MMM d, ''yy",
        "MMM. ''yy",
        "MMMM ''yy"
    };

    /**
     * <p>Parses a string representing a date by trying different date patterns.</p>
     *
     * <p>The following date patterns are tried (in the given order):</p>
     *
     * <pre>"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", "yyyy", "dd.MM.yyyy", "dd MMM yyyy",
     *  "dd MMM. yyyy", "MMMM yyyy", "MMM. dd, yyyy", "MMM. yyyy", "MMMM dd, yyyy",
     *  "MMM d, ''yy", "MMM. ''yy", "MMMM ''yy"</pre>
     *
     * <p>A parse is only sucessful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     *
     * <p>As a special case, the strings <code>"today"</code> and <code>"now"</code>
     * (ignoring case) return the current date.</p>
     *
     * @param str the date to parse, not null.
     * @return the parsed date, or the current date if the input String (ignoring case) was
     *      <code>"today"</code> or <code>"now"</code>.
     *
     * @throws ParseException if no pattern matches.
     * @throws NullPointerException if str is null.
     * @since 1.1.1.
     */
    public static Date parseDate(final String str) throws ParseException {
        if ("today".equalsIgnoreCase(str) || "now".equalsIgnoreCase(str)) {
            return new Date();
        }

        for (String datePattern : DATE_PATTERNS) {
            DATE_PARSER.applyPattern(datePattern);
            DATE_PARSE_POSITION.setIndex(0);
            final Date date = DATE_PARSER.parse(str, DATE_PARSE_POSITION);

            if (date != null && DATE_PARSE_POSITION.getIndex() == str.length()) {
                return date;
            }
        }

        throw new ParseException("Unable to parse date: " + str, -1);
    }

    //
    // private
    //

    private static boolean isAsciiLetter(final char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    private static boolean isAsciiDigit(final char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Determine width and height of an image. If successful, the returned SinkEventAttributes
     * contain width and height attribute keys whose values are the width and height of the image (as a String).
     *
     * @param logo a String containing either a URL or a path to an image file. Not null.
     * @return a set of SinkEventAttributes, or null if no ImageReader was found to read the image.
     *
     * @throws java.io.IOException if an error occurs during reading.
     * @throws NullPointerException if logo is null.
     *
     * @since 1.1.1
     */
    public static MutableAttributeSet getImageAttributes(final String logo) throws IOException {
        BufferedImage img;

        if (isExternalLink(logo)) {
            img = ImageIO.read(new URL(logo));
        } else {
            img = ImageIO.read(new File(logo));
        }

        if (img == null) {
            return null;
        }

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute(SinkEventAttributeSet.WIDTH, Integer.toString(img.getWidth()));
        atts.addAttribute(SinkEventAttributeSet.HEIGHT, Integer.toString(img.getHeight()));
        // add other attributes?

        return atts;
    }

    private DoxiaUtils() {
        // utility class
    }
}
