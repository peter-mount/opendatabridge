/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.area51.util.counter;

import dev.area51.util.DaemonThreadFactory;
import dev.area51.xsd.amqrabbitbridge.v1.RateLogger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class RateMonitor<T>
    implements Consumer<T>
{
    private static final Logger LOG = Logger.getLogger( RateMonitor.class.getName( ) );

    private final AtomicInteger counter = new AtomicInteger( );
    private final ScheduledFuture<?> scheduledFuture;

    private final String label;
    private int lastCount;
    private long total;

    public RateMonitor( RateLogger rateLogger )
    {
        label = rateLogger.getLabel( );

        int period = rateLogger.getPeriod( ) != null && rateLogger.getPeriod( ) > 0 ? rateLogger.getPeriod( ) : 60;

        int initDelay = rateLogger.getInitialDelay( ) != null && rateLogger.getInitialDelay( ) > 0 ? rateLogger.getInitialDelay( ) : period;

        int min = rateLogger.getMin( ) != null ? rateLogger.getMin( ) : 1;

        Runnable task = ( ) -> {
            // Read & reset the count. Don't put it in the logger as we use a supplier & won't be invoked if the logger
            // isn't showing the required level
            lastCount = counter.getAndSet( 0 );
            total += lastCount;

            // Reduce logspam by only logging when we've done something
            if ( lastCount >= min )
            {
                LOG.log( Level.INFO,
                         ( ) -> label + ' ' + lastCount );
            }

            RateStatistics.INSTANCE
                .getConsumer( label )
                .accept( lastCount );
        };

        scheduledFuture = DaemonThreadFactory.INSTANCE.scheduleAtFixedRate( task,
                                                                            initDelay,
                                                                            period,
                                                                            TimeUnit.SECONDS );
        task.run( );
    }

    @Override
    public String toString( )
    {
        return label + ' ' + lastCount + "/" + ( total + counter.get( ) );
    }

    @Override
    public void accept( T t )
    {
        counter.incrementAndGet( );
    }

    /**
     * The current snapshot of the counter's value.
     * <p>
     *
     * @return
     */
    public final int get( )
    {
        return counter.get( );
    }

    public boolean cancel( boolean mayInterruptIfRunning )
    {
        return scheduledFuture.cancel( mayInterruptIfRunning );
    }

    public boolean isCancelled( )
    {
        return scheduledFuture.isCancelled( );
    }

    public boolean isDone( )
    {
        return scheduledFuture.isDone( );
    }

    public final void reset( )
    {
        counter.set( 0 );
        lastCount = 0;
        total = 0L;
    }

}
