package br.edu.infnet.mono.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.infnet.mono.clients.FipeClient;
import br.edu.infnet.mono.model.domain.Veiculos;
import br.edu.infnet.mono.model.dto.FipeAnoDTO;
import br.edu.infnet.mono.model.dto.FipeMarcaDTO;
import br.edu.infnet.mono.model.dto.FipeModeloDTO;
import br.edu.infnet.mono.model.dto.FipeModelosResponseDTO;
import br.edu.infnet.mono.model.dto.FipeVeiculoDTO;
import br.edu.infnet.mono.repository.VeiculosRepository;

@Service
public class VeiculoService {

    private static final Logger log = LoggerFactory.getLogger(VeiculoService.class);
    
    private final FipeClient fipeClient;
    private final VeiculosRepository veiculosRepository;

    public VeiculoService(FipeClient fipeClient, VeiculosRepository veiculosRepository) {
        this.fipeClient = fipeClient;
        this.veiculosRepository = veiculosRepository;
    }

    @Transactional
    public Optional<Veiculos> buscarOuCriarVeiculo(String fabricante, String modelo, String ano) {
        if (!isInputValido(fabricante, modelo, ano)) {
            log.warn("Dados de veículo inválidos");
            return Optional.empty();
        }

        try {
            Optional<String> codigoMarcaOpt = buscarCodigoMarca(fabricante);
            if (codigoMarcaOpt.isEmpty()) {
                log.warn("Marca não encontrada: {}", fabricante);
                return Optional.empty();
            }

            String codigoMarca = codigoMarcaOpt.get();
            Optional<String> codigoModeloOpt = buscarCodigoModelo(codigoMarca, modelo);
            if (codigoModeloOpt.isEmpty()) {
                log.warn("Modelo não encontrado: {}", modelo);
                return Optional.empty();
            }

            String codigoModelo = codigoModeloOpt.get();
            Optional<String> codigoAnoOpt = buscarCodigoAno(codigoMarca, codigoModelo, ano);
            if (codigoAnoOpt.isEmpty()) {
                log.warn("Ano não encontrado: {}", ano);
                return Optional.empty();
            }

            String codigoAno = codigoAnoOpt.get();
            FipeVeiculoDTO detalhes = fipeClient.obterDetalhes(codigoMarca, codigoModelo, codigoAno);
            
            if (detalhes == null) {
                return Optional.empty();
            }

            return Optional.of(converterESalvar(detalhes));

        } catch (Exception e) {
            log.error("Erro ao buscar veículo na FIPE: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> buscarCodigoMarca(String nomeMarca) {
        try {
            List<FipeMarcaDTO> marcas = fipeClient.obterMarcas();
            return marcas.stream()
                .filter(marca -> marca.getNome().equalsIgnoreCase(nomeMarca))
                .map(FipeMarcaDTO::getCodigo)
                .findFirst();
        } catch (Exception e) {
            log.error("Erro ao buscar marca: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> buscarCodigoModelo(String codigoMarca, String nomeModelo) {
        try {
            FipeModelosResponseDTO response = fipeClient.obterModelos(codigoMarca);
            if (response == null || response.getModelos() == null) {
                return Optional.empty();
            }

            return response.getModelos().stream()
                .filter(modelo -> modelo.getNome().toLowerCase().contains(nomeModelo.toLowerCase()))
                .map(FipeModeloDTO::getCodigo)
                .map(String::valueOf)  
                .findFirst();
        } catch (Exception e) {
            log.error("Erro ao buscar modelo: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> buscarCodigoAno(String codigoMarca, String codigoModelo, String ano) {
        try {
            List<FipeAnoDTO> anos = fipeClient.obterAnos(codigoMarca, codigoModelo);
            return anos.stream()
                .filter(anoDTO -> anoDTO.getNome().startsWith(ano))
                .map(FipeAnoDTO::getCodigo)
                .findFirst();
        } catch (Exception e) {
            log.error("Erro ao buscar ano: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Veiculos converterESalvar(FipeVeiculoDTO dto) {
        Veiculos veiculo = new Veiculos();
        veiculo.setFabricante(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setAnoModelo(String.valueOf(dto.getAnoModelo()));
        veiculo.setCodigo(dto.getCodigoFipe());
        
        Veiculos salvo = veiculosRepository.save(veiculo);
        log.info("Veículo FIPE salvo: {} {} {}", salvo.getFabricante(), salvo.getModelo(), salvo.getAnoModelo());
        return salvo;
    }

    private boolean isInputValido(String fabricante, String modelo, String ano) {
        return !isVazio(fabricante) && !isVazio(modelo) && !isVazio(ano);
    }

    private boolean isVazio(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Transactional(readOnly = true)
    public Veiculos buscarPorId(Integer id) {
        return veiculosRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Veiculos> listarTodos() {
        return veiculosRepository.findAll();
    }
}