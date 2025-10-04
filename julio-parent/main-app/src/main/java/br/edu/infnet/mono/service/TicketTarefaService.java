package br.edu.infnet.mono.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.infnet.mono.model.domain.Cliente;
import br.edu.infnet.mono.model.domain.Funcionario;
import br.edu.infnet.mono.model.domain.StatusTicket;
import br.edu.infnet.mono.model.domain.TicketTarefa;
import br.edu.infnet.mono.model.dto.TicketTarefaRequestDTO;
import br.edu.infnet.mono.model.dto.TicketTarefaResponseDTO;
import br.edu.infnet.mono.repository.ClienteRepository;
import br.edu.infnet.mono.repository.FuncionarioRepository;
import br.edu.infnet.mono.repository.TicketTarefaRepository;

@Service
public class TicketTarefaService {

    private final TicketTarefaRepository ticketRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public TicketTarefaService(TicketTarefaRepository ticketRepository,
                               ClienteRepository clienteRepository,
                               FuncionarioRepository funcionarioRepository) {
        this.ticketRepository = ticketRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public TicketTarefaResponseDTO incluir(TicketTarefaRequestDTO dto) {
        // Validações primeiro
        if (dto == null) {
            throw new IllegalArgumentException("DTO não pode ser nulo");
        }
        if (dto.getCpfCliente() == null || dto.getCpfCliente().isBlank()) {
            throw new IllegalArgumentException("CPF do cliente é obrigatório");
        }
        if (dto.getCpfFuncionario() == null || dto.getCpfFuncionario().isBlank()) {
            throw new IllegalArgumentException("CPF do funcionário é obrigatório");
        }

        // Formata CPFs para xxx.xxx.xxx-xx
        String cpfCliente = formatarCpf(dto.getCpfCliente());
        String cpfFuncionario = formatarCpf(dto.getCpfFuncionario());

        // Busca com CPF formatado
        Cliente cliente = clienteRepository.findByCpf(cpfCliente)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + cpfCliente));

        Funcionario funcionario = funcionarioRepository.findByCpf(cpfFuncionario)
            .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado: " + cpfFuncionario));

        // Criar TicketTarefa
        TicketTarefa ticket = new TicketTarefa();
        ticket.setCodigo(dto.getCodigo());
        ticket.setCliente(cliente);
        ticket.setFuncionario(funcionario);
        ticket.setValorTotal(dto.getValorTotal());
        ticket.setDataAbertura(dto.getDataAbertura());
        ticket.setDataFechamento(dto.getDataFechamento());

        // Mapear Status
        try {
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                ticket.setStatus(StatusTicket.valueOf(dto.getStatus().trim().toUpperCase()));
            } else {
                ticket.setStatus(StatusTicket.ABERTO);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + dto.getStatus());
        }

        TicketTarefa salvo = ticketRepository.save(ticket);
        return toResponseDTO(salvo);
    }

    @Transactional
    public TicketTarefaResponseDTO alterar(Integer id, TicketTarefa ticket) {
        TicketTarefa existente = ticketRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado: " + id));

        existente.setCodigo(ticket.getCodigo());
        existente.setStatus(ticket.getStatus());
        existente.setValorTotal(ticket.getValorTotal());
        existente.setCliente(ticket.getCliente());
        existente.setFuncionario(ticket.getFuncionario());
        existente.setDataAbertura(ticket.getDataAbertura());
        existente.setDataFechamento(ticket.getDataFechamento());

        TicketTarefa salvo = ticketRepository.save(existente);
        return toResponseDTO(salvo);
    }

    @Transactional
    public void excluir(Integer id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket não encontrado: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Transactional
    public TicketTarefaResponseDTO inativar(Integer id) {
        TicketTarefa ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado: " + id));
        
        ticket.setStatus(StatusTicket.CANCELADO);
        
        TicketTarefa salvo = ticketRepository.save(ticket);
        return toResponseDTO(salvo);
    }

    public List<TicketTarefaResponseDTO> obterLista() {
        return ticketRepository.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    public TicketTarefaResponseDTO obterPorId(Integer id) {
        return ticketRepository.findById(id)
            .map(this::toResponseDTO)
            .orElse(null);
    }

    public TicketTarefaResponseDTO obterPorCodigo(String codigo) {
        if (codigo == null) return null;
        return ticketRepository.findAll().stream()
            .filter(t -> codigo.equals(t.getCodigo()))
            .findFirst()
            .map(this::toResponseDTO)
            .orElse(null);
    }
    
    private TicketTarefaResponseDTO toResponseDTO(TicketTarefa ticket) {
        TicketTarefaResponseDTO dto = new TicketTarefaResponseDTO(
            ticket.getId(),
            ticket.getCodigo(),
            ticket.getStatus().name(),
            ticket.getValorTotal(),
            ticket.getDataAbertura(),
            ticket.getDataFechamento(),
            ticket.getCliente().getId(),
            ticket.getCliente().getNome(),
            ticket.getCliente().getCpf(),
            ticket.getFuncionario().getId(),
            ticket.getFuncionario().getNome(),
            ticket.getFuncionario().getMatricula()
        );

        if (ticket.getTarefas() != null && !ticket.getTarefas().isEmpty()) {
            List<TicketTarefaResponseDTO.TarefaResumoDTO> tarefasDTO = ticket.getTarefas().stream()
                .map(t -> new TicketTarefaResponseDTO.TarefaResumoDTO(
                    t.getId(),
                    t.getDescricao(),
                    t.getValor(),
                    t.getStatus() != null ? t.getStatus().name() : "PENDENTE"
                ))
                .collect(Collectors.toList());
            dto.setTarefas(tarefasDTO);
        }

        return dto;
    }
    
    private String formatarCpf(String cpf) {
        if (cpf == null) return null;
        
        String digitos = cpf.replace(".", "")
                            .replace("-", "")
                            .replace(" ", "");
        
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("CPF inválido: deve ter 11 dígitos");
        }
        
        return digitos.substring(0, 3) + "." +
               digitos.substring(3, 6) + "." +
               digitos.substring(6, 9) + "-" +
               digitos.substring(9);
    }
}