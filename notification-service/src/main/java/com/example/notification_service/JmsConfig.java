package com.example.notification_service;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        // We're consuming queues (not topics)
        factory.setPubSubDomain(false);

        // Force single-threaded processing per container (reduce concurrency issues)
        factory.setConcurrency("1");

        // Use AUTO_ACK so JMS broker considers message consumed when listener returns normally.
        // If transactional DB work must roll back, combine carefully; using UPSERT below makes idempotent.
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);

        // Simple error handler to log — avoids throwing and uncontrolled redelivery loops
        factory.setErrorHandler(t -> {
            System.err.println("JMS listener error handled (preventing redelivery): " + t.getMessage());
            t.printStackTrace();
        });

        return factory;
    }
}
