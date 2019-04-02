package dev.area51.broker.rabbitmq;

import dev.area51.broker.BrokerFactory;
import dev.area51.broker.BrokerType;
import dev.area51.runtime.Service;
import dev.area51.xsd.amqrabbitbridge.v1.RabbitMQ;
import org.kohsuke.MetaInfServices;

@BrokerType ( RabbitMQ.class )
@MetaInfServices ( BrokerFactory.class )
public class RabbitMQServiceFactory
    implements BrokerFactory<RabbitMQ>
{
    @Override
    public Service create( final RabbitMQ definition )
    {
        return new RabbitMQService( definition );
    }
}
