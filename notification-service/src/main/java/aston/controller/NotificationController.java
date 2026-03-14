package aston.controller;

import aston.dto.NotificationRequest;
import aston.dto.NotificationResponse;
import aston.service.EmailNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Operations for sending notifications")
public class NotificationController {
    private final EmailNotificationService emailService;

    public NotificationController(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification", description = "Sends a notification email for a specified operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification sent",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content)
    })
    public ResponseEntity<EntityModel<NotificationResponse>> sendManualNotification(
            @Valid @RequestBody NotificationRequest request
    ) {
        Link selfLink = linkTo(NotificationController.class).slash("send").withSelfRel();
        Link docsLink = Link.of("/swagger-ui/index.html").withRel("docs");

        try {
            emailService.sendNotification(request.email(), request.operation());
            log.info("Отправляем нотификацию: {}", request);

            log.info("Уведомление успешно отправлено для: {}", request.email());
            NotificationResponse response = new NotificationResponse("Уведомление отправлено успешно");
            return ResponseEntity.ok(EntityModel.of(response, selfLink, docsLink));
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления для запроса: {}", request, e);
            NotificationResponse response = new NotificationResponse("Ошибка отправки: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EntityModel.of(response, selfLink, docsLink));
        }
    }
}