package br.edu.infnet.JulioJubiladoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JulioJubiladoapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(JulioJubiladoapiApplication.class, args);
	}

}
