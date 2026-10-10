package x10.zenfit.api.portal.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
        scanBasePackages = "x10.zenfit",
        exclude = UserDetailsServiceAutoConfiguration.class
)
@EnableMongoRepositories(basePackages = "x10.zenfit")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}