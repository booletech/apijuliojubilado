package br.edu.infnet.mono.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.model.dto.FuncionarioRequestDTO;
import br.edu.infnet.mono.model.dto.FuncionarioResponseDTO;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ViaCepClient viaCepClient;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, ViaCepClient viaCepClient) {
        this.funcionarioRepository = funcionarioRepository;
        this.viaCepClient = viaCepClient;
    }

    private Endereco copyFromViaCepResponse(ViaCepClient.ViaCepResponse response, Integer numero, String complemento) {
        Endereco endereco = new Endereco();
        endereco.setCep(response.getCep());
        endereco.setLogradouro(response.getLogradouro());
        endereco.setComplemento(response.getComplemento());
        endereco.setBairro(response.getBairro());
        endereco.setLocalidade(response.getLocalidade());
        endereco.setUf(response.getUf());
        endereco.setComplemento(response.getComplemento());
        endereco.setNumero(numero);
        return endereco;
    }

    private String somenteNumeros(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

    public FuncionarioResponseDTO incluir(FuncionarioRequestDTO funcionarioRequestDto) {
        funcionarioRepository.findByCpf(funcionarioRequestDto.getCpf()).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
        });
        funcionarioRepository.findByMatricula(funcionarioRequestDto.getMatricula()).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada.");
        });

        String cepLimpo = somenteNumeros(funcionarioRequestDto.getCep());
        ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepLimpo);

        if (viaCepResponse == null || viaCepResponse.isErro()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CEP inválido ou não encontrado: " + funcionarioRequestDto.getCep());
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(funcionarioRequestDto.getNome());
        funcionario.setCpf(funcionarioRequestDto.getCpf());
        funcionario.setEmail(funcionarioRequestDto.getEmail());
        funcionario.setTelefone(funcionarioRequestDto.getTelefone());
        funcionario.setMatricula(funcionarioRequestDto.getMatricula());
        funcionario.setSalario(funcionarioRequestDto.getSalario());
        funcionario.setCargo(funcionarioRequestDto.getCargo());
        funcionario.setTurno(funcionarioRequestDto.getTurno());
        funcionario.setEscolaridade(funcionarioRequestDto.getEscolaridade());
        funcionario.setDataNascimento(funcionarioRequestDto.getDataNascimento());
        funcionario.setDataContratacao(funcionarioRequestDto.getDataContratacao());
        funcionario.setDataDemissao(funcionarioRequestDto.getDataDemissao());
        funcionario.setAtivo(funcionarioRequestDto.isAtivo());
        funcionario.setEndereco(copyFromViaCepResponse(viaCepResponse, funcionarioRequestDto.getNumero(), funcionarioRequestDto.getComplemento()));

        
        validar(funcionario);
        
        Funcionario novoFuncionario = funcionarioRepository.save(funcionario);
        return new FuncionarioResponseDTO(novoFuncionario);
    }

   
    @Transactional
    public Funcionario alterar(Integer id, Funcionario funcionarioAtualizado) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
        }
        
        validar(funcionarioAtualizado);

        Funcionario funcionarioExistente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));

        // CPF
        if (funcionarioAtualizado.getCpf() != null
                && !funcionarioAtualizado.getCpf().equals(funcionarioExistente.getCpf())) {
            funcionarioRepository.findByCpf(funcionarioAtualizado.getCpf()).ifPresent(f -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Novo CPF já cadastrado para outro funcionário.");
            });
            funcionarioExistente.setCpf(funcionarioAtualizado.getCpf());
        }

        if (funcionarioAtualizado.getMatricula() != null
                && !funcionarioAtualizado.getMatricula().equals(funcionarioExistente.getMatricula())) {
            funcionarioRepository.findByMatricula(funcionarioAtualizado.getMatricula()).ifPresent(f -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Nova matrícula já cadastrada para outro funcionário.");
            });

            funcionarioExistente.setMatricula(funcionarioAtualizado.getMatricula());
        }

        funcionarioExistente.setNome(funcionarioAtualizado.getNome());
        funcionarioExistente.setEmail(funcionarioAtualizado.getEmail());
        funcionarioExistente.setTelefone(funcionarioAtualizado.getTelefone());
        funcionarioExistente.setSalario(funcionarioAtualizado.getSalario());
        funcionarioExistente.setCargo(funcionarioAtualizado.getCargo());
        funcionarioExistente.setTurno(funcionarioAtualizado.getTurno());
        funcionarioExistente.setEscolaridade(funcionarioAtualizado.getEscolaridade());
        funcionarioExistente.setDataNascimento(funcionarioAtualizado.getDataNascimento());
        funcionarioExistente.setDataContratacao(funcionarioAtualizado.getDataContratacao());
        funcionarioExistente.setDataDemissao(funcionarioAtualizado.getDataDemissao());
        if (funcionarioAtualizado.isAtivo() != funcionarioExistente.isAtivo()) {
            funcionarioExistente.setAtivo(funcionarioAtualizado.isAtivo());
        }

        
        String cepAntigo = funcionarioExistente.getEndereco() != null
                ? somenteNumeros(funcionarioExistente.getEndereco().getCep())
                : null;

        String cepNovoInformado = funcionarioAtualizado.getEndereco() != null
                ? somenteNumeros(funcionarioAtualizado.getEndereco().getCep())
                : null;

        if (cepNovoInformado != null && !cepNovoInformado.equals(cepAntigo)) {
            ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepNovoInformado);
            if (viaCepResponse == null || viaCepResponse.isErro()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido ou não encontrado.");
            }
            funcionarioExistente.setEndereco(copyFromViaCepResponse(viaCepResponse, 
					funcionarioAtualizado.getEndereco().getNumero(),
					funcionarioAtualizado.getEndereco().getComplemento()));
        }

        return funcionarioRepository.save(funcionarioExistente);
    }

    @Transactional
    public void excluir(Integer id) {
        Funcionario funcionario = obterPorId(id);
        funcionarioRepository.delete(funcionario);
    }

    @Transactional
    public Funcionario inativar(Integer id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("O ID para inativação não pode ser nulo/zero");
        }
        Funcionario funcionario = obterPorId(id);
        if (!funcionario.isAtivo()) {
            System.out.println("Funcionario " + funcionario.getNome() + " já está inativo!");
            return funcionario;
        }
        funcionario.setAtivo(false);
        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> obterLista() {
        return funcionarioRepository.findAll();
    }

    @Transactional
    public Funcionario obterPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID para CONSULTA não pode ser NULO/ZERO!");
        }
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O funcionario com id " + id + " não foi encontrado"));
    }

    public Funcionario obterPorCpf(String cpf) {
        return funcionarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O Funcionario com o cpf " + cpf + " não foi encontrado"));
    }

    private void validar(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário não pode ser nulo.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do funcionário é obrigatório.");
        }
        if (funcionario.getCpf() == null || funcionario.getCpf().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF do funcionário é obrigatório.");
        }
        if (funcionario.getMatricula() == null || funcionario.getMatricula().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A matrícula do funcionário é obrigatória.");
        }
        if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail do funcionário é obrigatório.");
        }
    }
}
