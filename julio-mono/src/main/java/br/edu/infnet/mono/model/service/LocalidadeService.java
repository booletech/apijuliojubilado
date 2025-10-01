package br.edu.infnet.mono.model.service;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.EnderecoLocalidadeQueryResult;

@Service
public class LocalidadeService {

    public EnderecoLocalidadeQueryResult obterLocalidadePorCep(String cep) {
        // Implementação simples de fallback/local: retornar objeto vazio ou nulo.
        // Substitua por chamada a cliente Feign ou delegação ao módulo externo conforme necessário.
        EnderecoLocalidadeQueryResult result = new EnderecoLocalidadeQueryResult();
        result.setCepConsultado(cep);
        result.setLogradouro("");
        result.setComplemento("");
        result.setBairro("");
        result.setLocalidade("");
        result.setUf("");
        return result;
    }
}