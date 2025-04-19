package com.baranova.tg_service.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQProducerConfig {
    @Value("${spring.rabbitmq.queue.name_send}")
    private String queueName;
    @Value("${spring.rabbitmq.exchange.name_send}")
    private String exchangeName;
    @Value("${spring.rabbitmq.routing.key_send}")
    private String routingKey;


    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    public Exchange exchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
    
}

