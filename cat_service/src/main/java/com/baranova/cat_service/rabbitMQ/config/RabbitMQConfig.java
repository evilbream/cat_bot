package com.baranova.cat_service.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.queue.name_produce}")
    private String produceQueueName;

    @Value("${spring.rabbitmq.queue.name_consume}")
    private String consumeQueueName;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key_produce}")
    private String produceRoutingKey;

    @Value("${spring.rabbitmq.routing.key_consume}")
    private String consumeRoutingKey;

    @Bean
    public Queue produceQueue() {
        return new Queue(produceQueueName, true);
    }

    @Bean
    public Queue consumeQueue() {
        return new Queue(consumeQueueName, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding produceBinding(Queue produceQueue, DirectExchange exchange) {
        return BindingBuilder.bind(produceQueue).to(exchange).with(produceRoutingKey);
    }

    @Bean
    public Binding consumeBinding(Queue consumeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(consumeQueue).to(exchange).with(consumeRoutingKey);
    }
}