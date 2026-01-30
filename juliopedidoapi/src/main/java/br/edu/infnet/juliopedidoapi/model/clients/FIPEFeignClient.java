package br.edu.infnet.juliopedidoapi.model.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoAno;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoModelo;
import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;

@FeignClient(name = "fipe", url = "https://fipe.parallelum.com.br/api/v2")
public interface FIPEFeignClient {

    // Modelos por marca
    @GetMapping("/{vehicleType}/brands/{brandId}/models")
    List<CarrosRetornoModelo> obterModelosPorMarca(
            @PathVariable("vehicleType") String vehicleType,
            @PathVariable("brandId") String brandId
    );

    // (Opcional) Modelos filtrados por marca + ano
    @GetMapping("/{vehicleType}/brands/{brandId}/years/{yearId}/models")
    List<CarrosRetornoModelo> obterModelosPorMarcaEAno(
            @PathVariable("vehicleType") String vehicleType,
            @PathVariable("brandId") String brandId,
            @PathVariable("yearId") String yearId
    );

    // Lista os anos disponíveis para uma marca (não filtra por modelo)
    @GetMapping("/{vehicleType}/brands/{brandId}/years")
    List<CarrosRetornoAno> listarAnosPorMarca(
            @PathVariable("vehicleType") String vehicleType,
            @PathVariable("brandId") String brandId
    );

    // Anos por marca + modelo (fluxo recomendado)
    @GetMapping("/{vehicleType}/brands/{brandId}/models/{modelId}/years")
    List<CarrosRetornoQueryResult> obterAnosPorMarcaEModelo(
            @PathVariable("vehicleType") String vehicleType,
            @PathVariable("brandId") String brandId,
            @PathVariable("modelId") String modelId
    );
    
    @GetMapping("/{vehicleType}/brands")
    List<CarrosRetornoQueryResult> listarMarcas(
            @PathVariable String vehicleType
    );
}
