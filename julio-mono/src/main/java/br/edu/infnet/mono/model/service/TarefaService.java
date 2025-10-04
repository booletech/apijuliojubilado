package br.edu.infnet.mono.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.mono.model.domain.Tarefa;

@Service
public class TarefaService {

    private final List<Tarefa> tarefas = new ArrayList<>();

    public Tarefa incluir(Tarefa tarefa) {
        tarefas.add(tarefa);
        return tarefa;
    }

    public Tarefa alterar(Integer id, Tarefa tarefa) {
        // Placeholder logic for updating a tarefa
        return tarefa;
    }

    public void excluir(Integer id) {
        // Placeholder logic for deleting a tarefa
    }

    public Tarefa inativar(Integer id) {
        // Placeholder logic for inactivating a tarefa
        return null;
    }

    public List<Tarefa> obterLista() {
        return tarefas;
    }

    public Tarefa obterPorId(Integer id) {
        // Placeholder logic for retrieving a tarefa by ID
        return null;
    }
}