package br.edu.infnet.JulioJubiladoapi.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.service.TarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

	// Construtor (Injeção de dependência)
	// final -> imutabilidade e segurança; a dependência é obrigatória
	private final TarefaService tarefaService;

	public TarefaController(TarefaService tarefaService) {
		this.tarefaService = tarefaService;
	}

	@PostMapping
	public ResponseEntity<Tarefa> incluir(@Valid @RequestBody Tarefa tarefa) {
		
		Tarefa novaTarefa = tarefaService.incluir(tarefa);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Tarefa> alterar(@PathVariable UUID id, @Valid @RequestBody Tarefa tarefa) {
		Tarefa tarefaAlterada = tarefaService.alterar(id, tarefa);
		return ResponseEntity.ok(tarefaAlterada);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable UUID id) {
		tarefaService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<Page<Tarefa>> obterLista(Pageable pageable) {
		return ResponseEntity.ok(tarefaService.obterLista(pageable));
	}

	@GetMapping(value = "/{id}")
	public Tarefa obterPorId(@PathVariable("id") UUID id) {
		return tarefaService.obterPorId(id);
	}
}
