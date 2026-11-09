package tn.esprit.clinique.rendezvous.messaging;

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

    @Bean
    Queue qPersonnelIndisponible() {
        return QueueBuilder.durable("rendezvous-service.personnel.indisponible").build();
    }

    @Bean
    Binding qPersonnelIndisponibleBinding(TopicExchange exchange) {
        return BindingBuilder.bind(qPersonnelIndisponible()).to(exchange).with("personnel.indisponible");
    }

    @Bean
    Queue qConsultationTerminee() {
        return QueueBuilder.durable("rendezvous-service.consultation.terminee").build();
    }

    @Bean
    Binding qConsultationTermineeBinding(TopicExchange exchange) {
        return BindingBuilder.bind(qConsultationTerminee()).to(exchange).with("consultation.terminee");
    }
}
