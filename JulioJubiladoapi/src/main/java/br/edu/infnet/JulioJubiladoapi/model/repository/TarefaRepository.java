package br.edu.infnet.JulioJubiladoapi.model.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {

}
