package br.edu.infnet.JulioJubiladoapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.LocalidadePedido;
import br.edu.infnet.JulioJubiladoapi.model.service.PedidoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<LocalidadePedido> obterLocalidade(@PathVariable String cep) {
        LocalidadePedido localidadepedido = pedidoService.obterLocalidade(cep);
        return ResponseEntity.ok(localidadepedido);
    }

    @GetMapping("/fabricantes/{tipoVeiculo}")
    public ResponseEntity<List<Map<String, Object>>> listarFabricantes(@PathVariable String tipoVeiculo) {
        List<Map<String, Object>> fabricantes = pedidoService.listarFabricantes(tipoVeiculo);
        return ResponseEntity.ok(fabricantes);
    }

    @GetMapping("/modelos/{tipoVeiculo}/{nomeFabricante}")
    public ResponseEntity<Map<String, Object>> listarModelosPorFabricante(@PathVariable String tipoVeiculo, @PathVariable String nomeFabricante) {
        Map<String, Object> modelos = pedidoService.listarModelosPorFabricante(tipoVeiculo, nomeFabricante);
        return ResponseEntity.ok(modelos);
    }

    @GetMapping("/anos/{tipoVeiculo}/{nomeFabricante}/{nomeModelo}")
    public ResponseEntity<List<Map<String, Object>>> listarAnosPorModelo(@PathVariable String tipoVeiculo, @PathVariable String nomeFabricante, @PathVariable String nomeModelo) {
        List<Map<String, Object>> anos = pedidoService.listarAnosPorModelo(tipoVeiculo, nomeFabricante, nomeModelo);
        return ResponseEntity.ok(anos);
    }
}