package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.List;

public interface CrudService<T, ID> {
    T incluir(T entity);
    T alterar(ID id, T entity);
    void excluir(ID id);
    List<T> obterLista();
    T obterPorId(ID id);
}

