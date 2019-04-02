package dev.area51.broker;

import dev.area51.runtime.Service;
import dev.area51.xsd.amqrabbitbridge.v1.MessageBroker;

import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public enum BrokerRepository
{
    INSTANCE;

    private final Map<Class, BrokerFactory> factories;

    BrokerRepository( )
    {
        factories = new ConcurrentHashMap<>( );

        ServiceLoader<BrokerFactory> loader = ServiceLoader.load( BrokerFactory.class );
        for ( BrokerFactory factory : loader )
        {
            BrokerType type = factory
                .getClass( )
                .getAnnotation( BrokerType.class );
            factories.put( type.value( ),
                           factory );
        }
    }

    public BrokerFactory lookup( MessageBroker definition )
    {
        return factories.get( definition.getClass( ) );
    }

    public Service createService( MessageBroker definition )
    {
        BrokerFactory factory = Objects.requireNonNull( lookup( definition ),
                                                        "No definition for " + definition.getClass( ) );
        return factory.create( definition );
    }
}
