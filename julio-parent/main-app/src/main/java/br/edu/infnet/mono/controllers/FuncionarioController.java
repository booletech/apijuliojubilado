package br.edu.infnet.mono.controllers;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.api.dto.funcionario.FuncionarioRequestDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioResponseDTO;
import br.edu.infnet.mono.service.FuncionarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<FuncionarioResponseDTO> listar() {
        return funcionarioService.findAll();
    }

    @GetMapping("/{id}")
    public FuncionarioResponseDTO buscarPorId(@PathVariable Integer id) {
        return funcionarioService.findById(id);
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> criar(@Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        FuncionarioResponseDTO criado = funcionarioService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public FuncionarioResponseDTO atualizar(@PathVariable Integer id,
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        return funcionarioService.update(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        funcionarioService.delete(id);
    }
}
