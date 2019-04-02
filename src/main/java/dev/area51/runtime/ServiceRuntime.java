package dev.area51.runtime;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class ServiceRuntime
{
    private static final Logger LOG = Logger.getLogger( ServiceRuntime.class.getName( ) );

    public static void run( final Context context )
        throws
        Exception
    {
        final Semaphore SEMAPHORE = new Semaphore( 0 );
        final LinkedList<Service> running = new LinkedList<>( );

        try
        {
            for ( Service s : context )
            {
                LOG.info( ( ) -> "Starting " + s.getName( ) );
                s.start( context );
                running.addFirst( s );
            }

            Runtime
                .getRuntime( )
                .addShutdownHook( new Thread( ( ) -> stop( context,
                                                           running ) ) );

            LOG.info( "Bridge running" );
            SEMAPHORE.acquire( 1 );
        }
        finally
        {
            stop( context,
                  running );
        }
    }

    private static void stop( Context context,
                              List<Service> services )
    {
        LOG.info( "Shutting down" );
        for ( Service s : services )
        {
            try
            {
                LOG.info( ( ) -> "Stopping" + s.getName( ) );
                s.stop( context );
            }
            catch ( Exception e )
            {
                // ignore
            }
        }
    }
}
