package br.edu.infnet.mono.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ApiController {
	
	@GetMapping
	public String Hello() {
		return "Hello World!";
	}

}
 