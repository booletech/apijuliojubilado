package br.edu.infnet.mono.model.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.model.dto.FuncionarioRequestDTO;
import br.edu.infnet.mono.model.dto.FuncionarioResponseDTO;
import br.edu.infnet.mono.model.service.FuncionarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

	private final FuncionarioService funcionarioService;

	public FuncionarioController(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;
	}

	@PostMapping
	public ResponseEntity<FuncionarioResponseDTO> incluir(@Valid @RequestBody FuncionarioRequestDTO funcionarioRequestDTO) {
		FuncionarioResponseDTO novoFuncionario = funcionarioService.incluir(funcionarioRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(novoFuncionario);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Funcionario> alterar(@PathVariable Integer id, @RequestBody Funcionario funcionario) {
		Funcionario funcionarioAlterado = funcionarioService.alterar(id, funcionario);

		return ResponseEntity.ok(funcionarioAlterado);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		funcionarioService.excluir(id);

		return ResponseEntity.noContent().build();

	}

	@PatchMapping(value = "/{id}/inativar")
	public ResponseEntity<Funcionario> inativar(@PathVariable Integer id) {
		Funcionario funcionario = funcionarioService.inativar(id);

		return ResponseEntity.ok(funcionario);
	}

	@GetMapping
	public ResponseEntity<List<Funcionario>> obterLista() {
		List<Funcionario> lista = funcionarioService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();

		}
		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public Funcionario obterPorId(@PathVariable Integer id) {	
		return funcionarioService.obterPorId(id);
	}
}