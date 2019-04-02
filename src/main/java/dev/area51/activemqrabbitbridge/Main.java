package dev.area51.activemqrabbitbridge;

import dev.area51.broker.BrokerRepository;
import dev.area51.runtime.Context;
import dev.area51.runtime.ServiceRuntime;
import dev.area51.util.Functions;
import dev.area51.xsd.amqrabbitbridge.v1.MessageBroker;

import javax.xml.bind.JAXBElement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application entry point
 */
public class Main
{
    private static final Logger LOG = Logger.getLogger( Main.class.getName( ) );

    public static void main( final String... args )
        throws
        Exception
    {
        LOG.log( Level.INFO,
                 "Initialising ActiveMQ-RabbitMQUtils Bridge v2.0" );

        final Context ctx = Context.newInstance( );

        ConfigLoader
            .parse( args )
            .getAny( )
            .stream( )
            .map( Functions.castTo( JAXBElement.class ) )
            .filter( Objects::nonNull )
            .map( JAXBElement::getValue )
            .map( Functions.castTo( MessageBroker.class ) )
            .filter( Objects::nonNull )
            .filter( a -> !Boolean.TRUE.equals( a.isDisabled( ) ) )
            .map( BrokerRepository.INSTANCE::createService )
            .forEach( ctx::addService );

        ServiceRuntime.run( ctx );
    }
}
