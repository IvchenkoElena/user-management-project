package aston.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @Email(message = "Некорректный формат email")
        String email,
        @NotBlank(message = "Операция не может быть пустой")
        String operation
) {}