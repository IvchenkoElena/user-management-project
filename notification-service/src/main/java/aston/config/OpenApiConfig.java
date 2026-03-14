package aston.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Notification API",
                version = "v1",
                description = "API for sending notifications."
        ),
        servers = {
                @Server(url = "http://localhost:8082", description = "Local")
        }
)
public class OpenApiConfig {
}
