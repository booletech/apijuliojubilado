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

import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.service.TicketTarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketTarefaController {

	private final TicketTarefaService ticketService;

	public TicketTarefaController(TicketTarefaService ticketService) {
		this.ticketService = ticketService;
	}

	@PostMapping
	public ResponseEntity<TicketTarefa> incluir(@Valid @RequestBody TicketTarefa ticket) {
		TicketTarefa novo = ticketService.incluir(ticket);
		return ResponseEntity.status(HttpStatus.CREATED).body(novo);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<TicketTarefa> alterar(@PathVariable Integer id, @RequestBody TicketTarefa ticket) {
		TicketTarefa alterado = ticketService.alterar(id, ticket);
		return ResponseEntity.ok(alterado);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		ticketService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(value = "/{id}/inativar")
	public ResponseEntity<TicketTarefa> inativar(@PathVariable Integer id) {
		TicketTarefa ticket = ticketService.inativar(id);
		return ResponseEntity.ok(ticket);
	}

	@GetMapping
	public ResponseEntity<List<TicketTarefa>> obterLista() {
		List<TicketTarefa> lista = ticketService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<TicketTarefa> obterPorId(@PathVariable Integer id) {
		TicketTarefa ticket = ticketService.obterPorId(id);
		if (ticket == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(ticket);
	}
}