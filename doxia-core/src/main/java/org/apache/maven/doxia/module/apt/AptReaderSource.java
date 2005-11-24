package org.apache.maven.doxia.module.apt;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class AptReaderSource
    implements AptSource
{
    private LineNumberReader reader;

    private int lineNumber;

    public AptReaderSource( Reader in )
    {
        reader = new LineNumberReader( in );

        lineNumber = -1;
    }

    public String getNextLine()
        throws AptParseException
    {
        if ( reader == null )
        {
            return null;
        }

        String line;

        try
        {
            line = reader.readLine();
            if ( line == null )
            {
                reader.close();
                reader = null;
            }
            else
            {
                lineNumber = reader.getLineNumber();
            }
        }
        catch ( IOException e )
        {
            throw new AptParseException( e );
        }

        return line;
    }

    public String getName()
    {
        return "";
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public void close()
    {
        if ( reader != null )
        {
            try
            {
                reader.close();
            }
            catch ( IOException ignored )
            {
            }
        }
        reader = null;
    }
}
