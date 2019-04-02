package dev.area51.broker.bridge;

import com.rabbitmq.client.AMQP;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MessageBridge takes a JMS Message from ActiveMQ and submits it to RabbitMQ
 */
class MessageBridge
    implements Consumer<Message>
{
    private static final Logger LOG = Logger.getLogger( MessageBridge.class.getName( ) );

    // The rabbitMQ BiConsumer so we can pass on the message headers
    private final BiConsumer<AMQP.BasicProperties, byte[]> consumer;

    // Function to convert a BytesMessage to a String, usually either the plain one or the gunzip one
    private final Function<BytesMessage, String> converter;

    // Destination routing key - used for debugging only if we ever receive a message thats not a BytesMessage or a TextMessage
    private final String routingKey;

    MessageBridge( final BiConsumer<AMQP.BasicProperties, byte[]> consumer,
                   final Function<BytesMessage, String> converter,
                   final String routingKey )
    {
        this.consumer = Objects.requireNonNull( consumer );
        this.converter = converter;
        this.routingKey = routingKey;
    }

    @Override
    public void accept( Message message )
    {
        try
        {
            if ( message == null )
            {
                return;
            }

            String xml = null;
            if ( message instanceof BytesMessage )
            {
                xml = converter.apply( ( BytesMessage ) message );
            }
            else if ( message instanceof TextMessage )
            {
                xml = ( ( TextMessage ) message ).getText( );
            }
            else
            {
                LOG.log( Level.INFO,
                         routingKey + ": received " + message.getClass( ) + ": " + message );
            }

            if ( xml != null )
            {
                consumer.accept( getProperties( message ),
                                 xml.getBytes( ) );
            }

        }
        catch ( Exception ex )
        {
            LOG.log( Level.INFO,
                     null,
                     ex );
        }
    }

    /**
     * Extracts the message properties from the source message ready for submission to rabbitmq
     *
     * @param m
     *
     * @return
     *
     * @throws JMSException
     */
    @SuppressWarnings ( "unchecked" )
    private AMQP.BasicProperties getProperties( final Message m )
        throws
        JMSException
    {
        Map<String, Object> properties = new HashMap<>( );
        Enumeration en = m.getPropertyNames( );
        while ( en.hasMoreElements( ) )
        {
            String propertyName = ( String ) en.nextElement( );
            properties.put( propertyName,
                            m.getObjectProperty( propertyName ) );
        }

        return new AMQP.BasicProperties( )
            .builder( )
            .messageId( m.getJMSMessageID( ) )
            .timestamp( new Date( m.getJMSTimestamp( ) ) )
            .headers( properties )
            .build( );
    }

}
