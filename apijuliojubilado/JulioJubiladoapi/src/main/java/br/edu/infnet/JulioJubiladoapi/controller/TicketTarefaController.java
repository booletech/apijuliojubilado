package br.edu.infnet.JulioJubiladoapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
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
	public ResponseEntity<TicketTarefa> alterar(@PathVariable Integer id, @RequestBody TicketTarefa ticket) {
		TicketTarefa alterado = ticketService.alterar(id, ticket);
		return ResponseEntity.ok(alterado);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		ticketService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<TicketTarefa>> obterLista() {
		List<TicketTarefa> lista = ticketService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public TicketTarefa obterPorId(@PathVariable("id") Integer id) {
		return ticketService.obterPorId(id);
	}
}
