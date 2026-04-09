package br.edu.infnet.JulioJubiladoapi.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/tickets")
@Validated
public class TicketTarefaController {

	// Construtor (Injeção de dependência)
	// final -> imutabilidade e segurança; a dependência é obrigatória
	private final TicketTarefaService ticketService;

	public TicketTarefaController(TicketTarefaService ticketService) {
		this.ticketService = ticketService;
	}

	@PostMapping
	public ResponseEntity<TicketTarefa> incluir(@Valid @RequestBody TicketTarefa ticket) {
		TicketTarefa novo = ticketService.incluir(ticket);
		return ResponseEntity.status(HttpStatus.CREATED).body(novo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TicketTarefa> alterar(@PathVariable UUID id, @Valid @RequestBody TicketTarefa ticket) {
		TicketTarefa alterado = ticketService.alterar(id, ticket);
		return ResponseEntity.ok(alterado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable UUID id) {
		ticketService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<Page<TicketTarefa>> obterLista(Pageable pageable) {
		return ResponseEntity.ok(ticketService.obterLista(pageable));
	}

	@GetMapping("/por-cpf")
	public ResponseEntity<List<TicketTarefa>> obterPorCpf(
			@RequestParam
			@NotBlank(message = "CPF obrigatorio.")
			@Pattern(regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$", message = "CPF invalido.")
			String cpf) {
		return ResponseEntity.ok(ticketService.obterPorCpfCliente(cpf));
	}

	@GetMapping("/por-codigo")
	public ResponseEntity<TicketTarefa> obterPorCodigo(
			@RequestParam
			@NotBlank(message = "Codigo obrigatorio.")
			@Size(max = 30, message = "Codigo deve ter no maximo 30 caracteres.")
			String codigo) {
		return ResponseEntity.ok(ticketService.obterPorCodigo(codigo));
	}

	@GetMapping("/{id}")
	public TicketTarefa obterPorId(@PathVariable("id") UUID id) {
		return ticketService.obterPorId(id);
	}
}
