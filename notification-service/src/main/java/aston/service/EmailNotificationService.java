package aston.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.website.url:your-website.com}")
    private String websiteUrl;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification(String email, String operation) {
        System.out.println("[EmailNotificationService] Отправка email для: " + email + ", операция: " + operation);

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

        System.out.println("[EmailNotificationService] Отправляем сообщение через mailSender");
        mailSender.send(message);
        System.out.println("[EmailNotificationService] Сообщение отправлено");
    }
}
