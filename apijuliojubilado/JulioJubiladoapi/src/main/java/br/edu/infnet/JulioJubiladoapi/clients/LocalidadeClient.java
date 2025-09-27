package br.edu.infnet.JulioJubiladoapi.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.infnet.JulioJubiladoapi.model.domain.EnderecoLocalidadeQueryResult;

@FeignClient(name = "localidade", url = "${juliopedidoapi.url}")
public interface LocalidadeClient {
    
    @GetMapping("/api/localidades/{cep}")
    EnderecoLocalidadeQueryResult obterLocalidadePorCep(@PathVariable("cep") String cep);
}