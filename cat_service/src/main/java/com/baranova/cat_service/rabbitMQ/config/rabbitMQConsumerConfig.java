package com.baranova.cat_service.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class rabbitMQConsumerConfig {
    @Value("${spring.rabbitmq.queue.name_get}")
    private String consumerQueueName;
    @Value("${spring.rabbitmq.exchange.name_get}")
    private String consumerExchangeName;
    @Value("${spring.rabbitmq.routing.key_get}")
    private String consumerRoutingKey;

    @Bean
    public Queue myQueue() {
        return new Queue(consumerQueueName, false);
    }

    @Bean(name = "consumerExchange")
    public Exchange exchange() {
        return new DirectExchange(consumerExchangeName, true, false);
    }

    @Bean(name = "consumerBinding")
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(consumerRoutingKey).noargs();
    }

    @Bean(name = "consumerQueueName")
    public String queueName() {
        return consumerQueueName;
    }
}