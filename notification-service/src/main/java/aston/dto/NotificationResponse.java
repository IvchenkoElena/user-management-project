package aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationResponse(
        @Schema(description = "Result message", example = "Уведомление отправлено успешно")
        String message
) {}

