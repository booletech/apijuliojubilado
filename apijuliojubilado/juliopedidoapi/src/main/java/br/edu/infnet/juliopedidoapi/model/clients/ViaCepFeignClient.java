package br.edu.infnet.juliopedidoapi.model.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.infnet.juliopedidoapi.model.domain.EnderecoRetorno;

@FeignClient(name = "viacep", url = "${api.viacep.url}")
public interface ViaCepFeignClient {
	
	
	
	@GetMapping("/ws/{cep}/json/")
	EnderecoRetorno findByCep(@PathVariable("cep") String cep);

}
