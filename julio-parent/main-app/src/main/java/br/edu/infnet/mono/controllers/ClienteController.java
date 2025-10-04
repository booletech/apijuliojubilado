package br.edu.infnet.mono.controllers;

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

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.dto.ClienteRequestDTO;
import br.edu.infnet.mono.model.dto.ClienteResponseDTO;
import br.edu.infnet.mono.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	
	@PostMapping
	public ResponseEntity<ClienteResponseDTO> incluir(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		ClienteResponseDTO novocliente = clienteService.incluir(clienteRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(novocliente);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ClienteResponseDTO> alterar(@PathVariable Integer id, @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
		Cliente clienteAlterado = clienteService.alterar(id, clienteRequestDTO);
		
		return ResponseEntity.ok(new ClienteResponseDTO(clienteAlterado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Integer id) {
		clienteService.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/fiado")
	public ResponseEntity<ClienteResponseDTO> alternarFiado(@PathVariable Integer id) {
		Cliente atualizado = clienteService.alternarFiado(id);
		return ResponseEntity.ok(new ClienteResponseDTO(atualizado));
	}

	@GetMapping
	public ResponseEntity<List<ClienteResponseDTO>> obterLista() {
		List<Cliente> lista = clienteService.obterLista();
		if (lista.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		List<ClienteResponseDTO> dtoList = lista.stream().map(ClienteResponseDTO::new).toList();
		return ResponseEntity.ok(dtoList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponseDTO> obterPorId(@PathVariable Integer id) {
		Cliente cliente = clienteService.obterPorId(id);
		return ResponseEntity.ok(new ClienteResponseDTO(cliente));
	}

}