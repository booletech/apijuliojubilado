package br.edu.infnet.mono.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.infnet.mono.model.domain.Tarefa;
import br.edu.infnet.mono.model.dto.TarefaRequestDTO;
import br.edu.infnet.mono.service.TarefaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    public ResponseEntity<Tarefa> incluir(@Valid @RequestBody TarefaRequestDTO dto) {
        Tarefa nova = tarefaService.incluir(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> obterLista() {
        List<Tarefa> lista = tarefaService.obterLista();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> obterPorId(@PathVariable Integer id) {
        Tarefa tarefa = tarefaService.obterPorId(id);
        if (tarefa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        tarefaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}