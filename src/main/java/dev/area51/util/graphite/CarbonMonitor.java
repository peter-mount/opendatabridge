package dev.area51.util.graphite;

import dev.area51.broker.rabbitmq.RabbitConnection;
import dev.area51.broker.rabbitmq.RabbitMQUtils;
import dev.area51.util.DaemonThreadFactory;
import dev.area51.xsd.amqrabbitbridge.v1.Graphite;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A Consumer which counts the number of times it's called.
 * <p>
 * Behind the scenes this also logs the rate once per minute and then resets.
 * <p>
 * Normal use you need to create a single instance of RateMonitor and then from within a stream by using
 * {@code peek( rateMonitor ).}
 * <p>
 * An alternate use when being used with another consumer is: {@code something.forEach( rateMonitor.andThen( consumer ) )} where
 * consumer is another consumer or lambda expression.
 * <p>
 * Do not place the RateMonitor.log() call within a stream. Doing that will cause a new instance to be created for every time
 * you use the stream.
 * <p>
 *
 * @param <T> <p>
 *
 * @author Peter T Mount
 */
public class CarbonMonitor<T>
    implements Consumer<T>
{

    private final AtomicInteger counter = new AtomicInteger( );

    public CarbonMonitor( final RabbitConnection rabbitmq,
                          final Graphite graphite )
    {
        int period = graphite.getPeriod( ) != null && graphite.getPeriod( ) > 0 ? graphite.getPeriod( ) : 10;

        int initDelay = graphite.getInitialDelay( ) != null && graphite.getInitialDelay( ) > 0 ? graphite.getInitialDelay( ) : period;

        final Consumer<String> consumer = RabbitMQUtils.stringConsumer( rabbitmq,
                                                                        Objects.toString( graphite.getExchange( ),
                                                                                          "graphite" ),
                                                                        Objects.requireNonNull( graphite.getMetric( ) ) );

        Runnable task;
        if ( Boolean.TRUE.equals( graphite.isMetricNameInBody( ) ) )
        {
            task = ( ) -> consumer.accept( String.format( "%s %d %d",
                                                          graphite.getMetric( ),
                                                          counter.getAndSet( 0 ),
                                                          System.currentTimeMillis( ) / 1000L ) );
        }
        else
        {
            task = ( ) -> consumer.accept( String.format( "%d %d",
                                                          counter.getAndSet( 0 ),
                                                          System.currentTimeMillis( ) / 1000L ) );
        }

        DaemonThreadFactory.INSTANCE.scheduleAtFixedRate( task,
                                                          initDelay,
                                                          period,
                                                          TimeUnit.SECONDS );
    }

    @Override
    public void accept( T t )
    {
        counter.incrementAndGet( );
    }

}
