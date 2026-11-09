package tn.esprit.clinique.facturation.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "clinique.events";
    public static final String CONSULTATION_QUEUE = "facturation-service.consultation.terminee";
    public static final String MEDICAMENTS_QUEUE = "facturation-service.medicaments.delivres";
    public static final String DEAD_LETTER_EXCHANGE = "facturation-service.dlx";
    public static final String CONSULTATION_DLQ = CONSULTATION_QUEUE + ".dlq";
    public static final String MEDICAMENTS_DLQ = MEDICAMENTS_QUEUE + ".dlq";
    private static final String CONSULTATION_DEAD_LETTER_KEY = "consultation.terminee.failed";
    private static final String MEDICAMENTS_DEAD_LETTER_KEY = "medicaments.delivres.failed";

    @Bean
    TopicExchange clinicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue consultationTermineeQueue() {
        return QueueBuilder.durable(CONSULTATION_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(CONSULTATION_DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    Queue medicamentsDelivresQueue() {
        return QueueBuilder.durable(MEDICAMENTS_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(MEDICAMENTS_DEAD_LETTER_KEY)
                .build();
    }

    @Bean
    Queue consultationTermineeDeadLetterQueue() {
        return QueueBuilder.durable(CONSULTATION_DLQ).build();
    }

    @Bean
    Queue medicamentsDelivresDeadLetterQueue() {
        return QueueBuilder.durable(MEDICAMENTS_DLQ).build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Binding consultationTermineeBinding(
            @Qualifier("consultationTermineeQueue") Queue consultationTermineeQueue,
            TopicExchange clinicExchange) {
        return BindingBuilder.bind(consultationTermineeQueue).to(clinicExchange).with("consultation.terminee");
    }

    @Bean
    Binding medicamentsDelivresBinding(
            @Qualifier("medicamentsDelivresQueue") Queue medicamentsDelivresQueue,
            TopicExchange clinicExchange) {
        return BindingBuilder.bind(medicamentsDelivresQueue).to(clinicExchange).with("medicaments.delivres");
    }

    @Bean
    Binding consultationDeadLetterBinding(
            @Qualifier("consultationTermineeDeadLetterQueue") Queue deadLetterQueue,
            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(CONSULTATION_DEAD_LETTER_KEY);
    }

    @Bean
    Binding medicamentsDeadLetterBinding(
            @Qualifier("medicamentsDelivresDeadLetterQueue") Queue deadLetterQueue,
            DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(MEDICAMENTS_DEAD_LETTER_KEY);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
