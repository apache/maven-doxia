package org.apache.maven.doxia.module.xwiki.parser;

import org.apache.maven.doxia.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was written with performance in mind and thus is lacking clarity. Since this code is
 * in the rendering path it's important it be very fast.
 * <p/>
 * NOTE: Do NOT use this parser yet as it's not ready and not working.
 */
public class MacroParser
{
    private int STATE_NAME = 0;

    private int STATE_PARAM_NAME = 1;

    private int STATE_PARAM_VALUE = 2;

    private int STATE_CONTENT = 3;

    private int STATE_END = 4;

    private boolean isInCompatibilityMode = false;

    private List multilineMacros;

    public MacroParser()
    {
        this.multilineMacros = new ArrayList();
        this.multilineMacros.add( "code" );
        this.multilineMacros.add( "style" );
        this.multilineMacros.add( "pre" );
    }

    public void setCompatibilityMode( boolean isInCompatibilityMode )
    {
        this.isInCompatibilityMode = isInCompatibilityMode;
    }

    public int parse( String input, int position, List blocks )
        throws ParseException
    {
        String macroName = null;
        Map parameters = new HashMap();
        String parameterName;
        String parameterValue;

        int state = STATE_NAME;
        StringBuffer text = new StringBuffer();

        int i = position;
        while ( i < input.length() && state != STATE_END )
        {
            char c = input.charAt( i );

            switch ( c )
            {
                // {macroname:...}
                case ':':
                    if ( state == STATE_NAME )
                    {
                        macroName = text.toString();
                        state = STATE_PARAM_NAME;
                        text = new StringBuffer();
                    }
                    else if ( state != STATE_PARAM_VALUE )
                    {
                        throw new ParseException( "Invalid position for character ':' in Macro" );
                    }
                    else
                    {
                        // We only allow ':' characters in parameter values (and of course after
                        // the macro name).
                        text.append( c );
                    }
                    break;
                    // {macroname:... /}. Contraction of {macroname:...}{/macroname}
                case '/':
                    if ( state != STATE_PARAM_VALUE )
                    {
                        throw new ParseException( "Invalid position for character '/' in Macro" );
                    }
                    else
                    {
                        if ( charAt( input, i ) == '}' )
                        {
                            i++;
                            state = STATE_END;
                        }
                        else
                        {
                            // We allow the '/' character in parameter values.
                            text.append( c );
                        }
                    }
                    break;
                    // {macro:...} or {macro:...}...{macro} or {macro:...}...{/macro}
                case '}':
                    // Here are the use cases to take into account:
                    // * {newmacro..}...{/newmacro}
                    // * {oldsinglelinemacro:...}
                    // * {oldmultilinemacro:...}...{oldmultilinemacro}
                    // * {oldmultilinemacro:...}...{/oldmultilinemacro}
                    if ( state == STATE_PARAM_VALUE )
                    {
                        // {macro:...}
                        if ( isInCompatibilityMode )
                        {
                            // TODO
                            throw new ParseException( "Compatibility mode for macros not implemented yet" );
                        }
                        else
                        {
                            state = STATE_CONTENT;
                        }
                    }
                    else if ( state == STATE_CONTENT )
                    {
                        // ...{macro}
                        if ( isInCompatibilityMode )
                        {
                            state = STATE_END;
                        }
                        else
                        {
                            // We allow the '}' character in macro content.
                            text.append( c );
                        }
                    }
                    break;
                    // {macroname:...}...{/macroname}
                case '{':
                    // When this parsing method is called it's assumed the first { character
                    // representing the macro has already been consumed so if there's another such
                    // character it means it's either part of the macro content (signifying a
                    // nested macro), part of a macro parameter or simply the closing part of the
                    // macro.
                    if ( charAt( input, i ) == '/' )
                    {
                        // TODO: We should probably verify here that the name of the closed macro
                        // corresponds to the current macro being parsed. For now we just assume it
                        // is.
                        state = STATE_END;
                    }
                    else
                    {
                        if ( state == STATE_PARAM_VALUE )
                        {
                            // We allow the '{' character in macro parameter values
                            text.append( c );
                        }
                        else if ( state == STATE_CONTENT )
                        {
                            if ( isInCompatibilityMode )
                            {
                                // TODO
                                throw new ParseException( "Compatibility mode for macros not implemented yet" );
                            }
                            else
                            {
                                // TODO: We have a '{' character inside the macro content. Let's consider
                                // it's a nested macro and let's parse it.
                                // For now just ignore it
                                text.append( c );
                            }
                        }
                    }
                    break;
                case '|':
                    // TODO: In the future allow quoted param values so that this character can be
                    // supported too in param values.
                    if ( state == STATE_PARAM_VALUE )
                    {
                        parameterValue = text.toString();
                        text = new StringBuffer();
                        state = STATE_PARAM_NAME;
                    }
                    else if ( state == STATE_CONTENT )
                    {
                        text.append( c );
                    }
                    else
                    {
                        throw new ParseException( "Invalid position for character '|' in Macro" );
                    }
                    break;
                case '=':
                    if ( state == STATE_PARAM_NAME )
                    {
                        parameterValue = text.toString();
                        text = new StringBuffer();
                        state = STATE_PARAM_VALUE;
                    }
                    else if ( state == STATE_PARAM_VALUE || state == STATE_CONTENT )
                    {
                        text.append( c );
                    }
                    else
                    {
                        throw new ParseException( "Invalid position for character '=' in Macro" );
                    }
                    break;
                default:
                    text.append( c );
            }

            i++;
        }

        blocks.add( new MacroBlock( macroName, new HashMap(), new ArrayList() ) );

        return i;
    }

    private static char charAt( String input, int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }
}
