package dev.area51.broker.consumer;

import dev.area51.xsd.amqrabbitbridge.v1.ConsumerDefinition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention ( RetentionPolicy.RUNTIME )
@Documented
@Target ( ElementType.TYPE )
public @interface ConsumerType
{
    Class<? extends ConsumerDefinition> value( );
}
