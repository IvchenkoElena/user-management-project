package aston.listener;

import aston.dto.UserEvent;
import aston.service.EmailNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {
    private final EmailNotificationService emailService;

    public UserEventListener(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        System.out.println("[UserEventListener] Получено событие: " + event);
        emailService.sendNotification(event.getEmail(), event.getOperation());
        System.out.println("[UserEventListener] Обработка завершена");
    }
}
