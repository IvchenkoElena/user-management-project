package aston.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {
    private String name;
    @Email(message = "Некорректный формат email")
    private String email;
    @Min(value = 0, message = "Возраст должен быть положительным")
    private Integer age;

    public boolean isNameSet() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean isEmailSet() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean isAgeSet() {
        return age != null;
    }
}
