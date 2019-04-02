package dev.area51.broker.activemq.sources;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQQueue;
import org.kohsuke.MetaInfServices;

import java.util.logging.Logger;

@SourceType ( ActiveMQQueue.class )
@MetaInfServices ( SourceFactory.class )
public class QueueSource
    implements SourceFactory
{

    private static final Logger LOG = Logger.getLogger( QueueSource.class.getName( ) );

    @Override
    public Runnable create( final ConsumerBuilder builder )
    {
        return ( ) -> {
            ActiveMQQueue queue = builder.getDefinition( );

            LOG.info( ( ) -> "Subscribing to queue " + queue.getQueue( ) );

            builder
                .getActiveMQ( )
                .registerQueueConsumer( queue.getQueue( ),
                                        builder.getConsumer( ) );
        };
    }
}
