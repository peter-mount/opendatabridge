package dev.area51.broker.bridge;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.broker.consumer.ConsumerFactory;
import dev.area51.broker.consumer.ConsumerType;
import dev.area51.util.Consumers;
import dev.area51.xsd.amqrabbitbridge.v1.ExitAfterNoMessages;
import org.kohsuke.MetaInfServices;

import javax.jms.Message;
import java.util.function.Consumer;

@ConsumerType ( ExitAfterNoMessages.class )
@MetaInfServices ( ConsumerFactory.class )
public class Issue16Factory
    implements ConsumerFactory<ExitAfterNoMessages>
{
    @Override
    public Consumer<Message> create( final ConsumerBuilder builder,
                                     final ExitAfterNoMessages definition )
    {
        if ( definition.getIdleTTL( ) == null || definition.getIdleTTL( ) <= 0 )
        {
            return Consumers.sink( );
        }

        return new Issue16<>( definition.getIdleTTL( ) );
    }
}
