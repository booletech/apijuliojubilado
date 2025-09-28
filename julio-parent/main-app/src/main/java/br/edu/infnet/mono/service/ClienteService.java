package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.edu.infnet.mono.api.dto.cliente.ClienteRequestDTO;
import br.edu.infnet.mono.api.dto.cliente.ClienteResponseDTO;
import br.edu.infnet.mono.api.dto.cliente.VeiculoRequestDTO;
import br.edu.infnet.mono.api.dto.cliente.VeiculoResponseDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;
import br.edu.infnet.mono.clients.FipeClient;
import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.clients.ViaCepClient.ViaCepResponse;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.dto.FipeMarcaDTO;
import br.edu.infnet.mono.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final ViaCepClient viaCepClient;
    private final FipeClient fipeClient;

    public ClienteService(ClienteRepository clienteRepository, ViaCepClient viaCepClient, FipeClient fipeClient) {
        this.clienteRepository = clienteRepository;
        this.viaCepClient = viaCepClient;
        this.fipeClient = fipeClient;
    }

    public List<ClienteResponseDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO findById(Integer id) {
        Cliente cliente = getClienteOrThrow(id);
        return toResponse(cliente);
    }

    public ClienteResponseDTO create(ClienteRequestDTO requestDTO) {
        Cliente cliente = new Cliente();
        cliente.setId(null);
        applyRequestData(cliente, requestDTO);

        Cliente salvo = clienteRepository.save(cliente);
        return toResponse(salvo);
    }

    public ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO) {
        Cliente existente = getClienteOrThrow(id);
        applyRequestData(existente, requestDTO);

        Cliente atualizado = clienteRepository.save(existente);
        return toResponse(atualizado);
    }

    public void delete(Integer id) {
        Cliente existente = getClienteOrThrow(id);
        clienteRepository.delete(existente);
    }

    private Cliente getClienteOrThrow(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    private void applyRequestData(Cliente cliente, ClienteRequestDTO requestDTO) {
        cliente.setNome(requestDTO.getNome());
        cliente.setCpf(requestDTO.getCpf());
        cliente.setEmail(requestDTO.getEmail());
        cliente.setTelefone(requestDTO.getTelefone());
        cliente.setLimiteCredito(Optional.ofNullable(requestDTO.getLimiteCredito()).orElse(0d));
        cliente.setPossuiFiado(Boolean.TRUE.equals(requestDTO.getPossuiFiado()));
        cliente.setPontosFidelidade(Optional.ofNullable(requestDTO.getPontosFidelidade()).orElse(0));
        cliente.setDataNascimento(requestDTO.getDataNascimento());
        cliente.setDataUltimaVisita(requestDTO.getDataUltimaVisita());

        Endereco endereco = resolveEndereco(cliente.getEndereco(), requestDTO.getCep(), requestDTO.getEndereco());
        cliente.setEndereco(endereco);

        Veiculos veiculo = resolveVeiculo(cliente.getVeiculo(), requestDTO.getVeiculo());
        cliente.setVeiculo(veiculo);
    }

    private Endereco resolveEndereco(Endereco atual, String cepInformado, EnderecoRequestDTO enderecoRequest) {
        Endereco endereco = atual != null ? atual : new Endereco();
        boolean possuiDados = atual != null;

        String cepParaBusca = firstNonBlank(cepInformado, enderecoRequest != null ? enderecoRequest.getCep() : null);
        if (cepParaBusca != null && !cepParaBusca.isBlank()) {
            try {
                ViaCepResponse viaCep = viaCepClient.buscarEnderecoPorCep(normalizarCep(cepParaBusca));
                if (viaCep != null && !viaCep.isErro()) {
                    endereco.setCep(viaCep.getCep());
                    endereco.setLogradouro(viaCep.getLogradouro());
                    endereco.setComplemento(viaCep.getComplemento());
                    endereco.setBairro(viaCep.getBairro());
                    endereco.setLocalidade(viaCep.getLocalidade());
                    endereco.setUf(viaCep.getUf());
                    possuiDados = true;
                }
            } catch (Exception ex) {
                LOGGER.warn("Não foi possível buscar endereço no ViaCEP para o CEP {}: {}", cepParaBusca, ex.getMessage());
            }
        }

        if (enderecoRequest != null) {
            if (notBlank(enderecoRequest.getCep())) {
                endereco.setCep(enderecoRequest.getCep());
                possuiDados = true;
            }
            if (notBlank(enderecoRequest.getLogradouro())) {
                endereco.setLogradouro(enderecoRequest.getLogradouro());
                possuiDados = true;
            }
            if (enderecoRequest.getComplemento() != null) {
                endereco.setComplemento(enderecoRequest.getComplemento());
                possuiDados = true;
            }
            if (notBlank(enderecoRequest.getBairro())) {
                endereco.setBairro(enderecoRequest.getBairro());
                possuiDados = true;
            }
            if (notBlank(enderecoRequest.getLocalidade())) {
                endereco.setLocalidade(enderecoRequest.getLocalidade());
                possuiDados = true;
            }
            if (notBlank(enderecoRequest.getUf())) {
                endereco.setUf(enderecoRequest.getUf());
                possuiDados = true;
            }
            if (enderecoRequest.getEstado() != null) {
                endereco.setEstado(enderecoRequest.getEstado());
                possuiDados = true;
            }
        }

        if (!possuiDados) {
            return null;
        }

        return endereco;
    }

    private Veiculos resolveVeiculo(Veiculos atual, VeiculoRequestDTO veiculoRequest) {
        if (veiculoRequest == null) {
            return atual;
        }

        Veiculos veiculo = atual != null ? atual : new Veiculos();
        boolean possuiDados = atual != null;

        String marcaCodigo = trimToNull(veiculoRequest.getMarcaCodigo());
        String modeloCodigo = trimToNull(veiculoRequest.getModeloCodigo());
        String anoCodigo = trimToNull(veiculoRequest.getAnoCodigo());

        if (marcaCodigo != null) {
            veiculo.setCodigo(marcaCodigo);
            possuiDados = true;
            preencherFabricante(veiculo, marcaCodigo);
        }

        if (modeloCodigo != null) {
            veiculo.setModeloCodigo(modeloCodigo);
            possuiDados = true;
            preencherModeloEAnos(veiculo, marcaCodigo, modeloCodigo, anoCodigo);
        }

        if (anoCodigo != null) {
            veiculo.setAnoCodigo(anoCodigo);
            possuiDados = true;
        }

        if (notBlank(veiculoRequest.getFabricante())) {
            veiculo.setFabricante(veiculoRequest.getFabricante());
            possuiDados = true;
        }
        if (notBlank(veiculoRequest.getModelo())) {
            veiculo.setModelo(veiculoRequest.getModelo());
            possuiDados = true;
        }
        if (notBlank(veiculoRequest.getAnoModelo())) {
            veiculo.setAnoModelo(veiculoRequest.getAnoModelo());
            possuiDados = true;
        }

        if (!possuiDados) {
            return null;
        }

        return veiculo;
    }

    private void preencherFabricante(Veiculos veiculo, String marcaCodigo) {
        try {
            List<FipeMarcaDTO> marcas = fipeClient.getMarcas();
            if (marcas != null) {
                marcas.stream()
                        .filter(m -> Objects.equals(m.getCodigo(), marcaCodigo))
                        .findFirst()
                        .ifPresent(m -> veiculo.setFabricante(m.getNome()));
            }
        } catch (Exception ex) {
            LOGGER.warn("Não foi possível consultar marcas na FIPE: {}", ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void preencherModeloEAnos(Veiculos veiculo, String marcaCodigo, String modeloCodigo, String anoCodigo) {
        if (marcaCodigo == null || modeloCodigo == null) {
            return;
        }

        try {
            Object response = fipeClient.getModelos(marcaCodigo);
            if (!(response instanceof Map<?, ?> modelosMap)) {
                return;
            }

            Object modelosObj = modelosMap.get("modelos");
            if (modelosObj instanceof List<?> modelos) {
                modelos.stream()
                        .filter(Map.class::isInstance)
                        .map(Map.class::cast)
                        .filter(m -> modeloCodigo.equals(String.valueOf(m.get("codigo"))))
                        .map(m -> String.valueOf(m.get("nome")))
                        .findFirst()
                        .ifPresent(veiculo::setModelo);
            }

            if (anoCodigo != null) {
                Object anosObj = modelosMap.get("anos");
                if (anosObj instanceof List<?> anos) {
                    anos.stream()
                            .filter(Map.class::isInstance)
                            .map(Map.class::cast)
                            .filter(a -> anoCodigo.equals(String.valueOf(a.get("codigo"))))
                            .map(a -> String.valueOf(a.get("nome")))
                            .findFirst()
                            .ifPresent(veiculo::setAnoModelo);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("Não foi possível consultar modelos/anos na FIPE: {}", ex.getMessage());
        }
    }

    private ClienteResponseDTO toResponse(Cliente cliente) {
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(cliente.getId());
        responseDTO.setCpf(cliente.getCpf());
        responseDTO.setNome(cliente.getNome());
        responseDTO.setEmail(cliente.getEmail());
        responseDTO.setTelefone(cliente.getTelefone());
        responseDTO.setLimiteCredito(cliente.getLimiteCredito());
        responseDTO.setPossuiFiado(cliente.isPossuiFiado());
        responseDTO.setPontosFidelidade(cliente.getPontosFidelidade());
        responseDTO.setDataNascimento(cliente.getDataNascimento());
        responseDTO.setDataUltimaVisita(cliente.getDataUltimaVisita());
        responseDTO.setEndereco(toEnderecoResponse(cliente.getEndereco()));
        responseDTO.setVeiculo(toVeiculoResponse(cliente.getVeiculo()));
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

    private VeiculoResponseDTO toVeiculoResponse(Veiculos veiculo) {
        if (veiculo == null) {
            return null;
        }

        VeiculoResponseDTO responseDTO = new VeiculoResponseDTO();
        responseDTO.setId(veiculo.getId());
        responseDTO.setFabricante(veiculo.getFabricante());
        responseDTO.setModelo(veiculo.getModelo());
        responseDTO.setAnoModelo(veiculo.getAnoModelo());
        responseDTO.setCodigo(veiculo.getCodigo());
        responseDTO.setModeloCodigo(veiculo.getModeloCodigo());
        responseDTO.setAnoCodigo(veiculo.getAnoCodigo());
        return responseDTO;
    }

    private String normalizarCep(String cep) {
        return cep == null ? null : cep.replaceAll("[^0-9]", "");
    }

    private String firstNonBlank(String primeiro, String segundo) {
        if (notBlank(primeiro)) {
            return primeiro;
        }
        if (notBlank(segundo)) {
            return segundo;
        }
        return null;
    }

    private boolean notBlank(String valor) {
        return valor != null && !valor.isBlank();
    }

    private String trimToNull(String valor) {
        if (valor == null) {
            return null;
        }
        String trimmed = valor.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
