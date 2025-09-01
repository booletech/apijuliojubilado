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

	@PutMapping(value = "/{id}")
	public ResponseEntity<Tarefa> alterar(@PathVariable Integer id, @RequestBody Tarefa tarefa) {
		Tarefa tarefaAlterada = tarefaService.alterar(id, tarefa);
		return ResponseEntity.ok(tarefaAlterada);
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		tarefaService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<Tarefa>> obterLista() {
		List<Tarefa> lista = tarefaService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(lista);
	}

	@GetMapping(value = "/{id}")
	public Tarefa obterPorId(@PathVariable("id") Integer id) {
		return tarefaService.obterPorId(id);
	}
}
