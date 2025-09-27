package br.edu.infnet.JulioJubiladoapi.model.service;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.clients.LocalidadeClient;
import br.edu.infnet.JulioJubiladoapi.model.domain.EnderecoLocalidadeQueryResult;

@Service
public class LocalidadeService {
    
    private final LocalidadeClient localidadeClient;

    public LocalidadeService(LocalidadeClient localidadeClient) {
        this.localidadeClient = localidadeClient;
    }

    public EnderecoLocalidadeQueryResult obterLocalidadePorCep(String cep) {
        return localidadeClient.obterLocalidadePorCep(cep);
    }
}