package dev.area51.broker;

import dev.area51.xsd.amqrabbitbridge.v1.MessageBroker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention ( RetentionPolicy.RUNTIME )
@Documented
@Target ( ElementType.TYPE )
public @interface BrokerType
{
    Class<? extends MessageBroker> value( );
}
