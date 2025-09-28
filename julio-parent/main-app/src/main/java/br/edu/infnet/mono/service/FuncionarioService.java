package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioRequestDTO;
import br.edu.infnet.mono.api.dto.funcionario.FuncionarioResponseDTO;
import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.clients.ViaCepClient.ViaCepResponse;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ViaCepClient viaCepClient;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, ViaCepClient viaCepClient) {
        this.funcionarioRepository = funcionarioRepository;
        this.viaCepClient = viaCepClient;
    }

    public List<FuncionarioResponseDTO> findAll() {
        return funcionarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FuncionarioResponseDTO findById(Integer id) {
        Funcionario funcionario = findEntityById(id);
        return toResponse(funcionario);
    }

    public FuncionarioResponseDTO create(FuncionarioRequestDTO requestDTO) {
        Funcionario funcionario = new Funcionario();
        applyRequest(funcionario, requestDTO);
        funcionario.setId(null);
        Funcionario salvo = funcionarioRepository.save(funcionario);
        return toResponse(salvo);
    }

    public FuncionarioResponseDTO update(Integer id, FuncionarioRequestDTO requestDTO) {
        Funcionario existente = findEntityById(id);
        applyRequest(existente, requestDTO);
        Funcionario salvo = funcionarioRepository.save(existente);
        return toResponse(salvo);
    }

    public void delete(Integer id) {
        Funcionario existente = findEntityById(id);
        funcionarioRepository.delete(existente);
    }

    private Funcionario findEntityById(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado: " + id));
    }

    private void applyRequest(Funcionario funcionario, FuncionarioRequestDTO requestDTO) {
        funcionario.setNome(requestDTO.getNome());
        funcionario.setCpf(requestDTO.getCpf());
        funcionario.setEmail(requestDTO.getEmail());
        funcionario.setTelefone(requestDTO.getTelefone());
        funcionario.setMatricula(requestDTO.getMatricula());
        if (requestDTO.getSalario() != null) {
            funcionario.setSalario(requestDTO.getSalario());
        }
        funcionario.setAtivo(Boolean.TRUE.equals(requestDTO.getAtivo()));
        funcionario.setCepInput(requestDTO.getCepInput());
        funcionario.setCargo(requestDTO.getCargo());
        funcionario.setTurno(requestDTO.getTurno());
        funcionario.setEscolaridade(requestDTO.getEscolaridade());
        funcionario.setDataNascimento(requestDTO.getDataNascimento());
        funcionario.setDataContratacao(requestDTO.getDataContratacao());
        funcionario.setDataDemissao(requestDTO.getDataDemissao());

        funcionario.setEndereco(resolveEndereco(funcionario.getEndereco(), requestDTO.getEndereco(), requestDTO.getCepInput()));
    }

    private Endereco resolveEndereco(Endereco atual, EnderecoRequestDTO enderecoDTO, String cepInput) {
        if (enderecoDTO == null) {
            return null;
        }
        Endereco endereco = Optional.ofNullable(atual).orElseGet(Endereco::new);
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setComplemento(enderecoDTO.getComplemento());

        String cepPrincipal = Optional.ofNullable(cepInput).orElse(enderecoDTO.getCep());
        String cepLimpo = cepPrincipal != null ? cepPrincipal.replaceAll("\\D", "") : null;
        if (cepLimpo != null && !cepLimpo.isBlank()) {
            ViaCepResponse response = viaCepClient.buscarEnderecoPorCep(cepLimpo);
            if (response != null && !response.isErro()) {
                endereco.setCep(Optional.ofNullable(response.getCep()).map(c -> c.replace("-", "")).orElse(cepLimpo));
                endereco.setLogradouro(response.getLogradouro());
                endereco.setBairro(response.getBairro());
                endereco.setLocalidade(response.getLocalidade());
                endereco.setUf(response.getUf());
                endereco.setEstado(mapUfToEstado(response.getUf()));
                if (response.getComplemento() != null && !response.getComplemento().isBlank()) {
                    endereco.setComplemento(response.getComplemento());
                }
            } else {
                fillEnderecoFromDto(endereco, enderecoDTO);
            }
        } else {
            fillEnderecoFromDto(endereco, enderecoDTO);
        }
        return endereco;
    }

    private void fillEnderecoFromDto(Endereco endereco, EnderecoRequestDTO enderecoDTO) {
        endereco.setCep(enderecoDTO.getCep());
        endereco.setLogradouro(enderecoDTO.getLogradouro());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setLocalidade(enderecoDTO.getLocalidade());
        endereco.setUf(enderecoDTO.getUf());
        endereco.setEstado(enderecoDTO.getEstado());
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
        responseDTO.setNumero(endereco.getNumero());
        return responseDTO;
    }

    private String mapUfToEstado(String uf) {
        if (uf == null) {
            return null;
        }
        return switch (uf.toUpperCase()) {
            case "AC" -> "Acre";
            case "AL" -> "Alagoas";
            case "AP" -> "Amapá";
            case "AM" -> "Amazonas";
            case "BA" -> "Bahia";
            case "CE" -> "Ceará";
            case "DF" -> "Distrito Federal";
            case "ES" -> "Espírito Santo";
            case "GO" -> "Goiás";
            case "MA" -> "Maranhão";
            case "MT" -> "Mato Grosso";
            case "MS" -> "Mato Grosso do Sul";
            case "MG" -> "Minas Gerais";
            case "PA" -> "Pará";
            case "PB" -> "Paraíba";
            case "PR" -> "Paraná";
            case "PE" -> "Pernambuco";
            case "PI" -> "Piauí";
            case "RJ" -> "Rio de Janeiro";
            case "RN" -> "Rio Grande do Norte";
            case "RS" -> "Rio Grande do Sul";
            case "RO" -> "Rondônia";
            case "RR" -> "Roraima";
            case "SC" -> "Santa Catarina";
            case "SP" -> "São Paulo";
            case "SE" -> "Sergipe";
            case "TO" -> "Tocantins";
            default -> uf;
        };
    }
}
