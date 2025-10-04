package br.edu.infnet.mono.model.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import br.edu.infnet.mono.model.domain.Tarefa;
import br.edu.infnet.mono.model.service.TarefaService;
import br.edu.infnet.mono.model.dto.TarefaRequestDTO;
import br.edu.infnet.mono.model.dto.TarefaResponseDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

	private final TarefaService tarefaService;

	public TarefaController(TarefaService tarefaService) {
		this.tarefaService = tarefaService;
	}

	@PostMapping
	public ResponseEntity<TarefaResponseDTO> incluir(@Valid @RequestBody TarefaRequestDTO tarefaRequestDTO) {
		Tarefa tarefa = toEntity(tarefaRequestDTO);
		Tarefa novaTarefa = tarefaService.incluir(tarefa);
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(novaTarefa));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TarefaResponseDTO> alterar(@PathVariable Integer id, @RequestBody TarefaRequestDTO tarefaRequestDTO) {
		Tarefa tarefa = toEntity(tarefaRequestDTO);
		Tarefa tarefaAlterada = tarefaService.alterar(id, tarefa);
		return ResponseEntity.ok(toResponseDTO(tarefaAlterada));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		tarefaService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/inativar")
	public ResponseEntity<TarefaResponseDTO> inativar(@PathVariable Integer id) {
		Tarefa tarefa = tarefaService.inativar(id);
		return ResponseEntity.ok(toResponseDTO(tarefa));
	}

	@GetMapping
	public ResponseEntity<List<TarefaResponseDTO>> obterLista() {
		List<Tarefa> lista = tarefaService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		List<TarefaResponseDTO> dtos = lista.stream().map(this::toResponseDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<TarefaResponseDTO> obterPorId(@PathVariable("id") Integer id) {
		Tarefa tarefa = tarefaService.obterPorId(id);
		if (tarefa == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(toResponseDTO(tarefa));
	}

	private Tarefa toEntity(TarefaRequestDTO dto) {
		Tarefa tarefa = new Tarefa();
		tarefa.setDescricao(dto.getDescricao());
		tarefa.setValor(dto.getValor());
		tarefa.setStatus(dto.getStatus());
		// TODO: Map ticket, cliente, funcionario, tipo (requires fetching by id or value)
		return tarefa;
	}

	private TarefaResponseDTO toResponseDTO(Tarefa tarefa) {
		TarefaResponseDTO dto = new TarefaResponseDTO();
		dto.setId(tarefa.getId());
		dto.setDescricao(tarefa.getDescricao());
		dto.setValor(tarefa.getValor());
		dto.setStatus(tarefa.getStatus());
		// TODO: Map ticketId, clienteId, funcionarioId, tipo
		return dto;
	}
}