package aston.controller;

import aston.dto.UserEvent;
import aston.service.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest(properties = "spring.profiles.active=test")
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
class NotificationServiceKafkaIntegrationTest {
    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;
    @Autowired
    private EmailNotificationService emailService;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    void shouldSendEmailOnUserCreation() throws Exception {
        String testEmail = "test@example.com";
        UserEvent event = new UserEvent("CREATE", testEmail);
        CountDownLatch latch = new CountDownLatch(1);

        willAnswer(invocation -> {
            log.info("[Тест] mailSender.send() вызван!");
            latch.countDown();
            return null;
        }).given(mailSender).send(any(SimpleMailMessage.class));

        log.info("[Тест] Отправляем сообщение в Kafka...");
        kafkaTemplate.send("user-events", event);

        boolean emailSent = latch.await(20, TimeUnit.SECONDS);
        if (!emailSent) {
            log.error("ОШИБКА: Email не был отправлен в течение 20 секунд");
            log.warn("Проверьте логи выше — должно быть:");
            log.warn("1. [UserEventListener] Получено событие");
            log.warn("2. [EmailNotificationService] Отправка email");
            log.warn("3. [Тест] mailSender.send() вызван");
        }
        assertTrue(emailSent, "Email не был отправлен в течение 20 секунд");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
