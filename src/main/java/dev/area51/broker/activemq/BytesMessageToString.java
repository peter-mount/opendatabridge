/*
 * Copyright 2015 peter.
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
package dev.area51.broker.activemq;

import javax.jms.BytesMessage;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mapping function which accepts a JMS BytesMessage and decompresses the gzipped content into a String
 *
 * @author peter
 */
public class BytesMessageToString
    implements Function<BytesMessage, String>
{

    @Override
    public String apply( BytesMessage t )
    {
        if ( t != null )
        {
            try
            {
                final byte[] d = new byte[ ( int ) t.getBodyLength( ) ];
                t.readBytes( d );
                return new String( d );
            }
            catch ( Exception ex )
            {
                Logger
                    .getLogger( getClass( ).getName( ) )
                    .log( Level.SEVERE,
                          null,
                          ex );
            }
        }

        return null;
    }

}
