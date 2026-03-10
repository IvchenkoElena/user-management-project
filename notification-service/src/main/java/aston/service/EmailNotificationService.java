package aston.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.website.url}")
    private String websiteUrl;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(String email, String operation) {
        log.info("[EmailNotificationService] Отправка email для: {}, операция: {}", email, operation);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        if ("CREATE".equals(operation)) {
            message.setSubject("Аккаунт успешно создан");
            message.setText(String.format(
                    ("Здравствуйте!\n\n" +
                            "Ваш аккаунт на сайте %s был успешно создан.\n\n" +
                            "С уважением,\nКоманда сайта"),
                    websiteUrl
            ));

        } else if ("DELETE".equals(operation)) {
            message.setSubject("Аккаунт удалён");
            message.setText(String.format(
                    "Здравствуйте!\n\n" +
                            "Ваш аккаунт на сайте %s был удалён.\n\n" +
                            "С уважением,\nКоманда сайта",
                    websiteUrl
            ));
        }

        log.info("[EmailNotificationService] Отправляем сообщение через mailSender");
        mailSender.send(message);
        log.info("[EmailNotificationService] Сообщение отправлено");
    }
}
