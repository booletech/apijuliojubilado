package br.edu.infnet.juliopedidoapi.model.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.LocalidadeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/localidades")
@Validated
public class LocalidadeController {

	//injetar o service
	private final LocalidadeService localidadeService;

	//construtor
	public LocalidadeController(LocalidadeService localidadeService) {
		this.localidadeService = localidadeService;
	}

	@GetMapping("/{cep}")
		public ResponseEntity<EnderecoLocalidadeQueryResult> obterLocalidadePorCep(
				@PathVariable
				@NotBlank(message = "CEP obrigatorio.")
				@Pattern(regexp = "^(\\d{8}|\\d{5}-\\d{3})$", message = "CEP invalido.")
				String cep){
		
		EnderecoLocalidadeQueryResult localidadeQueryResult = localidadeService.obterLocalidadePorCep(cep);

		return  ResponseEntity.ok(localidadeQueryResult);    

	}

}
