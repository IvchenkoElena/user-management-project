package aston.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsoleApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ConsoleApplication.class, args);
        UserConsoleClient client = context.getBean(UserConsoleClient.class);
        client.run();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
