package br.edu.infnet.mono.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.clients.FipeClient;
import br.edu.infnet.mono.model.clients.ViaCepClient;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.domain.exceptions.ClienteInvalidoException;
import br.edu.infnet.mono.model.domain.exceptions.ClienteNaoEncontradoException;
import br.edu.infnet.mono.model.dto.FipeModelosResponseDTO;
import br.edu.infnet.mono.model.dto.FipeResponseDTO;
import br.edu.infnet.mono.model.dto.FipeVeiculoDTO;
import br.edu.infnet.mono.model.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService implements CrudService<Cliente, Integer> {

    private final ClienteRepository clienteRepository;
    private final ViaCepClient viaCepClient;
    private final FipeClient fipeClient;

    public ClienteService(ClienteRepository clienteRepository, ViaCepClient viaCepClient, FipeClient fipeClient) {
        this.clienteRepository = clienteRepository;
        this.viaCepClient = viaCepClient;
        this.fipeClient = fipeClient;
    }

    // Validação do cliente
    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("O cliente não pode estar nulo e precisa ser criado!");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ClienteInvalidoException("O nome do cliente é uma informação obrigatória!");
        }
    }

    // Incluir cliente
    @Override
    @Transactional
    public Cliente incluir(Cliente cliente) {
        validar(cliente);
        if (cliente.getId() != null && cliente.getId() > 0) {
            throw new IllegalArgumentException("Um novo cliente não pode ter um Id na inclusão!");
        }

        // Resolve CEP via ViaCepClient se informado
        if (cliente.getEndereco() != null && cliente.getEndereco().getCep() != null) {
            String cepLimpo = cliente.getEndereco().getCep().replace("-", "");
            ViaCepClient.ViaCepResponse response = viaCepClient.buscarEnderecoPorCep(cepLimpo);
            if (response != null && !response.isErro()) {
                cliente.getEndereco().copyFromViaCepResponse(response);
            }
        }

        // Resolve fabricante/modelo/ano via Fipe se códigos forem informados
        if (cliente.getVeiculo() != null && cliente.getVeiculo().getCodigo() != null) {
            String marcaCodigo = cliente.getVeiculo().getCodigo();
            String modeloCodigo = cliente.getVeiculo().getModeloCodigo();
            String anoCodigo = cliente.getVeiculo().getAnoCodigo();

            try {
                if (modeloCodigo != null && anoCodigo != null) {
                    FipeVeiculoDTO veic = fipeClient.getVeiculo(marcaCodigo, modeloCodigo, anoCodigo);
                    if (veic != null) {
                        cliente.getVeiculo().setFabricante(veic.getMarca());
                        cliente.getVeiculo().setModelo(veic.getModelo());
                        cliente.getVeiculo().setAnoModelo(veic.getAnoModelo());
                    }
                } else if (modeloCodigo != null) {
                    FipeModelosResponseDTO modelosResp = fipeClient.getModelos(marcaCodigo);
                    if (modelosResp != null && modelosResp.getModelos() != null) {
                        modelosResp.getModelos().stream()
                                .filter(m -> m.getCodigo() != null && m.getCodigo().equals(modeloCodigo))
                                .findFirst()
                                .ifPresent(m -> cliente.getVeiculo().setModelo(m.getNome()));
                    }
                } else {
                    List<FipeResponseDTO> marcas = fipeClient.getMarcas();
                    Optional<FipeResponseDTO> marca = marcas.stream()
                            .filter(m -> m.getCodigo() != null && m.getCodigo().equals(marcaCodigo))
                            .findFirst();
                    marca.ifPresent(m -> cliente.getVeiculo().setFabricante(m.getNome()));
                }
            } catch (Exception e) {
                System.out.println("Aviso: não foi possível obter dados da FIPE: " + e.getMessage());
            }
        }

        return clienteRepository.save(cliente);
    }

    // Alterar cliente
    @Override
    @Transactional
    public Cliente alterar(Integer id, Cliente cliente) {
        validar(cliente);
        if (id == null || id == 0) {
            throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
        }
        obterPorId(id);
        cliente.setId(id);

        // mesma lógica de resolução quando alterar
        if (cliente.getEndereco() != null && cliente.getEndereco().getCep() != null) {
            String cepLimpo = cliente.getEndereco().getCep().replace("-", "");
            ViaCepClient.ViaCepResponse response = viaCepClient.buscarEnderecoPorCep(cepLimpo);
            if (response != null && !response.isErro()) {
                cliente.getEndereco().copyFromViaCepResponse(response);
            }
        }

        if (cliente.getVeiculo() != null && cliente.getVeiculo().getCodigo() != null) {
            String marcaCodigo = cliente.getVeiculo().getCodigo();
            String modeloCodigo = cliente.getVeiculo().getModeloCodigo();
            String anoCodigo = cliente.getVeiculo().getAnoCodigo();

            try {
                if (modeloCodigo != null && anoCodigo != null) {
                    FipeVeiculoDTO veic = fipeClient.getVeiculo(marcaCodigo, modeloCodigo, anoCodigo);
                    if (veic != null) {
                        cliente.getVeiculo().setFabricante(veic.getMarca());
                        cliente.getVeiculo().setModelo(veic.getModelo());
                        cliente.getVeiculo().setAnoModelo(veic.getAnoModelo());
                    }
                } else if (modeloCodigo != null) {
                    FipeModelosResponseDTO modelosResp = fipeClient.getModelos(marcaCodigo);
                    if (modelosResp != null && modelosResp.getModelos() != null) {
                        modelosResp.getModelos().stream()
                                .filter(m -> m.getCodigo() != null && m.getCodigo().equals(modeloCodigo))
                                .findFirst()
                                .ifPresent(m -> cliente.getVeiculo().setModelo(m.getNome()));
                    }
                } else {
                    List<FipeResponseDTO> marcas = fipeClient.getMarcas();
                    Optional<FipeResponseDTO> marca = marcas.stream()
                            .filter(m -> m.getCodigo() != null && m.getCodigo().equals(marcaCodigo))
                            .findFirst();
                    marca.ifPresent(m -> cliente.getVeiculo().setFabricante(m.getNome()));
                }
            } catch (Exception e) {
                System.out.println("Aviso: não foi possível obter dados da FIPE: " + e.getMessage());
            }
        }

        return clienteRepository.save(cliente);
    }

    // Excluir cliente
    @Transactional
    public void excluir(Integer id) {
        Cliente cliente = obterPorId(id);
        clienteRepository.delete(cliente);
    }

    // Alternar fiado
    @Transactional
    public Cliente alternarFiado(Integer id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("O ID para alteração de fiado não pode ser nulo/zero");
        }

        Cliente cliente = obterPorId(id);
        cliente.setPossuiFiado(!cliente.isPossuiFiado());

        return clienteRepository.save(cliente);
    }

    // Obter lista de clientes
    @Override
    public List<Cliente> obterLista() {
        return clienteRepository.findAll();
    }

    // Obter cliente por ID
    @Override
    @Transactional
    public Cliente obterPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID para CONSULTA não pode ser NULO/ZERO!");
        }

        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("O cliente com id " + id + " não foi encontrado"));
    }

    public Cliente obterPorCpf(String cpf) {

        return clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new ClienteNaoEncontradoException("O Funcionario com o cpf " + cpf + " não foi encontrado"));

    }

}