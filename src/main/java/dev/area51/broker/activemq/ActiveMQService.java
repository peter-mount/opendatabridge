package dev.area51.broker.activemq;

import dev.area51.broker.activemq.sources.SourceRepository;
import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.runtime.Context;
import dev.area51.runtime.Service;
import dev.area51.util.Functions;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQ;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQBase;

import javax.xml.bind.JAXBElement;
import java.util.Objects;
import java.util.logging.Logger;

public class ActiveMQService
    implements Service
{
    private static final Logger LOG = Logger.getLogger( ActiveMQService.class.getName( ) );

    private final ActiveMQ config;

    private RemoteActiveMQConnection activeMQ;

    public ActiveMQService( final ActiveMQ config )
    {
        this.config = config;
    }

    @Override
    public String getName( )
    {
        return "ActiveMQService " + config.getHostname( );
    }

    @Override
    public void start( Context ctx )
        throws
        Exception
    {
        activeMQ = new RemoteActiveMQConnection( Objects.requireNonNull( config.getHostname( ),
                                                                         "hostname not provided" ),
                                                 config.getPort( ) == null ? 61616 : config.getPort( ),
                                                 config.getClientId( ) == null ? config
                                                     .getCredentials( )
                                                     .getUsername( ) : config.getClientId( ),
                                                 config
                                                     .getCredentials( )
                                                     .getUsername( ),
                                                 config
                                                     .getCredentials( )
                                                     .getPassword( ) );

        config
            .getAny( )
            .stream( )
            .map( Functions.castTo( JAXBElement.class ) )
            .filter( Objects::nonNull )
            .map( JAXBElement::getValue )
            .map( Functions.castTo( ActiveMQBase.class ) )
            .filter( Objects::nonNull )
            .map( ConsumerBuilder::new )
            .map( b -> b
                .setContext( ctx )
                .setActiveMQ( activeMQ ) )
            .map( ConsumerBuilder::generateConsumer )
            .map( SourceRepository.INSTANCE::createTask )
            .forEach( task -> task.run( ) );

        activeMQ.start( );
    }

    @Override
    public void stop( final Context ctx )
    {
        activeMQ.stop( );
    }

}
