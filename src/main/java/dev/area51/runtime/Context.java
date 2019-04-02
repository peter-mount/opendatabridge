package dev.area51.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface Context
    extends Iterable<Service>
{

    <V> void put( String k,
                  V v );

    <V> V get( String k );

    void remove( String k );

    Context addService( Service s );

    static Context newInstance( )
    {
        final List<Service> services = new CopyOnWriteArrayList<>( );
        final Map<String, Object> properties = new ConcurrentHashMap<>( );

        return new Context( )
        {
            @Override
            public <V> void put( final String k,
                                 final V v )
            {
                properties.put( k,
                                v );
            }

            @Override
            public <V> V get( final String k )
            {
                return ( V ) properties.get( k );
            }

            @Override
            public void remove( final String k )
            {
                properties.remove( k );
            }

            @Override
            public Context addService( final Service s )
            {
                services.add( s );
                return this;
            }

            @Override
            public Iterator<Service> iterator( )
            {
                return services.iterator( );
            }
        };
    }
}
