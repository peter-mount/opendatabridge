package dev.area51.broker.activemq.sources;

import dev.area51.broker.consumer.ConsumerBuilder;

public interface SourceFactory
{
    Runnable create( ConsumerBuilder builder );
}
