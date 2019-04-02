package dev.area51.util.counter;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.broker.consumer.ConsumerFactory;
import dev.area51.broker.consumer.ConsumerType;
import dev.area51.xsd.amqrabbitbridge.v1.RateLogger;
import org.kohsuke.MetaInfServices;

import javax.jms.Message;
import java.util.function.Consumer;

@ConsumerType ( RateLogger.class )
@MetaInfServices ( ConsumerFactory.class )
public class RateMonitorFactory
    implements ConsumerFactory<RateLogger>
{
    @Override
    public Consumer<Message> create( final ConsumerBuilder builder,
                                     final RateLogger definition )
    {
        return new RateMonitor<>( definition );
    }
}
