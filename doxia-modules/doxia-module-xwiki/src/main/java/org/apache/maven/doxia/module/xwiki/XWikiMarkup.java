package org.apache.maven.doxia.module.xwiki;

import org.apache.maven.doxia.markup.TextMarkup;

public interface XWikiMarkup
    extends TextMarkup
{
    /**
     * Syntax for the list start: "*"
     */
    String LIST_START_MARKUP = String.valueOf( STAR );

    /**
     * XWiki numbering decimal markup char: '1'
     */
    char NUMBERING = '1';

    /**
     * XWiki Numbering lower alpha markup char: 'a'
     */
    char NUMBERING_LOWER_ALPHA_CHAR = 'a';

    /**
     * XWiki numbering lower roman markup char: 'i'
     */
    char NUMBERING_LOWER_ROMAN_CHAR = 'i';

    /**
     * XWiki numbering upper alpha markup char: 'A'
     */
    char NUMBERING_UPPER_ALPHA_CHAR = 'A';

    /**
     * XWiki numbering upper roman markup char: 'I'
     */
    char NUMBERING_UPPER_ROMAN_CHAR = 'I';

}
