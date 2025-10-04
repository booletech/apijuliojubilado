package br.edu.infnet.mono.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.model.domain.Comentario;
import br.edu.infnet.mono.service.ComentarioService;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

	private final ComentarioService comentarioService;

	public ComentarioController(ComentarioService comentarioService) {
		this.comentarioService = comentarioService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<List<Comentario>> obterLista() {
		List<Comentario> comentarios = comentarioService.obterLista();
		return ResponseEntity.ok(comentarios);
	}
	

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
		comentarioService.excluir(id);

		return ResponseEntity.noContent().build();

	}

}