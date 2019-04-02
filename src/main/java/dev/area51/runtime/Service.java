package dev.area51.runtime;

public interface Service
{
    /**
     * Service entry point
     *
     * @throws Exception
     */
    void start( Context context )
        throws
        Exception;

    /**
     * Stop a service
     */
    default void stop( Context context )
    {

    }

    default String getName( )
    {
        return getClass( ).getSimpleName( );
    }
}
