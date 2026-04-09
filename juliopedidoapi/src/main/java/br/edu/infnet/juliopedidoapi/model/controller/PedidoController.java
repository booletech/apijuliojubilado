package br.edu.infnet.juliopedidoapi.model.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.dto.PedidoRequestDTO;
import br.edu.infnet.juliopedidoapi.model.dto.PedidoResponseDTO;
import br.edu.infnet.juliopedidoapi.model.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/pedidos")
@Validated
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping
	public ResponseEntity<PedidoResponseDTO> incluir(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
		PedidoResponseDTO response = pedidoService.incluir(pedidoRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PedidoResponseDTO> alterar(@PathVariable UUID id,
			@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
		PedidoResponseDTO response = pedidoService.alterar(id, pedidoRequestDTO);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable UUID id) {
		pedidoService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/inativar")
	public ResponseEntity<PedidoResponseDTO> inativar(@PathVariable UUID id) {
		PedidoResponseDTO response = pedidoService.inativar(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<PedidoResponseDTO>> obterLista(Pageable pageable) {
		return ResponseEntity.ok(pedidoService.obterLista(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PedidoResponseDTO> obterPorId(@PathVariable UUID id) {
		return ResponseEntity.ok(pedidoService.obterPorId(id));
	}

	@GetMapping("/search")
	public ResponseEntity<List<String>> buscarDescricoes(
			@RequestParam("q")
			@NotBlank(message = "Descricao obrigatoria.")
			String termo,
			@RequestParam(name = "page", defaultValue = "0")
			@Min(value = 0, message = "Pagina minima e 0.")
			int page,
			@RequestParam(name = "limit", defaultValue = "10")
			@Min(value = 1, message = "Limite minimo e 1.")
			@Max(value = 100, message = "Limite maximo e 100.")
			int limit) {
		return ResponseEntity.ok(pedidoService.buscarDescricoes(termo, page, limit));
	}
}
