package com.example.application_service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_QUEUE = "notification_queue";

    public static final String JOB_TITLE_REQUEST_QUEUE = "jobTitleRequestQueue";

    public static final String JOB_TITLE_RESPONSE_QUEUE = "jobTitleResponseQueue";

    public static final String JOB_TITLE_EXCHANGE = "jobTitleExchange";

    public static final String JOB_TITLE_REQUEST_ROUTING_KEY = "jobTitle";

    public static final String JOB_TITLE_RESPONSE_ROUTING_KEY = "jobTitleResponse";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(JOB_TITLE_REQUEST_QUEUE);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(JOB_TITLE_RESPONSE_QUEUE);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(JOB_TITLE_EXCHANGE);
    }

    @Bean
    public Binding bindingRequest(Queue requestQueue, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue).to(exchange).with(JOB_TITLE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding bindingResponse(Queue responseQueue, DirectExchange exchange) {
        return BindingBuilder.bind(responseQueue).to(exchange).with(JOB_TITLE_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register module for Java 8 date/time API

        // Configure ObjectMapper to handle specific classes

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
