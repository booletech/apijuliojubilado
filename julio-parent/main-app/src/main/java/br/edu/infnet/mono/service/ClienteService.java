package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.mono.clients.FipeClient;
import br.edu.infnet.mono.clients.ViaCepClient;
import br.edu.infnet.mono.clients.ViaCepClient.ViaCepResponse;
import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Endereco;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.dto.ClienteRequestDTO;
import br.edu.infnet.mono.model.dto.ClienteResponseDTO;
import br.edu.infnet.mono.model.dto.EnderecoRequestDTO;
import br.edu.infnet.mono.model.dto.VeiculoRequestDTO;
import br.edu.infnet.mono.model.dto.FipeModelosResponseDTO;
import br.edu.infnet.mono.model.dto.FipeResponseDTO;
import br.edu.infnet.mono.model.dto.FipeVeiculoDTO;
import br.edu.infnet.mono.repository.ClienteRepository;

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

    @Transactional
    public ClienteResponseDTO incluir(ClienteRequestDTO dto) {
        validar(dto);
        validarCpfDuplicado(dto.getCpf(), null);
        Cliente cliente = aplicarDados(new Cliente(), dto, true);
        Cliente salvo = clienteRepository.save(cliente);
        return new ClienteResponseDTO(salvo);
    }

    @Transactional
    public ClienteResponseDTO alterar(Integer id, ClienteRequestDTO dto) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração não pode ser nulo/zero");
        }
        validar(dto);
        Cliente existente = obterEntidade(id);
        validarCpfDuplicado(dto.getCpf(), id);
        Cliente atualizado = aplicarDados(existente, dto, false);
        atualizado.setId(id);
        Cliente salvo = clienteRepository.save(atualizado);
        return new ClienteResponseDTO(salvo);
    }

    @Transactional
    public void excluir(Integer id) {
        Cliente cliente = obterEntidade(id);
        clienteRepository.delete(cliente);
    }

    @Transactional
    public ClienteResponseDTO alternarFiado(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para alteração de fiado não pode ser nulo/zero");
        }
        Cliente cliente = obterEntidade(id);
        cliente.setPossuiFiado(!cliente.isPossuiFiado());
        Cliente salvo = clienteRepository.save(cliente);
        return new ClienteResponseDTO(salvo);
    }

    public List<ClienteResponseDTO> obterLista() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    public ClienteResponseDTO obterPorId(Integer id) {
        return new ClienteResponseDTO(obterEntidade(id));
    }

    public ClienteResponseDTO obterPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O cliente com CPF " + cpf + " não foi encontrado"));
        return new ClienteResponseDTO(cliente);
    }

    private void validar(ClienteRequestDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O cliente não pode estar nulo e precisa ser criado!");
        }
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do cliente é obrigatório!");
        }
    }

    private Cliente aplicarDados(Cliente cliente, ClienteRequestDTO dto, boolean novoCadastro) {
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setLimiteCredito(dto.getLimiteCredito());
        cliente.setDataNascimento(dto.getDataNascimento());
        if (novoCadastro) {
            cliente.setPossuiFiado(false);
            cliente.setPontosFidelidade(0);
        }
        Endereco endereco = construirEndereco(dto.getEndereco(), cliente.getEndereco());
        cliente.setEndereco(endereco);
        Veiculos veiculo = construirVeiculo(dto.getVeiculo(), cliente.getVeiculo());
        cliente.setVeiculo(veiculo);
        return cliente;
    }

    private Endereco construirEndereco(EnderecoRequestDTO enderecoDto, Endereco atual) {
        if (enderecoDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O endereço é obrigatório.");
        }
        String cep = enderecoDto.getCep();
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
        endereco.setNumero(enderecoDto.getNumero());
        return endereco;
    }

    private Veiculos construirVeiculo(VeiculoRequestDTO veiculoDto, Veiculos atual) {
        if (veiculoDto == null) {
            return null;
        }
        Veiculos veiculo = atual != null ? atual : new Veiculos();
        veiculo.setCodigo(veiculoDto.getCodigo());
        veiculo.setModeloCodigo(veiculoDto.getModeloCodigo());
        veiculo.setAnoCodigo(veiculoDto.getAnoCodigo());
        veiculo.setModelo(veiculoDto.getModelo());
        veiculo.setAnoModelo(veiculoDto.getAnoModelo());
        enriquecerVeiculoComFipe(veiculo);
        return veiculo;
    }

    private void enriquecerVeiculoComFipe(Veiculos veiculo) {
        if (veiculo == null) {
            return;
        }
        String marcaCodigo = veiculo.getCodigo();
        if (marcaCodigo == null || marcaCodigo.isBlank()) {
            return;
        }
        String modeloCodigo = veiculo.getModeloCodigo();
        String anoCodigo = veiculo.getAnoCodigo();
        try {
            if (modeloCodigo != null && !modeloCodigo.isBlank() && anoCodigo != null && !anoCodigo.isBlank()) {
                FipeVeiculoDTO veiculoDTO = fipeClient.getVeiculo(marcaCodigo, modeloCodigo, anoCodigo);
                if (veiculoDTO != null) {
                    veiculo.setFabricante(veiculoDTO.getMarca());
                    veiculo.setModelo(veiculoDTO.getModelo());
                    veiculo.setAnoModelo(veiculoDTO.getAnoModelo());
                }
            } else if (modeloCodigo != null && !modeloCodigo.isBlank()) {
                FipeModelosResponseDTO modelos = fipeClient.getModelos(marcaCodigo);
                if (modelos != null && modelos.getModelos() != null) {
                    Optional<FipeResponseDTO> modeloEncontrado = modelos.getModelos().stream()
                            .filter(modelo -> modelo.getCodigo() != null && modelo.getCodigo().equals(modeloCodigo))
                            .findFirst();
                    modeloEncontrado.ifPresent(modelo -> veiculo.setModelo(modelo.getNome()));
                }
            } else {
                List<FipeResponseDTO> marcas = fipeClient.getMarcas();
                if (marcas != null) {
                    marcas.stream()
                            .filter(marca -> marca.getCodigo() != null && marca.getCodigo().equals(marcaCodigo))
                            .findFirst()
                            .ifPresent(marca -> veiculo.setFabricante(marca.getNome()));
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("Não foi possível enriquecer dados da FIPE: {}", ex.getMessage());
        }
    }

    private Cliente obterEntidade(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ID para consulta não pode ser nulo/zero");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O cliente com id " + id + " não foi encontrado"));
    }

    private void validarCpfDuplicado(String cpf, Integer idAtual) {
        clienteRepository.findByCpf(cpf).ifPresent(cliente -> {
            if (idAtual == null || !cliente.getId().equals(idAtual)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado.");
            }
        });
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
