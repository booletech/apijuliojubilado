package br.edu.infnet.mono.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.clients.ViaCepClient.ViaCepResponse;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.model.dto.FuncionarioRequestDTO;
import br.edu.infnet.mono.model.dto.FuncionarioResponseDTO;
import br.edu.infnet.mono.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ViaCepClient viaCepClient;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, ViaCepClient viaCepClient) {
        this.funcionarioRepository = funcionarioRepository;
        this.viaCepClient = viaCepClient;
    }

    @Transactional
    public FuncionarioResponseDTO incluir(FuncionarioRequestDTO dto) {
        validarDuplicidade(dto.getCpf(), dto.getMatricula(), null);
        Funcionario funcionario = mapearFuncionario(new Funcionario(), dto);
        Funcionario salvo = funcionarioRepository.save(funcionario);
        return new FuncionarioResponseDTO(salvo);
    }

    @Transactional
    public FuncionarioResponseDTO alterar(Integer id, FuncionarioRequestDTO dto) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração não pode ser nulo/zero");
        }
        Funcionario existente = obterEntidadePorId(id);
        validarDuplicidade(dto.getCpf(), dto.getMatricula(), id);
        Funcionario atualizado = mapearFuncionario(existente, dto);
        atualizado.setId(id);
        Funcionario salvo = funcionarioRepository.save(atualizado);
        return new FuncionarioResponseDTO(salvo);
    }

    @Transactional
    public void excluir(Integer id) {
        Funcionario funcionario = obterEntidadePorId(id);
        funcionarioRepository.delete(funcionario);
    }

    @Transactional
    public FuncionarioResponseDTO inativar(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para inativação não pode ser nulo/zero");
        }
        Funcionario funcionario = obterEntidadePorId(id);
        if (!funcionario.isAtivo()) {
            return new FuncionarioResponseDTO(funcionario);
        }
        funcionario.setAtivo(false);
        Funcionario salvo = funcionarioRepository.save(funcionario);
        return new FuncionarioResponseDTO(salvo);
    }

    public List<FuncionarioResponseDTO> obterLista() {
        return funcionarioRepository.findAll().stream()
                .map(FuncionarioResponseDTO::new)
                .toList();
    }

    public FuncionarioResponseDTO obterPorId(Integer id) {
        return new FuncionarioResponseDTO(obterEntidadePorId(id));
    }

    public FuncionarioResponseDTO obterPorCpf(String cpf) {
        Funcionario funcionario = funcionarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O funcionário com CPF " + cpf + " não foi encontrado"));
        return new FuncionarioResponseDTO(funcionario);
    }

    private Funcionario obterEntidadePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para consulta não pode ser nulo/zero");
        }
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O funcionário com id " + id + " não foi encontrado"));
    }

    private void validarDuplicidade(String cpf, String matricula, Integer idAtual) {
        funcionarioRepository.findByCpf(cpf).ifPresent(func -> {
            if (idAtual == null || !func.getId().equals(idAtual)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
            }
        });
        funcionarioRepository.findByMatricula(matricula).ifPresent(func -> {
            if (idAtual == null || !func.getId().equals(idAtual)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada.");
            }
        });
    }

    private Funcionario mapearFuncionario(Funcionario funcionario, FuncionarioRequestDTO dto) {
        funcionario.setNome(dto.getNome());
        funcionario.setCpf(dto.getCpf());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setMatricula(dto.getMatricula());
        funcionario.setSalario(dto.getSalario());
        funcionario.setCargo(dto.getCargo());
        funcionario.setTurno(dto.getTurno());
        funcionario.setEscolaridade(dto.getEscolaridade());
        funcionario.setDataNascimento(dto.getDataNascimento());
        funcionario.setDataContratacao(dto.getDataContratacao());
        funcionario.setDataDemissao(dto.getDataDemissao());
        funcionario.setCepInput(dto.getCep());
        funcionario.setEndereco(resolverEndereco(dto.getCep(), null, funcionario.getEndereco()));
        return funcionario;
    }

    private Endereco resolverEndereco(String cep, String numero, Endereco atual) {
        if (cep == null || cep.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CEP é obrigatório.");
        }
        String cepLimpo = cep.replace("-", "");
        ViaCepResponse resposta = viaCepClient.buscarEnderecoPorCep(cepLimpo);
        if (resposta == null || resposta.isErro()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido ou não encontrado: " + cep);
        }
        Endereco endereco = atual != null ? atual : new Endereco();
        endereco.setCep(resposta.getCep() != null ? resposta.getCep().replace("-", "") : cepLimpo);
        endereco.setLogradouro(resposta.getLogradouro());
        endereco.setComplemento(resposta.getComplemento());
        endereco.setBairro(resposta.getBairro());
        endereco.setLocalidade(resposta.getLocalidade());
        endereco.setUf(resposta.getUf());
        endereco.setEstado(mapUfParaEstado(resposta.getUf()));
        endereco.setNumero(numero);
        return endereco;
    }

    private String mapUfParaEstado(String uf) {
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
