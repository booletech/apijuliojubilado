package br.edu.infnet.mono.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ViaCepClient viaCepClient;

    public ClienteService(ClienteRepository clienteRepository, ViaCepClient viaCepClient) {
        this.clienteRepository = clienteRepository;
        this.viaCepClient = viaCepClient;
    }

    private Endereco copyFromViaCepResponse(ViaCepClient.ViaCepResponse response, Integer numero, String complemento) {
        Endereco endereco = new Endereco();
        if (response == null) return endereco;
        endereco.setCep(response.getCep());
        endereco.setLogradouro(response.getLogradouro());
        endereco.setComplemento(response.getComplemento());
        endereco.setBairro(response.getBairro());
        endereco.setLocalidade(response.getLocalidade());
        endereco.setUf(response.getUf());
        endereco.setNumero(numero != null ? numero : 0);
        if (complemento != null && !complemento.trim().isEmpty()) {
            endereco.setComplemento(complemento);
        }
        return endereco;
    }

    private String somenteNumeros(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cliente não pode ser nulo.");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do cliente é obrigatório.");
        }
        if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CPF do cliente é obrigatório.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail do cliente é obrigatório.");
        }
    }

    @Transactional
    public Cliente incluir(Cliente cliente) {
        validar(cliente);

        clienteRepository.findByCpf(cliente.getCpf()).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
        });

        // Resolve endereço via CEP se informado
        if (cliente.getEndereco() != null && cliente.getEndereco().getCep() != null) {
            String cepLimpo = somenteNumeros(cliente.getEndereco().getCep());
            ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepLimpo);
            if (viaCepResponse == null || viaCepResponse.isErro()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido ou não encontrado: " + cliente.getEndereco().getCep());
            }
            // Preserve número e complemento informados
            Integer numero = cliente.getEndereco().getNumero();
            String complemento = cliente.getEndereco().getComplemento();
            cliente.setEndereco(copyFromViaCepResponse(viaCepResponse, numero, complemento));
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente alterar(Integer id, Cliente clienteAtualizado) {
        if (id == null || id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração não pode ser nulo/zero");
        }

        validar(clienteAtualizado);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));

        // CPF
        if (clienteAtualizado.getCpf() != null && !clienteAtualizado.getCpf().equals(clienteExistente.getCpf())) {
            clienteRepository.findByCpf(clienteAtualizado.getCpf()).ifPresent(c -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Novo CPF já cadastrado para outro cliente.");
            });
            clienteExistente.setCpf(clienteAtualizado.getCpf());
        }

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setLimiteCredito(clienteAtualizado.getLimiteCredito());
        clienteExistente.setPossuiFiado(clienteAtualizado.isPossuiFiado());
        clienteExistente.setPontosFidelidade(clienteAtualizado.getPontosFidelidade());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setDataUltimaVisita(clienteAtualizado.getDataUltimaVisita());

        String cepAntigo = clienteExistente.getEndereco() != null ? somenteNumeros(clienteExistente.getEndereco().getCep()) : null;
        String cepNovoInformado = clienteAtualizado.getEndereco() != null ? somenteNumeros(clienteAtualizado.getEndereco().getCep()) : null;

        if (cepNovoInformado != null && !cepNovoInformado.equals(cepAntigo)) {
            ViaCepClient.ViaCepResponse viaCepResponse = viaCepClient.buscarEnderecoPorCep(cepNovoInformado);
            if (viaCepResponse == null || viaCepResponse.isErro()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido ou não encontrado.");
            }
            clienteExistente.setEndereco(copyFromViaCepResponse(viaCepResponse,
                    clienteAtualizado.getEndereco().getNumero(),
                    clienteAtualizado.getEndereco().getComplemento()));
        } else if (clienteAtualizado.getEndereco() != null && cepNovoInformado == null && clienteExistente.getEndereco() != null) {
            clienteExistente.getEndereco().setNumero(clienteAtualizado.getEndereco().getNumero());
            clienteExistente.getEndereco().setComplemento(clienteAtualizado.getEndereco().getComplemento());
        }

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void excluir(Integer id) {
        Cliente cliente = obterPorId(id);
        clienteRepository.delete(cliente);
    }

    @Transactional
    public Cliente alternarFiado(Integer id) {
        if (id == null || id == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração de fiado não pode ser nulo/zero");
        }
        Cliente cliente = obterPorId(id);
        cliente.setPossuiFiado(!cliente.isPossuiFiado());
        return clienteRepository.save(cliente);
    }

    public List<Cliente> obterLista() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente obterPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para CONSULTA não pode ser NULO/ZERO!");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O cliente com id " + id + " não foi encontrado"));
    }

    public Cliente obterPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O cliente com o cpf " + cpf + " não foi encontrado"));
    }
}