package br.edu.infnet.JulioJubiladoapi.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/funcionarios")
@Validated
public class FuncionarioController {

	// Construtor (Injeção de dependência)
	// final -> Devido a imutabilidade e segurança no código; a dependência é
	// obrigatória;
	private final FuncionarioService funcionarioService;

	public FuncionarioController(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;
	}

	@PostMapping
	public ResponseEntity<Funcionario> incluir(@Valid @RequestBody Funcionario funcionario) {
		Funcionario novoFuncionario = funcionarioService.incluir(funcionario);

		return ResponseEntity.status(HttpStatus.CREATED).body(novoFuncionario);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Funcionario> alterar(@PathVariable UUID id, @Valid @RequestBody Funcionario funcionario) {
		Funcionario funcionarioAlterado = funcionarioService.alterar(id, funcionario);

		return ResponseEntity.ok(funcionarioAlterado);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluir(@PathVariable UUID id) {
		funcionarioService.excluir(id);

		return ResponseEntity.noContent().build();

	}

	@PatchMapping(value = "/{id}/inativar")
	public ResponseEntity<Funcionario> inativar(@PathVariable UUID id) {
		Funcionario funcionario = funcionarioService.inativar(id);

		return ResponseEntity.ok(funcionario);
	}

	@GetMapping
	public ResponseEntity<Page<Funcionario>> obterLista(Pageable pageable) {
		return ResponseEntity.ok(funcionarioService.obterLista(pageable));
	}

	@GetMapping("/por-cpf")
	public ResponseEntity<Funcionario> obterPorCpf(
			@RequestParam
			@NotBlank(message = "CPF obrigatorio.")
			@Pattern(regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$", message = "CPF invalido.")
			String cpf) {
		return ResponseEntity.ok(funcionarioService.obterPorCpf(cpf));
	}

	@GetMapping(value = "/{id}")
	public Funcionario obterPorId(@PathVariable UUID id) {
		return funcionarioService.obterPorId(id);
	}
}
