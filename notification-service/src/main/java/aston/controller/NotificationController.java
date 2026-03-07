package aston.controller;

import aston.dto.NotificationRequest;
import aston.service.EmailNotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            System.out.println("Отправляем нотификацию: " + request);

            return ResponseEntity.ok("Уведомление отправлено успешно");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка отправки: " + e.getMessage());
        }
    }
}

