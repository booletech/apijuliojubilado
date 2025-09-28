package br.edu.infnet.mono.controllers;

import java.util.List;
import java.util.stream.Collectors;

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

import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioRequestDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioResponseDTO;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
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
        return funcionarioService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public FuncionarioResponseDTO buscarPorId(@PathVariable Integer id) {
        Funcionario funcionario = funcionarioService.findById(id);
        return toResponse(funcionario);
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> criar(@Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = toEntity(requestDTO);
        Funcionario criado = funcionarioService.create(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @PutMapping("/{id}")
    public FuncionarioResponseDTO atualizar(@PathVariable Integer id,
            @Valid @RequestBody FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = toEntity(requestDTO);
        Funcionario atualizado = funcionarioService.update(id, funcionario);
        return toResponse(atualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        funcionarioService.delete(id);
    }

    private Funcionario toEntity(FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(requestDTO.getNome());
        funcionario.setCpf(requestDTO.getCpf());
        funcionario.setEmail(requestDTO.getEmail());
        funcionario.setTelefone(requestDTO.getTelefone());
        funcionario.setMatricula(requestDTO.getMatricula());
        funcionario.setSalario(requestDTO.getSalario());
        funcionario.setAtivo(Boolean.TRUE.equals(requestDTO.getAtivo()));
        funcionario.setCepInput(requestDTO.getCepInput());
        funcionario.setCargo(requestDTO.getCargo());
        funcionario.setTurno(requestDTO.getTurno());
        funcionario.setEscolaridade(requestDTO.getEscolaridade());
        funcionario.setDataNascimento(requestDTO.getDataNascimento());
        funcionario.setDataContratacao(requestDTO.getDataContratacao());
        funcionario.setDataDemissao(requestDTO.getDataDemissao());

        funcionario.setEndereco(toEnderecoEntity(requestDTO.getEndereco()));
        return funcionario;
    }

    private Endereco toEnderecoEntity(EnderecoRequestDTO enderecoRequest) {
        if (enderecoRequest == null) {
            return null;
        }
        Endereco endereco = new Endereco();
        endereco.setCep(enderecoRequest.getCep());
        endereco.setLogradouro(enderecoRequest.getLogradouro());
        endereco.setComplemento(enderecoRequest.getComplemento());
        endereco.setBairro(enderecoRequest.getBairro());
        endereco.setLocalidade(enderecoRequest.getLocalidade());
        endereco.setUf(enderecoRequest.getUf());
        endereco.setEstado(enderecoRequest.getEstado());
        return endereco;
    }

    private FuncionarioResponseDTO toResponse(Funcionario funcionario) {
        FuncionarioResponseDTO responseDTO = new FuncionarioResponseDTO();
        responseDTO.setId(funcionario.getId());
        responseDTO.setNome(funcionario.getNome());
        responseDTO.setCpf(funcionario.getCpf());
        responseDTO.setEmail(funcionario.getEmail());
        responseDTO.setTelefone(funcionario.getTelefone());
        responseDTO.setMatricula(funcionario.getMatricula());
        responseDTO.setSalario(funcionario.getSalario());
        responseDTO.setAtivo(funcionario.isAtivo());
        responseDTO.setCepInput(funcionario.getCepInput());
        responseDTO.setCargo(funcionario.getCargo());
        responseDTO.setTurno(funcionario.getTurno());
        responseDTO.setEscolaridade(funcionario.getEscolaridade());
        responseDTO.setDataNascimento(funcionario.getDataNascimento());
        responseDTO.setDataContratacao(funcionario.getDataContratacao());
        responseDTO.setDataDemissao(funcionario.getDataDemissao());

        responseDTO.setEndereco(toEnderecoResponse(funcionario.getEndereco()));
        return responseDTO;
    }

    private EnderecoResponseDTO toEnderecoResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoResponseDTO responseDTO = new EnderecoResponseDTO();
        responseDTO.setId(endereco.getId());
        responseDTO.setCep(endereco.getCep());
        responseDTO.setLogradouro(endereco.getLogradouro());
        responseDTO.setComplemento(endereco.getComplemento());
        responseDTO.setBairro(endereco.getBairro());
        responseDTO.setLocalidade(endereco.getLocalidade());
        responseDTO.setUf(endereco.getUf());
        responseDTO.setEstado(endereco.getEstado());
        return responseDTO;
    }
}
