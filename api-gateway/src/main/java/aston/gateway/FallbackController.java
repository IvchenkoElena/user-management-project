package aston.gateway;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback/user-service", produces = MediaType.APPLICATION_JSON_VALUE)
    public String userServiceFallback() {
        return "{\"message\":\"User service is temporarily unavailable\"}";
    }

    @RequestMapping(value = "/fallback/notification-service", produces = MediaType.APPLICATION_JSON_VALUE)
    public String notificationServiceFallback() {
        return "{\"message\":\"Notification service is temporarily unavailable\"}";
    }
}
