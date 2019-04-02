/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.area51.util;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @author Peter T Mount
 */
public class Functions
{

    /**
     * Mapping function to cast between two classes
     * <p>
     *
     * @param <I> Input type
     * @param <R> Output type
     *            <p>
     *
     * @return mapping function
     */
    @SuppressWarnings ( "unchecked" )
    public static <I, R> Function<I, R> castingIdentity( )
    {
        return i -> ( R ) i;
    }

    /**
     * BinaryOperator that will always throw an {@link IllegalStateException}
     * <p>
     *
     * @param <T> Type
     *            <p>
     *
     * @return BinaryOperator
     */
    public static <T> BinaryOperator<T> throwingBinaryOperator( )
    {
        return ( u, v ) -> {
            throw new IllegalStateException( String.format( "Duplicate key %s",
                                                            u ) );
        };
    }

    /**
     * BinaryOperator that will preserve an existing entry when used as a combiner or the first entry in a reduction.
     * <p>
     *
     * @param <T> Type
     *            <p>
     *
     * @return BinaryOperator
     */
    public static <T> BinaryOperator<T> writeOnceBinaryOperator( )
    {
        return ( a, b ) -> a;
    }

    /**
     * BinaryOperator that will overwrite an existing entry when used as a combiner or the last entry in a reduction.
     * <p>
     *
     * @param <T> Type
     *            <p>
     *
     * @return BinaryOperator
     */
    public static <T> BinaryOperator<T> overwritingBinaryOperator( )
    {
        return ( a, b ) -> b;
    }

    /**
     * Cast to a specific class
     * <p>
     *
     * @param <T>   Type of class
     * @param clazz Class of type T to cast to
     *              <p>
     *
     * @return casted object or null if null or not an instance of clazz
     */
    public static <T> Function<? super Object, T> castTo( Class<T> clazz )
    {
        return o -> o == null || !clazz.isAssignableFrom( o.getClass( ) ) ? null : clazz.cast( o );
    }

    /**
     * Get a class by name
     * <p>
     *
     * @param <T> Type of class
     * @param n   class name
     *            <p>
     *
     * @return Class or null if not found
     */
    public static <T> Class<T> forName( String n )
    {
        if ( n == null || n.isEmpty( ) )
        {
            return null;
        }
        else
        {
            try
            {
                return ( Class<T> ) Class.forName( n );
            }
            catch ( ClassNotFoundException ex )
            {
                return null;
            }
        }
    }

    /**
     * Create a new instance of a class by its name
     * <p>
     * This is equivalent of newInstance(forName(n))
     * <p>
     *
     * @param <T> Type of class
     * @param n   Class name
     *            <p>
     *
     * @return new instance or null if it could not be instantiated
     */
    public static <T> T newInstance( String n )
    {
        return newInstance( forName( n ) );
    }

    /**
     * Create a new instance of a class
     * <p>
     *
     * @param <T>   Type of class
     * @param clazz Class
     *              <p>
     *
     * @return new instance or null if it could not be instantiated
     */
    public static <T> T newInstance( Class<T> clazz )
    {
        if ( clazz == null )
        {
            return null;
        }
        else
        {
            try
            {
                return clazz.newInstance( );
            }
            catch ( InstantiationException | IllegalAccessException ex )
            {
                return null;
            }
        }
    }

    public static <T, R> Function<T, R> guard( Function<T, R> f )
    {
        return r -> {
            try
            {
                return f.apply( r );
            }
            catch ( Throwable t )
            {
                return null;
            }
        };
    }

    /**
     * Apply one function. if that returns null then use the result of a second
     *
     * @param <T>
     * @param <R>
     * @param f
     * @param s
     *
     * @return
     */
    public static <T, R> Function<T, R> applyWithDefault( Function<T, R> f,
                                                          Function<T, R> s )
    {
        return t -> {
            R r = f.apply( t );
            return r == null ? s.apply( t ) : r;
        };
    }
}
