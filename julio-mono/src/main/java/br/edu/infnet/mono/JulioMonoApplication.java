package br.edu.infnet.mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JulioMonoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JulioMonoApplication.class, args);
	}

}
