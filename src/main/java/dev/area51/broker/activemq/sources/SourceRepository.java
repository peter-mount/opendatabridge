package dev.area51.broker.activemq.sources;

import dev.area51.broker.consumer.ConsumerBuilder;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQBase;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public enum SourceRepository
{
    INSTANCE;

    private final Map<Class, SourceFactory> factories;

    SourceRepository( )
    {
        factories = new ConcurrentHashMap<>( );

        ServiceLoader<SourceFactory> loader = ServiceLoader.load( SourceFactory.class );
        for ( SourceFactory factory : loader )
        {
            SourceType type = factory
                .getClass( )
                .getAnnotation( SourceType.class );
            factories.put( type.value( ),
                           factory );
        }
    }

    public SourceFactory lookup( ActiveMQBase definition )
    {
        return factories.get( definition.getClass( ) );
    }

    public Runnable createTask( ConsumerBuilder builder )
    {
        SourceFactory factory = Objects.requireNonNull( lookup( builder.getDefinition( ) ),
                                                        "No definition for " + builder
                                                            .getDefinition( )
                                                            .getClass( ) );
        return factory.create( builder );
    }
}
