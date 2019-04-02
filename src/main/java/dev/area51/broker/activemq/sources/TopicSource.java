package dev.area51.broker.activemq.sources;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQTopic;
import org.kohsuke.MetaInfServices;

import java.util.logging.Logger;

@SourceType ( ActiveMQTopic.class )
@MetaInfServices ( SourceFactory.class )
public class TopicSource
    implements SourceFactory
{

    private static final Logger LOG = Logger.getLogger( TopicSource.class.getName( ) );

    @Override
    public Runnable create( final ConsumerBuilder builder )
    {
        return ( ) -> {
            ActiveMQTopic topic = builder.getDefinition( );

            LOG.info( ( ) -> "Subscribing to topic " + topic.getTopic( ) );

            builder
                .getActiveMQ( )
                .registerTopicConsumer( topic.getTopic( ),
                                        builder.getConsumer( ) );
        };
    }
}
