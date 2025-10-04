package br.edu.infnet.mono.controllers;

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

import br.edu.infnet.mono.model.domain.TicketTarefa;
import br.edu.infnet.mono.model.dto.TicketTarefaRequestDTO;
import br.edu.infnet.mono.model.dto.TicketTarefaResponseDTO;
import br.edu.infnet.mono.service.TicketTarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketTarefaController {

	private final TicketTarefaService ticketTarefaService;

	public TicketTarefaController(TicketTarefaService ticketTarefaService) {
		this.ticketTarefaService = ticketTarefaService;
	}

	@PostMapping
	public ResponseEntity<TicketTarefaResponseDTO> incluir(@Valid @RequestBody TicketTarefaRequestDTO ticketTarefaRequestDTO) {
		try {
			TicketTarefaResponseDTO novoTicketTarefa = ticketTarefaService.incluir(ticketTarefaRequestDTO);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoTicketTarefa);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<TicketTarefaResponseDTO> alterar(@PathVariable Integer id, @Valid @RequestBody TicketTarefa ticket) {
		try {
			TicketTarefaResponseDTO alterado = ticketTarefaService.alterar(id, ticket);
			return ResponseEntity.ok(alterado);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		try {
			ticketTarefaService.excluir(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping(value = "/{id}/inativar")
	public ResponseEntity<TicketTarefaResponseDTO> inativar(@PathVariable Integer id) {
		try {
			TicketTarefaResponseDTO ticket = ticketTarefaService.inativar(id);
			return ResponseEntity.ok(ticket);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping
	public ResponseEntity<List<TicketTarefaResponseDTO>> obterLista() {
		List<TicketTarefaResponseDTO> lista = ticketTarefaService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<TicketTarefaResponseDTO> obterPorId(@PathVariable Integer id) {
		TicketTarefaResponseDTO ticket = ticketTarefaService.obterPorId(id);
		if (ticket == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(ticket);
	}
}