package br.edu.infnet.JulioJubiladoapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
	
	//Construtor (Injeção de dependência)
	//final -> Devido a imutabilidade e segurança no código; a dependência é obrigatória;
	private final FuncionarioService funcionarioService;
	
	public FuncionarioController(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;
	}
	
	@PostMapping
	public Funcionario incluir(@RequestBody Funcionario funcionario) {
		return funcionarioService.incluir(funcionario);
	}
	
	@PutMapping(value = "/{id}")
	public Funcionario alterar(@PathVariable Integer id, @RequestBody Funcionario funcionario) {
		return funcionarioService.alterar(id, funcionario);	
	}
	
	@DeleteMapping(value = "/{id}")
	public void excluir(@PathVariable Integer id) {
		funcionarioService.excluir(id);
	}

	
	
	@PatchMapping(value = "/{id}/inativar")
	public Funcionario inativar(@PathVariable  Integer id ) {
		return funcionarioService.inativar(id);
	}
	
	
	@GetMapping
	public List<Funcionario> obterLista(){
		return funcionarioService.obterLista();
	}
	
	
	
	@GetMapping(value = "/{id}") // trás um funcionario pelo id
	public Funcionario obterPorId(@PathVariable("id")  Integer id) {
		return funcionarioService.obterPorId(id);
	}
}