package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.edu.infnet.JulioJubiladoapi.clients.LocalidadeClient;
import br.edu.infnet.JulioJubiladoapi.model.domain.EnderecoLocalidadeQueryResult;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.LocalidadeNaoEncontradaException;

@Service
public class LocalidadeService {
    
    private final LocalidadeClient localidadeClient;
    private static final Pattern CEP_PATTERN = Pattern.compile("^(\\d{8}|\\d{5}-\\d{3})$");

    public LocalidadeService(LocalidadeClient localidadeClient) {
        this.localidadeClient = localidadeClient;
    }

    public EnderecoLocalidadeQueryResult obterLocalidadePorCep(String cep) {
        if (!StringUtils.hasText(cep) || !CEP_PATTERN.matcher(cep).matches()) {
            throw new IllegalArgumentException("CEP invalido. Use 00000000 ou 00000-000.");
        }

        EnderecoLocalidadeQueryResult localidade = localidadeClient.obterLocalidadePorCep(cep);
        if (localidade == null || !StringUtils.hasText(localidade.getCep())) {
            throw new LocalidadeNaoEncontradaException("CEP nao encontrado: " + cep);
        }
        return localidade;
    }
}
