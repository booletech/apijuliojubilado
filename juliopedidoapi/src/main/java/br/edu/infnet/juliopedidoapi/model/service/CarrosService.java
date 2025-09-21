package br.edu.infnet.juliopedidoapi.model.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.edu.infnet.juliopedidoapi.model.clients.FIPEFeignClient;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoModelo;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;

@Service
public class CarrosService {

    private final FIPEFeignClient fipeFeignClient;

    public CarrosService(FIPEFeignClient fipeFeignClient) {
        this.fipeFeignClient = fipeFeignClient;
    }

    public String buscarBrandIdPorNome(String vehicleType, String brandName) {
        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        validarParametro(brandName, "Nome do fabricante não pode ser nulo ou vazio");

        List<CarrosRetornoQueryResult> marcas = fipeFeignClient.listarMarcas(vehicleType);
        CarrosRetornoQueryResult match = encontrarPorNome(marcas, brandName, CarrosRetornoQueryResult::getName);
        return match != null ? match.getCode() : null;
    }

    public String buscarModelIdPorNome(String vehicleType, String brandId, String modelName) {
        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        validarParametro(brandId, "ID do fabricante não pode ser nulo ou vazio");
        validarParametro(modelName, "Nome do modelo não pode ser nulo ou vazio");

        List<CarrosRetornoModelo> modelos = fipeFeignClient.obterModelosPorMarca(vehicleType, brandId);
        CarrosRetornoModelo match = encontrarPorNome(modelos, modelName, CarrosRetornoModelo::getName);
        return match != null ? match.getCode() : null;
    }

    public CarrosRetornoQueryResult obterModeloPorFabricante(String vehicleType, String brandName) {
        String brandId = buscarBrandIdPorNome(vehicleType, brandName);
        CarrosRetornoQueryResult retorno = new CarrosRetornoQueryResult();
        if (brandId == null) {
            retorno.setListadeveiculos(List.of());
            return retorno;
        }
        List<CarrosRetornoModelo> modelos = fipeFeignClient.obterModelosPorMarca(vehicleType, brandId);

        retorno.setListadeveiculos(extrairNomesDosModelos(modelos));
        return retorno;
    }

    public List<CarrosRetornoQueryResult> obterAnosPorModelo(String vehicleType, String brandName, String modelName) {
        String brandId = buscarBrandIdPorNome(vehicleType, brandName);
        if (brandId == null) return List.of();
        String modelId = buscarModelIdPorNome(vehicleType, brandId, modelName);
        if (modelId == null) return List.of();
        return fipeFeignClient.obterAnosPorMarcaEModelo(vehicleType, brandId, modelId);
    }

    public List<CarrosRetornoQueryResult> listarFabricantes(String vehicleType) {
        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        List<CarrosRetornoQueryResult> marcas = fipeFeignClient.listarMarcas(vehicleType);
        if (marcas == null) {
            return List.of();
        }
        return marcas.stream()
                .filter(Objects::nonNull)
                .map(this::copiarMarca)
                .collect(Collectors.toList());
    }

    public List<CarrosRetornoQueryResult> obterAnosPorModeloNome(String vehicleType, String brandId, String modelName) {
        List<CarrosRetornoModelo> modelos = fipeFeignClient.obterModelosPorMarca(vehicleType, brandId);
        CarrosRetornoModelo match = encontrarPorNome(modelos, modelName, CarrosRetornoModelo::getName);
        if (match == null) return List.of();
        return fipeFeignClient.obterAnosPorMarcaEModelo(vehicleType, brandId, match.getCode());
    }

    private void validarParametro(String parametro, String mensagemErro) {
        if (!StringUtils.hasText(parametro)) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private <T> T encontrarPorNome(List<T> itens, String nomeBuscado, Function<T, String> extratorNome) {
        if (itens == null || itens.isEmpty()) {
            return null;
        }

        String nomeNormalizado = nomeBuscado.toLowerCase(Locale.ROOT);

        return itens.stream()
                .filter(item -> nomesIguais(extratorNome.apply(item), nomeBuscado))
                .findFirst()
                .orElseGet(() -> itens.stream()
                        .filter(item -> contemTrecho(extratorNome.apply(item), nomeNormalizado))
                        .findFirst()
                        .orElse(null));
    }

    private List<String> extrairNomesDosModelos(List<CarrosRetornoModelo> modelos) {
        if (modelos == null || modelos.isEmpty()) {
            return List.of();
        }

        List<String> nomes = modelos.stream()
                .map(CarrosRetornoModelo::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return nomes.isEmpty() ? List.of() : nomes;
    }

    private CarrosRetornoQueryResult copiarMarca(CarrosRetornoQueryResult marca) {
        if (marca == null) {
            return null;
        }

        CarrosRetornoQueryResult copia = new CarrosRetornoQueryResult();
        copia.setCode(marca.getCode());
        copia.setName(marca.getName());
        copia.setAno(marca.getAno());
        copia.setListadeveiculos(marca.getListadeveiculos());
        return copia;
    }

    private boolean nomesIguais(String valor, String esperado) {
        return valor != null && valor.equalsIgnoreCase(esperado);
    }

    private boolean contemTrecho(String valor, String trechoNormalizado) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(trechoNormalizado);
    }
}