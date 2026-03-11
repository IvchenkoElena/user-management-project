package aston.listener;

import aston.dto.UserEvent;
import aston.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {
    private final EmailNotificationService emailService;

    public UserEventListener(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event, Acknowledgment acknowledgment) {
        log.info("[UserEventListener] Получено событие: {}", event);
        emailService.sendNotification(event.getEmail(), event.getOperation());
        acknowledgment.acknowledge();
        log.info("[UserEventListener] Обработка завершена");
    }
}

