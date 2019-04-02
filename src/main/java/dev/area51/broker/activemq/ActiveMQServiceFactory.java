package dev.area51.broker.activemq;

import dev.area51.broker.BrokerFactory;
import dev.area51.broker.BrokerType;
import dev.area51.runtime.Service;
import dev.area51.xsd.amqrabbitbridge.v1.ActiveMQ;
import org.kohsuke.MetaInfServices;

@BrokerType ( ActiveMQ.class )
@MetaInfServices ( BrokerFactory.class )
public class ActiveMQServiceFactory
    implements BrokerFactory<ActiveMQ>
{
    @Override
    public Service create( final ActiveMQ definition )
    {
        return new ActiveMQService( definition );
    }
}
