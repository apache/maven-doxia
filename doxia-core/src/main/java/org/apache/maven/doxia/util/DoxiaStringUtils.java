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

/**
 * Utility methods for string operations.
 * This class provides methods that were previously supplied by Apache Commons Lang3.
 *
 * @since 2.1.0
 */
public class DoxiaStringUtils {

    private DoxiaStringUtils() {
        // utility class
    }

    /**
     * Repeats a string a certain number of times.
     *
     * @param str the string to repeat
     * @param repeat number of times to repeat
     * @return the repeated string
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        // Check for potential overflow
        int len = str.length();
        if (len > 0 && repeat > Integer.MAX_VALUE / len) {
            throw new IllegalArgumentException("Resulting string would be too long");
        }
        StringBuilder sb = new StringBuilder(len * repeat);
        for (int i = 0; i < repeat; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Replaces all occurrences of a string within another string.
     *
     * @param text the text to search and replace in
     * @param searchString the string to search for
     * @param replacement the string to replace with
     * @return the text with any replacements processed
     */
    public static String replace(String text, String searchString, String replacement) {
        if (text == null || text.isEmpty() || searchString == null || searchString.isEmpty()) {
            return text;
        }
        if (replacement == null) {
            replacement = "";
        }
        return text.replace(searchString, replacement);
    }

    /**
     * Checks if a string is not empty.
     *
     * @param str the string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Checks if a string is blank (null, empty, or whitespace only).
     *
     * @param str the string to check
     * @return true if the string is null, empty, or whitespace only
     */
    public static boolean isBlank(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all strings are blank.
     *
     * @param strs the strings to check
     * @return true if all strings are blank
     */
    public static boolean isAllBlank(String... strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        for (String str : strs) {
            if (!isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits a string by specified delimiters.
     *
     * @param str the string to split
     * @param separatorChars the characters to use as delimiters
     * @return an array of parsed strings, empty array if null string input
     */
    public static String[] split(String str, String separatorChars) {
        if (str == null) {
            return new String[0];
        }
        if (str.isEmpty()) {
            return new String[0];
        }
        if (separatorChars == null || separatorChars.isEmpty()) {
            separatorChars = " \t\n\r\f";
        }

        java.util.List<String> result = new java.util.ArrayList<>();
        int start = 0;
        int len = str.length();

        while (start < len) {
            // Skip separators
            while (start < len && separatorChars.indexOf(str.charAt(start)) >= 0) {
                start++;
            }
            if (start >= len) {
                break;
            }
            // Find end of token
            int end = start;
            while (end < len && separatorChars.indexOf(str.charAt(end)) < 0) {
                end++;
            }
            result.add(str.substring(start, end));
            start = end;
        }

        return result.toArray(new String[0]);
    }

    /**
     * Removes a substring only if it is at the end of a source string.
     *
     * @param str the source string
     * @param remove the string to remove
     * @return the substring with the string removed if found
     */
    public static String removeEnd(String str, String remove) {
        if (str == null || remove == null || str.isEmpty() || remove.isEmpty()) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * Strips any of a set of characters from the start of a string.
     *
     * @param str the string to remove characters from
     * @param stripChars the characters to remove
     * @return the stripped string
     */
    public static String stripStart(String str, String stripChars) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int start = 0;
        int len = str.length();
        if (stripChars == null) {
            while (start < len && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else {
            while (start < len && stripChars.indexOf(str.charAt(start)) >= 0) {
                start++;
            }
        }
        return start > 0 ? str.substring(start) : str;
    }

    /**
     * Counts how many times the substring appears in the larger string.
     *
     * @param str the string to check
     * @param sub the substring to count
     * @return the number of occurrences, 0 if either string is null
     */
    public static int countMatches(String str, String sub) {
        if (str == null || str.isEmpty() || sub == null || sub.isEmpty()) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
