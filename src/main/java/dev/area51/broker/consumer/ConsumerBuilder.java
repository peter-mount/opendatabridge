package dev.area51.broker.consumer;

import dev.area51.broker.activemq.RemoteActiveMQConnection;
import dev.area51.runtime.Context;
import dev.area51.util.Consumers;
import dev.area51.util.Functions;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQBase;
import dev.area51.xsd.amqrabbitbridge.v1.ConsumerDefinition;

import javax.jms.Message;
import javax.xml.bind.JAXBElement;
import java.util.Objects;
import java.util.function.Consumer;

public class ConsumerBuilder
{
    private ActiveMQBase definition;
    private RemoteActiveMQConnection activeMQ;

    private Context context;
    private Consumer<Message> consumer;

    public ConsumerBuilder( ActiveMQBase definition )
    {
        this.definition = definition;
    }

    public <T extends ActiveMQBase> T getDefinition( )
    {
        return ( T ) definition;
    }

    public Context getContext( )
    {
        return context;
    }

    public ConsumerBuilder setContext( final Context context )
    {
        this.context = context;
        return this;
    }

    public RemoteActiveMQConnection getActiveMQ( )
    {
        return activeMQ;
    }

    public ConsumerBuilder setActiveMQ( final RemoteActiveMQConnection activeMQ )
    {
        this.activeMQ = activeMQ;
        return this;
    }

    public Consumer<Message> getConsumer( )
    {
        return consumer;
    }

    public ConsumerBuilder generateConsumer( )
    {
        consumer = definition
            .getAny( )
            .stream( )
            .map( Functions.castTo( JAXBElement.class ) )
            .filter( Objects::nonNull )
            .map( JAXBElement::getValue )
            .map( Functions.castTo( ConsumerDefinition.class ) )
            .filter( Objects::nonNull )
            .map( d -> ConsumerRepository.INSTANCE.createConsumer( this,
                                                                   d ) )
            .reduce( Consumers::andThenGuarded )
            .orElseThrow( ( ) -> new UnsupportedOperationException( "No message consumers declared" ) );
        return this;
    }

}
