package dev.area51.broker.rabbitmq;

import dev.area51.runtime.Context;
import dev.area51.runtime.Service;
import dev.area51.xsd.amqrabbitbridge.v1.RabbitMQ;

import java.util.logging.Logger;

public class RabbitMQService
    implements Service
{
    private static final Logger LOG = Logger.getLogger( RabbitMQService.class.getName( ) );

    private final RabbitMQ config;

    private RabbitConnection rabbitmq;

    public static final String KEY = "RabbitMQUtils";

    public RabbitMQService( final RabbitMQ config )
    {
        this.config = config;
    }

    @Override
    public String getName( )
    {
        return "RabbitMQService " + config.getHostname( );
    }

    @Override
    public void start( Context ctx )
        throws
        Exception
    {
        rabbitmq = new RabbitConnection( config
                                             .getCredentials( )
                                             .getUsername( ),
                                         config
                                             .getCredentials( )
                                             .getPassword( ),
                                         config.getHostname( ),
                                         config.getPort( ) == null ? 0 : config.getPort( ),
                                         config.getVhost( ) );

        LOG.info( ( ) -> "Connecting to " + config.getHostname( ) );

        rabbitmq.getConnection( );

        LOG.info( ( ) -> "Connected to " + config.getHostname( ) );

        // Allow this instance to be picked up
        ctx.put( KEY,
                 this );
    }

    @Override
    public void stop( final Context ctx )
    {
        ctx.remove( KEY );
        rabbitmq.close( );
    }

    public RabbitMQ getConfig( )
    {
        return config;
    }

    public RabbitConnection getRabbitmq( )
    {
        return rabbitmq;
    }
}
