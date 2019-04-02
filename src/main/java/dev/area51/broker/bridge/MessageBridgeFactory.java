package dev.area51.broker.bridge;

import com.rabbitmq.client.AMQP;
import dev.area51.broker.activemq.BytesMessageToString;
import dev.area51.broker.activemq.GUnZipBytesMessage;
import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.broker.consumer.ConsumerFactory;
import dev.area51.broker.consumer.ConsumerType;
import dev.area51.broker.rabbitmq.RabbitConsumer;
import dev.area51.broker.rabbitmq.RabbitMQService;
import org.kohsuke.MetaInfServices;

import javax.jms.BytesMessage;
import javax.jms.Message;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@ConsumerType ( dev.area51.xsd.amqrabbitbridge.v1.MessageBridge.class )
@MetaInfServices ( ConsumerFactory.class )
public class MessageBridgeFactory
    implements ConsumerFactory<dev.area51.xsd.amqrabbitbridge.v1.MessageBridge>
{
    private static final String DEFAULT_EXCHANGE = "amq.topic";

    @Override
    public Consumer<Message> create( final ConsumerBuilder builder,
                                     final dev.area51.xsd.amqrabbitbridge.v1.MessageBridge definition )
    {
        String routingKey = Objects.requireNonNull( definition.getRoutingKey( ) );

        // How to handle gzipped binary data
        Function<BytesMessage, String> converter;
        if ( Boolean.TRUE.equals( definition.isGzip( ) ) )
        {
            converter = new GUnZipBytesMessage( );
        }
        else
        {
            converter = new BytesMessageToString( );
        }

        // Consumer to submit message to rabbitmq
        BiConsumer<AMQP.BasicProperties, byte[]> consumer = new RabbitConsumer( builder.getContext( ).<RabbitMQService>get( RabbitMQService.KEY ).getRabbitmq( ),
                                                                                Objects.toString( definition.getExchange( ),
                                                                                                  DEFAULT_EXCHANGE ),
                                                                                routingKey );
        return new MessageBridge( consumer,
                                  converter,
                                  routingKey );
    }
}
