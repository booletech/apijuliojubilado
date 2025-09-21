package br.edu.infnet.JulioJubiladoapi.model.clients;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.infnet.JulioJubiladoapi.model.domain.LocalidadePedido;

@FeignClient(name = "pedidoApi", url = "${juliopedidoapi.url}")
public interface PedidoFeignClient {

    // ---- VEÍCULOS (FIPE via juliopedidoapi) ----

    // GET /api/veiculos/{vehicleType}/marcas
    @GetMapping("/api/veiculos/{vehicleType}/marcas")
    List<Map<String, Object>> listarMarcas(@PathVariable("vehicleType") String vehicleType);

    // GET /api/veiculos/{vehicleType}/marcas/nome/{brandName}/modelos
    @GetMapping("/api/veiculos/{vehicleType}/marcas/nome/{brandName}/modelos")
    Map<String, Object> listarModelosPorMarcaNome(@PathVariable("vehicleType") String vehicleType,
                                                  @PathVariable("brandName") String brandName);

    // GET /api/veiculos/{vehicleType}/marcas/nome/{brandName}/modelos/nome/{modelName}/anos
    @GetMapping("/api/veiculos/{vehicleType}/marcas/nome/{brandName}/modelos/nome/{modelName}/anos")
    List<Map<String, Object>> listarAnosPorModeloNome(@PathVariable("vehicleType") String vehicleType,
                                                      @PathVariable("brandName") String brandName,
                                                      @PathVariable("modelName") String modelName);

    // ---- LOCALIDADE (ViaCEP via juliopedidoapi) ----

    // GET /api/localidades/{cep}
    @GetMapping("/api/localidades/{cep}")
    LocalidadePedido obterLocalidade(@PathVariable String cep);
}