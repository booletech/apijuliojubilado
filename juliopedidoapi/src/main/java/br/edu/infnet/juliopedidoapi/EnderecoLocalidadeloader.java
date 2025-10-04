package br.edu.infnet.juliopedidoapi;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.LocalidadeService;

@Component
public class EnderecoLocalidadeloader implements ApplicationRunner {

	
	private final LocalidadeService localidadeService;
	
	public EnderecoLocalidadeloader(LocalidadeService localidadeService) {
		this.localidadeService = localidadeService; 
	}
	
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
	
		EnderecoLocalidadeQueryResult enderecolocalidadeQueryResult= localidadeService.obterLocalidadePorCep("20010020");
		
		System.out.println("[RESULTADO] MEUCEP: " + enderecolocalidadeQueryResult.getCepConsultado());
		System.out.println("[RESULTADO] MEU Municipio: " + enderecolocalidadeQueryResult.getLocalidade());
		System.out.println("[RESULTADO] MINHA UF: " + enderecolocalidadeQueryResult.getUf());
	}
}
