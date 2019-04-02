package dev.area51.broker.consumer;

import dev.area51.xsd.amqrabbitbridge.v1.ConsumerDefinition;

import javax.jms.Message;
import java.util.function.Consumer;

public interface ConsumerFactory<T extends ConsumerDefinition>
{
    Consumer<Message> create( ConsumerBuilder builder,
                              T definition );
}
