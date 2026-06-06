package com.example.nlp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String BATCH_TRANSLATE_QUEUE = "batch.translate.queue";
    public static final String BATCH_TRANSLATE_EXCHANGE = "batch.translate.exchange";
    public static final String BATCH_TRANSLATE_ROUTING_KEY = "batch.translate";

    @Bean
    public Queue batchTranslateQueue() {
        return QueueBuilder.durable(BATCH_TRANSLATE_QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public DirectExchange batchTranslateExchange() {
        return new DirectExchange(BATCH_TRANSLATE_EXCHANGE);
    }

    @Bean
    public Binding batchTranslateBinding(Queue batchTranslateQueue, DirectExchange batchTranslateExchange) {
        return BindingBuilder.bind(batchTranslateQueue)
                .to(batchTranslateExchange)
                .with(BATCH_TRANSLATE_ROUTING_KEY);
    }

    @Bean
    public Declarables deadLetterQueue() {
        var dlx = new DirectExchange("dlx.exchange");
        var dlq = QueueBuilder.durable("dlq").build();
        return new Declarables(dlx, dlq, BindingBuilder.bind(dlq).to(dlx).with("batch.translate"));
    }
}
