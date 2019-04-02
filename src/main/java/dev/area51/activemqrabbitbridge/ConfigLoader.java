package dev.area51.activemqrabbitbridge;

import dev.area51.xsd.amqrabbitbridge.v1.Config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class ConfigLoader
{
    public static Config parse( final String[] args )
        throws
        IOException,
        JAXBException
    {
        if ( args == null || args.length != 1 )
        {
            throw new IOException( "No config file provided" );
        }

        File file = new File( args[ 0 ] );

        JAXBContext ctx = JAXBContext.newInstance( Config.class );
        Unmarshaller unmarshaller = ctx.createUnmarshaller( );

        return ( Config ) unmarshaller.unmarshal( file );
    }
}
