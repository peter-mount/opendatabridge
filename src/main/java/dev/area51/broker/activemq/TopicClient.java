/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.area51.broker.activemq;

import dev.area51.util.Consumers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a Topic and when a message is received it then hands it to the Consumer
 */
class TopicClient
    implements MQClient
{

    private static final Logger LOG = Logger.getLogger( TopicClient.class.getName( ) );

    private final String topicName;
    private final Consumer<Message> consumer;
    private final int hashCode;
    private Session session;
    private MessageConsumer messageConsumer;
    private Thread thread;
    private volatile boolean running;
    private final RemoteActiveMQConnection connection;

    public TopicClient( final RemoteActiveMQConnection connection,
                        final String topicName,
                        final Consumer<Message> consumer )
    {
        this.connection = Objects.requireNonNull( connection );
        this.topicName = Objects.requireNonNull( topicName );
        this.consumer = Consumers.guard( LOG,
                                         Objects.requireNonNull( consumer ) );
        hashCode = topicName.hashCode( );
    }

    @Override
    public Session getSession( )
    {
        return session;
    }

    @Override
    public int hashCode( )
    {
        return hashCode;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null || getClass( ) != obj.getClass( ) )
        {
            return false;
        }
        final TopicClient other = ( TopicClient ) obj;
        return Objects.equals( this.topicName,
                               other.topicName );
    }

    @Override
    public void start( )
    {
        LOG.info( ( ) -> "Client " + topicName + " starting" );
        try
        {
            session = connection
                .getConnection( )
                .createSession( false,
                                Session.AUTO_ACKNOWLEDGE );
            final Topic topic = session.createTopic( topicName );
            messageConsumer = session.createDurableSubscriber( topic,
                                                               topicName + connection.getUsername( ) );
            if ( thread == null )
            {
                thread = new Thread( ( ) -> {
                    LOG.info( ( ) -> "Starting to receive from " + topicName + " on " + connection.getBrokerUri( ) );
                    running = true;
                    try
                    {
                        while ( running )
                        {
                            Message message = messageConsumer.receive( 1000L );
                            if ( message != null )
                            {
                                consumer.accept( message );
                            }
                        }
                    }
                    catch ( Exception ex )
                    {
                        LOG.log( Level.SEVERE,
                                 "Exception in topic " + topicName + " on " + connection.getBrokerUri( ),
                                 ex );
                        if ( running )
                        {
                            connection.reconnect( );
                        }
                    }
                    finally
                    {
                        thread = null;
                    }
                    LOG.info( ( ) -> "Receive thread for " + topicName + " terminated" + " on " + connection.getBrokerUri( ) );
                } );
                thread.start( );

                connection
                    .getConnection( )
                    .setExceptionListener( e -> LOG.info( ( ) -> topicName + " Exception:" + e ) );
            }
        }
        catch ( JMSException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public TopicClient stop( )
    {
        LOG.info( ( ) -> "Client " + topicName + " on " + connection.getBrokerUri( ) + " stopping" );
        if ( consumer != null && thread != null )
        {
            try
            {
                try
                {
                    try
                    {
                        messageConsumer.close( );
                    }
                    catch ( JMSException ex )
                    {
                        LOG.log( Level.SEVERE,
                                 null,
                                 ex );
                    }
                }
                finally
                {
                    try
                    {
                        session.close( );
                    }
                    catch ( JMSException ex )
                    {
                        LOG.log( Level.SEVERE,
                                 null,
                                 ex );
                    }
                }
            }
            finally
            {
                running = false;
            }
        }
        return null;
    }

}
