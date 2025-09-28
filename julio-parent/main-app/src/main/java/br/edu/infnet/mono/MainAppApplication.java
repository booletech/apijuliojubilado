package br.edu.infnet.mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "br.edu.infnet.mono.clients")
public class MainAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }
}
