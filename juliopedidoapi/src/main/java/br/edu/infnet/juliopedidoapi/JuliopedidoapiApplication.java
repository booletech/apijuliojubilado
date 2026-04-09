package br.edu.infnet.juliopedidoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "br.edu.infnet.juliopedidoapi.model.clients")
public class JuliopedidoapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JuliopedidoapiApplication.class, args);
	}

}

