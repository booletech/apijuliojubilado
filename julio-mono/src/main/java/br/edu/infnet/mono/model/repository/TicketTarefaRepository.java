package br.edu.infnet.mono.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.infnet.mono.model.domain.TicketTarefa;

@Repository
public interface TicketTarefaRepository extends JpaRepository<TicketTarefa, Integer> {
}
