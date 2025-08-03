package br.edu.infnet.JulioJubiladoapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;

@RestController
@RequestMapping("/api/Funcionarios")
public class FuncionarioController {
	
	//Construtor (Injeção de dependência)
	//final -> Devido a imutabilidade e segurança no código; a dependência é obrigatória;
	private final FuncionarioService funcionarioService;
	
	public FuncionarioController(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;
		
	
	}
	
	@GetMapping
	public Funcionario obterFuncionario() {
		
		return funcionarioService.obter();
	}
}