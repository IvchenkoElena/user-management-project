package aston.controller;

import aston.dto.NotificationRequest;
import aston.service.EmailNotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final EmailNotificationService emailService;

    public NotificationController(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendManualNotification(@Valid @RequestBody NotificationRequest request) {
        try {
            emailService.sendNotification(request.email(), request.operation());
            log.info("Отправляем нотификацию: {}", request);

            log.info("Уведомление успешно отправлено для: {}", request.email());
            return ResponseEntity.ok("Уведомление отправлено успешно");
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления для запроса: {}", request, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка отправки: " + e.getMessage());
        }
    }
}

