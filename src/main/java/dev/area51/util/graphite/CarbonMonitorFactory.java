package dev.area51.util.graphite;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.broker.consumer.ConsumerFactory;
import dev.area51.broker.consumer.ConsumerType;
import dev.area51.broker.rabbitmq.RabbitMQService;
import dev.area51.xsd.amqrabbitbridge.v1.Graphite;
import org.kohsuke.MetaInfServices;

import javax.jms.Message;
import java.util.function.Consumer;

@ConsumerType ( Graphite.class )
@MetaInfServices ( ConsumerFactory.class )
public class CarbonMonitorFactory
    implements ConsumerFactory<Graphite>
{
    @Override
    public Consumer<Message> create( final ConsumerBuilder builder,
                                     final Graphite definition )
    {
        return new CarbonMonitor<>( builder.getContext( ).<RabbitMQService>get( RabbitMQService.KEY ).getRabbitmq( ),
                                    definition );
    }
}
