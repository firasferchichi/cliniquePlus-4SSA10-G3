package tn.esprit.clinique.personnel.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bus d'événements commun : exchange topic « clinique.events ».
 * Chaque service consommateur possède SES propres files (une par événement écouté).
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "clinique.events";

    @Bean
    TopicExchange clinicExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
