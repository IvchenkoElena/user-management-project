package aston.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @Schema(description = "Recipient email", example = "user@example.com")
        @Email(message = "Некорректный формат email")
        String email,
        @Schema(description = "Operation type", example = "USER_CREATED")
        @NotBlank(message = "Операция не может быть пустой")
        String operation
) {}