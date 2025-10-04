package br.edu.infnet.juliopedidoapi.model.service;

import java.util.List;
import java.util.Objects;
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
        if (marcas == null || marcas.isEmpty()) return null;

        CarrosRetornoQueryResult match = marcas.stream()
                .filter(b -> b.getName() != null && brandName.equalsIgnoreCase(b.getName()))
                .findFirst()
                .orElseGet(() -> marcas.stream()
                        .filter(b -> b.getName() != null && b.getName().toLowerCase().contains(brandName.toLowerCase()))
                        .findFirst()
                        .orElse(marcas.get(0)));

        return match != null ? match.getCode() : null;
    }

    public String buscarModelIdPorNome(String vehicleType, String brandId, String modelName) {
        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        validarParametro(brandId, "ID do fabricante não pode ser nulo ou vazio");
        validarParametro(modelName, "Nome do modelo não pode ser nulo ou vazio");

        return fipeFeignClient.obterModelosPorMarca(vehicleType, brandId).stream()
                .filter(model -> modelName.equalsIgnoreCase(model.getName()))
                .map(CarrosRetornoModelo::getCode)
                .findFirst()
                .orElseGet(() -> {
                    // fallback: contains
                    return fipeFeignClient.obterModelosPorMarca(vehicleType, brandId).stream()
                            .filter(m -> m.getName() != null && m.getName().toLowerCase().contains(modelName.toLowerCase()))
                            .map(CarrosRetornoModelo::getCode)
                            .findFirst()
                            .orElse(null);
                });
    }

    public CarrosRetornoQueryResult obterModeloPorFabricante(String vehicleType, String brandName) {
        String brandId = buscarBrandIdPorNome(vehicleType, brandName);
        CarrosRetornoQueryResult retorno = new CarrosRetornoQueryResult();
        if (brandId == null) {
            retorno.setListadeveiculos(List.of());
            return retorno;
        }
        List<CarrosRetornoModelo> modelos = fipeFeignClient.obterModelosPorMarca(vehicleType, brandId);

        if (modelos != null && !modelos.isEmpty()) {
            List<String> nomes = modelos.stream()
                    .map(CarrosRetornoModelo::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            retorno.setListadeveiculos(nomes.isEmpty() ? List.of() : nomes);
        } else {
            retorno.setListadeveiculos(List.of());
        }
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
        return fipeFeignClient.listarMarcas(vehicleType).stream()
                .map(brand -> {
                    CarrosRetornoQueryResult result = new CarrosRetornoQueryResult();
                    result.setName(brand.getName());
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<CarrosRetornoQueryResult> obterAnosPorModeloNome(String vehicleType, String brandId, String modelName) {
        List<CarrosRetornoModelo> modelos = fipeFeignClient.obterModelosPorMarca(vehicleType, brandId);
        if (modelos == null) return List.of();
        String modelId = modelos.stream()
                .filter(model -> modelName.equalsIgnoreCase(model.getName()))
                .map(CarrosRetornoModelo::getCode)
                .findFirst()
                .orElse(null);
        if (modelId == null) return List.of();
        return fipeFeignClient.obterAnosPorMarcaEModelo(vehicleType, brandId, modelId);
    }

    private void validarParametro(String parametro, String mensagemErro) {
        if (!StringUtils.hasText(parametro)) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }
}