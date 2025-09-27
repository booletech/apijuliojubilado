package br.edu.infnet.mono.model.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.model.dto.PedidoRequestDTO;
import br.edu.infnet.mono.model.dto.PedidoResponseDTO;
import br.edu.infnet.mono.model.domain.Pedido;
import br.edu.infnet.JulioJubiladoapi.model.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> incluir(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoResponseDTO novoPedido = pedidoService.incluir(pedidoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Pedido> alterar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        Pedido pedidoAlterado = pedidoService.alterar(id, pedido);
        return ResponseEntity.ok(pedidoAlterado);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        pedidoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/inativar")
    public ResponseEntity<Pedido> inativar(@PathVariable Integer id) {
        Pedido pedido = pedidoService.inativar(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> obterLista() {
        List<Pedido> lista = pedidoService.obterLista();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping(value = "/{id}")
    public Pedido obterPorId(@PathVariable Integer id) {
        return pedidoService.obterPorId(id);
    }
}