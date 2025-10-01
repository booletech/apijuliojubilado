package br.edu.infnet.mono.service;

import java.util.List;

public interface CrudService<T, ID> {
    T incluir(T objeto);
    T alterar(ID id, T objeto);
    void excluir(ID id);
    List<T> obterLista();
    T obterPorId(ID id);
}
