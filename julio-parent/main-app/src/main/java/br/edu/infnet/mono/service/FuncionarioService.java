package br.edu.infnet.mono.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioRequestDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioResponseDTO;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<FuncionarioResponseDTO> findAll() {
        return funcionarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FuncionarioResponseDTO findById(Integer id) {
        Funcionario funcionario = getFuncionarioOrThrow(id);
        return toResponse(funcionario);
    }

    public FuncionarioResponseDTO create(FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(null);
        applyRequestData(funcionario, requestDTO);

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return toResponse(salvo);
    }

    public FuncionarioResponseDTO update(Integer id, FuncionarioRequestDTO requestDTO) {
        Funcionario existente = getFuncionarioOrThrow(id);
        applyRequestData(existente, requestDTO);

        Funcionario atualizado = funcionarioRepository.save(existente);
        return toResponse(atualizado);
    }

    public void delete(Integer id) {
        Funcionario existente = getFuncionarioOrThrow(id);
        funcionarioRepository.delete(existente);
    }

    private Funcionario getFuncionarioOrThrow(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado: " + id));
    }

    private void applyRequestData(Funcionario funcionario, FuncionarioRequestDTO requestDTO) {
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

        EnderecoRequestDTO enderecoRequest = requestDTO.getEndereco();
        if (enderecoRequest != null) {
            Endereco endereco = toEnderecoEntity(enderecoRequest);
            Endereco enderecoExistente = funcionario.getEndereco();
            if (enderecoExistente != null) {
                endereco.setId(enderecoExistente.getId());
            }
            funcionario.setEndereco(endereco);
        } else {
            funcionario.setEndereco(null);
        }
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
