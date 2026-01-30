package br.edu.infnet.juliopedidoapi.model.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.edu.infnet.juliopedidoapi.model.clients.ViaCepFeignClient;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.juliopedidoapi.model.domain.EnderecoRetorno;
import br.edu.infnet.juliopedidoapi.model.domain.exceptions.LocalidadeNaoEncontradaException;

@Service
public class LocalidadeService {

	private static final Pattern CEP_PATTERN = Pattern.compile("^(\\d{8}|\\d{5}-\\d{3})$");

	private final ViaCepFeignClient viaCepFeignClient;
	
	public LocalidadeService(ViaCepFeignClient viaCepFeignClient) {
		this.viaCepFeignClient = viaCepFeignClient;
	}
	
	public EnderecoLocalidadeQueryResult obterLocalidadePorCep (String cep) {
		if (!StringUtils.hasText(cep) || !CEP_PATTERN.matcher(cep).matches()) {
			throw new IllegalArgumentException("CEP invalido. Use 00000000 ou 00000-000.");
		}

		EnderecoRetorno endereco = viaCepFeignClient.findByCep(cep);
		if (endereco == null || !StringUtils.hasText(endereco.getCep())) {
			throw new LocalidadeNaoEncontradaException("CEP nao encontrado: " + cep);
		}
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
