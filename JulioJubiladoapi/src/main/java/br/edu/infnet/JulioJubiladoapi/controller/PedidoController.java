package br.edu.infnet.JulioJubiladoapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.domain.LocalidadePedido;
import br.edu.infnet.JulioJubiladoapi.model.dto.PedidoDoc;
import br.edu.infnet.JulioJubiladoapi.model.service.PedidoSearchService;
import br.edu.infnet.JulioJubiladoapi.model.service.PedidoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/pedidos")
@Validated
public class PedidoController {
    private final PedidoService pedidoService;
    private final PedidoSearchService pedidoSearchService;

    public PedidoController(PedidoService pedidoService, PedidoSearchService pedidoSearchService) {
        this.pedidoService = pedidoService;
        this.pedidoSearchService = pedidoSearchService;
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<LocalidadePedido> obterLocalidade(
            @PathVariable
            @NotBlank(message = "CEP obrigatorio.")
            @Pattern(regexp = "^(\\d{8}|\\d{5}-\\d{3})$", message = "CEP invalido.")
            String cep) {
        LocalidadePedido localidadepedido = pedidoService.obterLocalidade(cep);
        return ResponseEntity.ok(localidadepedido);
    }

    @GetMapping("/fabricantes/{tipoVeiculo}")
    public ResponseEntity<List<Map<String, Object>>> listarFabricantes(
            @PathVariable
            @NotBlank(message = "Tipo de veiculo obrigatorio.")
            String tipoVeiculo) {
        List<Map<String, Object>> fabricantes = pedidoService.listarFabricantes(tipoVeiculo);
        return ResponseEntity.ok(fabricantes);
    }

    @GetMapping("/fabricantes/{tipoVeiculo}/{nomeFabricante}")
    public ResponseEntity<Map<String, Object>> listarModelosPorFabricante(
            @PathVariable
            @NotBlank(message = "Tipo de veiculo obrigatorio.")
            String tipoVeiculo,
            @PathVariable
            @NotBlank(message = "Nome do fabricante obrigatorio.")
            String nomeFabricante) {
        Map<String, Object> modelos = pedidoService.listarModelosPorFabricante(tipoVeiculo, nomeFabricante);
        return ResponseEntity.ok(modelos);
    }

    @GetMapping("/fabricantes/{tipoVeiculo}/{nomeFabricante}/{nomeModelo}/anos")
    public ResponseEntity<List<Map<String, Object>>> listarAnosPorModelo(
            @PathVariable
            @NotBlank(message = "Tipo de veiculo obrigatorio.")
            String tipoVeiculo,
            @PathVariable
            @NotBlank(message = "Nome do fabricante obrigatorio.")
            String nomeFabricante,
            @PathVariable
            @NotBlank(message = "Nome do modelo obrigatorio.")
            String nomeModelo) {
        List<Map<String, Object>> anos = pedidoService.listarAnosPorModelo(tipoVeiculo, nomeFabricante, nomeModelo);
        return ResponseEntity.ok(anos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PedidoDoc>> buscarDescricoes(
            @RequestParam("q")
            @NotBlank(message = "Descricao obrigatoria.")
            String termo,
            @RequestParam(name = "page", defaultValue = "0")
            @Min(value = 0, message = "Pagina minima e 0.")
            int page,
            @RequestParam(name = "limit", defaultValue = "10")
            @Min(value = 1, message = "Limite minimo e 1.")
            @Max(value = 100, message = "Limite maximo e 100.")
            int limit,
            @RequestParam(name = "source", defaultValue = "elastic")
            @Pattern(regexp = "^(elastic|db|relacional)$", message = "Source invalido.")
            String source) {
        List<PedidoDoc> resultados = pedidoSearchService.buscarDescricoes(termo, page, limit, source);
        String role = resolveRole();
        List<PedidoDoc> filtrados = resultados.stream()
                .map(doc -> doc == null ? null : doc.forRole(role))
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtrados);
    }

    private String resolveRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return "ROLE_FUNCIONARIO";
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_FUNCIONARIO");
    }
}
