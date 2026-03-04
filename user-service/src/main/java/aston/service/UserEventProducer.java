package aston.service;

import aston.dto.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreated(String email) {
        kafkaTemplate.send("user-events", new UserEvent("CREATE", email));
    }

    public void sendUserDeleted(String email) {
        kafkaTemplate.send("user-events", new UserEvent("DELETE", email));
    }
}