package aston.gateway;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping(value = "/fallback/user-service", produces = MediaType.APPLICATION_JSON_VALUE)
    public String userServiceFallback() {
        return "{\"message\":\"User service is temporarily unavailable\"}";
    }

    @GetMapping(value = "/fallback/notification-service", produces = MediaType.APPLICATION_JSON_VALUE)
    public String notificationServiceFallback() {
        return "{\"message\":\"Notification service is temporarily unavailable\"}";
    }
}
