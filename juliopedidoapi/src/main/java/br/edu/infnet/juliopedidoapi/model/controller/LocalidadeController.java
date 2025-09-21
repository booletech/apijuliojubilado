package br.edu.infnet.juliopedidoapi.model.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.LocalidadeService;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadeController {
	
	//injetar o service
	private final LocalidadeService localidadeService;
	
	//construtor
	public LocalidadeController(LocalidadeService localidadeService) {
		this.localidadeService = localidadeService;
	}
	
	@GetMapping("/{cep}")
		public ResponseEntity<EnderecoLocalidadeQueryResult> obterLocalidadePorCep(@PathVariable String cep){
		
		EnderecoLocalidadeQueryResult localidadeQueryResult = localidadeService.obterLocalidadePorCep(cep);
		
		return  ResponseEntity.ok(localidadeQueryResult);    
		
	}

}
