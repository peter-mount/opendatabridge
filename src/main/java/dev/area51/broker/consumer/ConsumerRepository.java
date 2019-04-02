package dev.area51.broker.consumer;

import dev.area51.xsd.amqrabbitbridge.v1.ConsumerDefinition;

import javax.jms.Message;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum ConsumerRepository
{
    INSTANCE;

    private final Map<Class, ConsumerFactory> factories;

    ConsumerRepository( )
    {
        factories = new ConcurrentHashMap<>( );

        ServiceLoader<ConsumerFactory> loader = ServiceLoader.load( ConsumerFactory.class );
        for ( ConsumerFactory factory : loader )
        {
            ConsumerType type = factory
                .getClass( )
                .getAnnotation( ConsumerType.class );
            factories.put( type.value( ),
                           factory );
        }
    }

    public ConsumerFactory lookup( ConsumerDefinition definition )
    {
        return factories.get( definition.getClass( ) );
    }

    public Consumer<Message> createConsumer( ConsumerBuilder builder,
                                             ConsumerDefinition definition )
    {
        ConsumerFactory factory = Objects.requireNonNull( lookup( definition ),
                                                          "No definition for " + definition.getClass( ) );
        return factory.create( builder,
                               definition );
    }
}
