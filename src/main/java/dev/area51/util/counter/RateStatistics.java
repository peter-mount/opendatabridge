/*
 * Copyright 2015 peter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.area51.util.counter;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author peter
 */
public enum RateStatistics
{
    INSTANCE;

    private final Map<String, Stat> stats = new ConcurrentHashMap<>( );

    public int size( )
    {
        return stats.size( );
    }

    public boolean isEmpty( )
    {
        return stats.isEmpty( );
    }

    public void clear( )
    {
        stats.clear( );
    }

    public Consumer<Integer> getConsumer( String label )
    {
        return getConsumer( label,
                            BoundedDeque::getTotal );
    }

    public Consumer<Integer> getConsumer( String label,
                                          Function<BoundedDeque, Integer> aggregator )
    {
        String l = label;
        int i = label.indexOf( '[' );
        if ( i > 0 )
        {
            l = label.substring( 0,
                                 i );
        }
        return stats.computeIfAbsent( l,
                                      k -> new Stat( label,
                                                     aggregator ) );
    }

    public Stream<Stat> stream( )
    {
        return stats
            .values( )
            .stream( );
    }

    public final class Stat
        implements Consumer<Integer>,
                   Comparable<Stat>
    {

        private final String name;
        /**
         * The last hour, so up to 60 entries
         */
        private final BoundedDeque lastHour;
        /**
         * The last day, one for every 15 minutes, max 96 elements
         */
        private final BoundedDeque lastDay;

        Stat( String name,
              Function<BoundedDeque, Integer> aggregator )
        {
            this.name = name;
            lastHour = new BoundedDeque( 60,
                                         aggregator );
            lastDay = new BoundedDeque( 96,
                                        aggregator );
        }

        public String getName( )
        {
            return name;
        }

        public BoundedDeque getLastHour( )
        {
            return lastHour;
        }

        public BoundedDeque getLastDay( )
        {
            return lastDay;
        }

        @Override
        public void accept( Integer t )
        {
            if ( t != null )
            {
                // Update within the lock
                update( t );
            }
        }

        private synchronized void update( int t )
        {
            lastHour.accept( t );

            if ( Duration
                .between( lastDay.getLastTime( ),
                          lastHour.getLastTime( ) )
                .toMinutes( ) >= 15 )
            {
                lastHour.reset( lastDay );
            }
        }

        @Override
        public int compareTo( Stat o )
        {
            return String.CASE_INSENSITIVE_ORDER.compare( name,
                                                          o.getName( ) );
        }
    }
}
