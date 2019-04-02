package dev.area51.broker;

import dev.area51.runtime.Service;
import dev.area51.xsd.amqrabbitbridge.v1.MessageBroker;

public interface BrokerFactory<T extends MessageBroker>
{
    Service create( T definition );
}
