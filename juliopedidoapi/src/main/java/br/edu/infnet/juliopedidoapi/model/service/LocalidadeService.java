package br.edu.infnet.juliopedidoapi.model.service;

import org.springframework.stereotype.Service;

import br.edu.infnet.juliopedidoapi.model.clients.ViaCepFeignClient;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoRetorno;

@Service
public class LocalidadeService {
	
	
	
	private final ViaCepFeignClient viaCepFeignClient;
	
	public LocalidadeService(ViaCepFeignClient viaCepFeignClient) {
		this.viaCepFeignClient = viaCepFeignClient;
		// TODO Auto-generated constructor stub
	}
	
	public EnderecoLocalidadeQueryResult obterLocalidadePorCep (String cep) {
	
		
		EnderecoRetorno endereco = viaCepFeignClient.findByCep(cep);
		String municipioOrigem = endereco.getLocalidade();
		String ufOrigem = endereco.getUf();
		String logradouro = endereco.getLogradouro();
		String complemento = endereco.getComplemento();
		String bairro = endereco.getBairro();
		
		EnderecoLocalidadeQueryResult localidadeQueryResult = new EnderecoLocalidadeQueryResult();
		localidadeQueryResult.setCepConsultado(cep);
		localidadeQueryResult.setLogradouro(logradouro);
		localidadeQueryResult.setComplemento(complemento);
		localidadeQueryResult.setBairro(bairro);
		localidadeQueryResult.setLocalidade(municipioOrigem);
		localidadeQueryResult.setUf(ufOrigem);
		
		
	return localidadeQueryResult;
	
	};

}