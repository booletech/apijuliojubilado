package br.edu.infnet.mono.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.model.domain.StatusTarefa;
import br.edu.infnet.mono.model.domain.Tarefa;
import br.edu.infnet.mono.model.domain.TicketTarefa;
import br.edu.infnet.mono.model.domain.TipoTarefa;
import br.edu.infnet.mono.model.dto.TarefaRequestDTO;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import br.edu.infnet.mono.repository.TarefaRepository;
import br.edu.infnet.mono.repository.TicketTarefaRepository;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final TicketTarefaRepository ticketRepository;
    private final FuncionarioRepository funcionarioRepository;

    public TarefaService(TarefaRepository tarefaRepository,
                        TicketTarefaRepository ticketRepository,
                        FuncionarioRepository funcionarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.ticketRepository = ticketRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public Tarefa incluir(TarefaRequestDTO dto) {
        // Buscar TicketTarefa
        TicketTarefa ticket = ticketRepository.findById(dto.getTicketTarefaId())
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));

        // Validar e buscar tipo
        TipoTarefa tipo;
        try {
            tipo = TipoTarefa.valueOf(dto.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de tarefa inválido: " + dto.getTipo());
        }

        // Criar tarefa
        Tarefa tarefa = new Tarefa();
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setTipo(tipo);
        tarefa.setValor(tipo.getPrecoBase());  // ✅ Preço automático do tipo
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setTickettarefa(ticket);

        
        if (dto.getFuncionarioId() != null) {
            Funcionario func = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
            tarefa.setFuncionario(func);
        }

        Tarefa salva = tarefaRepository.save(tarefa);

        // ✅ Atualizar valor total do ticket
        atualizarValorTotalTicket(ticket.getId());

        return salva;
    }

    @Transactional
    public void excluir(Integer id) {
        Tarefa tarefa = tarefaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
        
        Integer ticketId = tarefa.getTickettarefa().getId();
        tarefaRepository.deleteById(id);

        atualizarValorTotalTicket(ticketId);
    }

    private void atualizarValorTotalTicket(Integer ticketId) {
        TicketTarefa ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));

        BigDecimal total = ticket.getTarefas().stream()
            .map(Tarefa::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        ticket.setValorTotal(total);
        ticketRepository.save(ticket);
    }

    public List<Tarefa> obterLista() {
        return tarefaRepository.findAll();
    }

    public Tarefa obterPorId(Integer id) {
        return tarefaRepository.findById(id).orElse(null);
    }
}