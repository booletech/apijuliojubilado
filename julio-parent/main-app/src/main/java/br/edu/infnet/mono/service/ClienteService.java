package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.api.dto.cliente.ClienteRequestDTO;
import br.edu.infnet.mono.api.dto.cliente.ClienteResponseDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoRequestDTO;
import br.edu.infnet.mono.api.dto.endereco.EnderecoResponseDTO;
import br.edu.infnet.mono.api.dto.veiculo.VeiculoRequestDTO;
import br.edu.infnet.mono.api.dto.veiculo.VeiculoResponseDTO;
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
                .toList();
    }

    public ClienteResponseDTO findById(Integer id) {
        Cliente cliente = findEntityById(id);
        return toResponse(cliente);
    }

    public ClienteResponseDTO create(ClienteRequestDTO requestDTO) {
        Cliente cliente = new Cliente();
        applyRequest(cliente, requestDTO);
        cliente.setId(null);
        Cliente salvo = clienteRepository.save(cliente);
        return toResponse(salvo);
    }

    public ClienteResponseDTO update(Integer id, ClienteRequestDTO requestDTO) {
        Cliente existente = findEntityById(id);
        applyRequest(existente, requestDTO);
        Cliente salvo = clienteRepository.save(existente);
        return toResponse(salvo);
    }

    public void delete(Integer id) {
        Cliente existente = findEntityById(id);
        clienteRepository.delete(existente);
    }

    private Cliente findEntityById(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    private void applyRequest(Cliente cliente, ClienteRequestDTO requestDTO) {
        cliente.setCpf(requestDTO.getCpf());
        cliente.setNome(requestDTO.getNome());
        cliente.setEmail(requestDTO.getEmail());
        cliente.setTelefone(requestDTO.getTelefone());
        if (requestDTO.getLimiteCredito() != null) {
            cliente.setLimiteCredito(requestDTO.getLimiteCredito());
        }
        if (requestDTO.getPossuiFiado() != null) {
            cliente.setPossuiFiado(requestDTO.getPossuiFiado());
        }
        if (requestDTO.getPontosFidelidade() != null) {
            cliente.setPontosFidelidade(requestDTO.getPontosFidelidade());
        }
        cliente.setDataNascimento(requestDTO.getDataNascimento());
        cliente.setDataUltimaVisita(requestDTO.getDataUltimaVisita());

        cliente.setEndereco(resolveEndereco(cliente.getEndereco(), requestDTO.getEndereco()));
        cliente.setVeiculo(resolveVeiculo(cliente.getVeiculo(), requestDTO.getVeiculo()));
    }

    private Endereco resolveEndereco(Endereco atual, EnderecoRequestDTO enderecoDTO) {
        if (enderecoDTO == null) {
            return null;
        }

        Endereco endereco = Optional.ofNullable(atual).orElseGet(Endereco::new);
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setComplemento(enderecoDTO.getComplemento());

        String cep = Optional.ofNullable(enderecoDTO.getCep()).map(c -> c.replaceAll("\\D", "")).orElse(null);
        if (cep != null && !cep.isBlank()) {
            ViaCepResponse response = viaCepClient.buscarEnderecoPorCep(cep);
            if (response != null && !response.isErro()) {
                endereco.setCep(Optional.ofNullable(response.getCep()).map(c -> c.replace("-", "")).orElse(cep));
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

    private Veiculos resolveVeiculo(Veiculos atual, VeiculoRequestDTO veiculoDTO) {
        if (veiculoDTO == null) {
            return null;
        }
        Veiculos veiculo = Optional.ofNullable(atual).orElseGet(Veiculos::new);
        veiculo.setCodigo(veiculoDTO.getCodigo());
        veiculo.setModeloCodigo(veiculoDTO.getModeloCodigo());
        veiculo.setAnoCodigo(veiculoDTO.getAnoCodigo());
        if (veiculoDTO.getModelo() != null) {
            veiculo.setModelo(veiculoDTO.getModelo());
        }
        if (veiculoDTO.getAnoModelo() != null) {
            veiculo.setAnoModelo(veiculoDTO.getAnoModelo());
        }
        enrichVeiculo(veiculo);
        return veiculo;
    }

    private void enrichVeiculo(Veiculos veiculo) {
        if (veiculo == null) {
            return;
        }
        String codigoMarca = veiculo.getCodigo();
        if (codigoMarca == null || codigoMarca.isBlank()) {
            return;
        }

        List<FipeMarcaDTO> marcas = fipeClient.getMarcas();
        marcas.stream()
                .filter(m -> m.getCodigo() != null && m.getCodigo().equalsIgnoreCase(codigoMarca))
                .findFirst()
                .ifPresent(m -> veiculo.setFabricante(m.getNome()));

        if (veiculo.getModeloCodigo() == null || veiculo.getModeloCodigo().isBlank()) {
            return;
        }

        Object modelosResponse = fipeClient.getModelos(codigoMarca);
        if (!(modelosResponse instanceof Map<?, ?> modelosMap)) {
            return;
        }
        Object modelos = modelosMap.get("modelos");
        if (modelos instanceof List<?> listaModelos) {
            listaModelos.stream()
                    .filter(item -> item instanceof Map<?, ?>)
                    .map(item -> (Map<?, ?>) item)
                    .filter(item -> veiculo.getModeloCodigo().equals(String.valueOf(item.get("codigo"))))
                    .map(item -> String.valueOf(item.get("nome")))
                    .findFirst()
                    .ifPresent(veiculo::setModelo);
        }
        if (veiculo.getAnoCodigo() == null || veiculo.getAnoCodigo().isBlank()) {
            return;
        }
        Object anos = modelosMap.get("anos");
        if (anos instanceof List<?> listaAnos) {
            listaAnos.stream()
                    .filter(item -> item instanceof Map<?, ?>)
                    .map(item -> (Map<?, ?>) item)
                    .filter(item -> veiculo.getAnoCodigo().equals(String.valueOf(item.get("codigo"))))
                    .map(item -> String.valueOf(item.get("nome")))
                    .findFirst()
                    .ifPresent(veiculo::setAnoModelo);
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
        responseDTO.setNumero(endereco.getNumero());
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
