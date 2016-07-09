package racegrid.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan({
        "racegrid.api.controller",
        "racegrid.api.service",
        "racegrid.api.config"
})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}