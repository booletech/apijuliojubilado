package br.edu.infnet.JulioJubiladoapi.model.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Cliente;
import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.Tarefa;
import br.edu.infnet.JulioJubiladoapi.model.domain.TicketTarefa;
import br.edu.infnet.JulioJubiladoapi.model.dto.ClienteExportRow;
import br.edu.infnet.JulioJubiladoapi.model.dto.TarefaExportRow;
import br.edu.infnet.JulioJubiladoapi.model.dto.TicketTarefaExportRow;
import br.edu.infnet.JulioJubiladoapi.model.repository.ClienteRepository;
import br.edu.infnet.JulioJubiladoapi.model.repository.TarefaRepository;
import br.edu.infnet.JulioJubiladoapi.model.repository.TicketTarefaRepository;

@Service
public class ExportService {

    private static final MediaType CSV_MEDIA_TYPE = MediaType.parseMediaType("text/csv; charset=UTF-8");

    private final ClienteRepository clienteRepository;
    private final TarefaRepository tarefaRepository;
    private final TicketTarefaRepository ticketTarefaRepository;
    private final ObjectMapper objectMapper;

    public ExportService(
            ClienteRepository clienteRepository,
            TarefaRepository tarefaRepository,
            TicketTarefaRepository ticketTarefaRepository,
            ObjectMapper objectMapper
    ) {
        this.clienteRepository = clienteRepository;
        this.tarefaRepository = tarefaRepository;
        this.ticketTarefaRepository = ticketTarefaRepository;
        this.objectMapper = objectMapper;
    }

    public ExportPayload exportClientes(ExportFormat format) {
        List<ClienteExportRow> rows = clienteRepository.findAll().stream()
                .map(this::toClienteRow)
                .toList();
        if (format == ExportFormat.JSON) {
            return jsonPayload("clientes", rows);
        }
        return csvPayload("clientes", buildClientesCsv(rows));
    }

    public ExportPayload exportTarefas(ExportFormat format) {
        List<TarefaExportRow> rows = tarefaRepository.findAll().stream()
                .map(this::toTarefaRow)
                .toList();
        if (format == ExportFormat.JSON) {
            return jsonPayload("tarefas", rows);
        }
        return csvPayload("tarefas", buildTarefasCsv(rows));
    }

    public ExportPayload exportTickets(ExportFormat format) {
        List<TicketTarefaExportRow> rows = ticketTarefaRepository.findAll().stream()
                .map(this::toTicketRow)
                .toList();
        if (format == ExportFormat.JSON) {
            return jsonPayload("tickets", rows);
        }
        return csvPayload("tickets", buildTicketsCsv(rows));
    }

    private ClienteExportRow toClienteRow(Cliente cliente) {
        Endereco endereco = cliente.getEndereco();
        return new ClienteExportRow(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getDataNascimento(),
                cliente.getDataUltimaVisita(),
                cliente.getLimiteCredito(),
                cliente.isPossuiFiado(),
                cliente.getPontosFidelidade(),
                endereco != null ? endereco.getCep() : null,
                endereco != null ? endereco.getLogradouro() : null,
                endereco != null ? endereco.getNumero() : null,
                endereco != null ? endereco.getComplemento() : null,
                endereco != null ? endereco.getBairro() : null,
                endereco != null ? endereco.getLocalidade() : null,
                endereco != null ? endereco.getUf() : null,
                endereco != null ? endereco.getEstado() : null
        );
    }

    private TarefaExportRow toTarefaRow(Tarefa tarefa) {
        return new TarefaExportRow(
                tarefa.getId(),
                tarefa.getDescricao(),
                tarefa.getTipo() != null ? tarefa.getTipo().name() : null,
                tarefa.getStatus(),
                tarefa.getValor(),
                getTicketId(tarefa),
                getClienteId(tarefa),
                getFuncionarioId(tarefa)
        );
    }

    private TicketTarefaExportRow toTicketRow(TicketTarefa ticket) {
        return new TicketTarefaExportRow(
                ticket.getId(),
                ticket.getCodigo(),
                ticket.getStatus() != null ? ticket.getStatus().name() : null,
                ticket.getValorTotal(),
                ticket.getDataAbertura(),
                ticket.getDataFechamento(),
                ticket.getCliente() != null ? ticket.getCliente().getId() : null,
                ticket.getFuncionario() != null ? ticket.getFuncionario().getId() : null
        );
    }

    private UUID getTicketId(Tarefa tarefa) {
        return tarefa.getTickettarefa() != null ? tarefa.getTickettarefa().getId() : null;
    }

    private UUID getClienteId(Tarefa tarefa) {
        return tarefa.getCliente() != null ? tarefa.getCliente().getId() : null;
    }

    private UUID getFuncionarioId(Tarefa tarefa) {
        Funcionario funcionario = tarefa.getFuncionario();
        return funcionario != null ? funcionario.getId() : null;
    }

    private ExportPayload jsonPayload(String baseName, Object data) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(data);
            return new ExportPayload(payload, MediaType.APPLICATION_JSON, buildFilename(baseName, ExportFormat.JSON));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Falha ao gerar exportacao.", ex);
        }
    }

    private ExportPayload csvPayload(String baseName, String data) {
        byte[] payload = data.getBytes(StandardCharsets.UTF_8);
        return new ExportPayload(payload, CSV_MEDIA_TYPE, buildFilename(baseName, ExportFormat.CSV));
    }

    private String buildClientesCsv(List<ClienteExportRow> rows) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, "id", "nome", "email", "cpf", "telefone", "data_nascimento",
                "data_ultima_visita", "limite_credito", "possui_fiado", "pontos_fidelidade",
                "cep", "logradouro", "numero", "complemento", "bairro", "localidade", "uf", "estado");
        for (ClienteExportRow row : rows) {
            appendRow(sb,
                    row.id(),
                    row.nome(),
                    row.email(),
                    row.cpf(),
                    row.telefone(),
                    row.dataNascimento(),
                    row.dataUltimaVisita(),
                    row.limiteCredito(),
                    row.possuiFiado(),
                    row.pontosFidelidade(),
                    row.cep(),
                    row.logradouro(),
                    row.numero(),
                    row.complemento(),
                    row.bairro(),
                    row.localidade(),
                    row.uf(),
                    row.estado()
            );
        }
        return sb.toString();
    }

    private String buildTarefasCsv(List<TarefaExportRow> rows) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, "id", "descricao", "tipo", "status", "valor", "ticket_id", "cliente_id", "funcionario_id");
        for (TarefaExportRow row : rows) {
            appendRow(sb,
                    row.id(),
                    row.descricao(),
                    row.tipo(),
                    row.status(),
                    row.valor(),
                    row.ticketId(),
                    row.clienteId(),
                    row.funcionarioId()
            );
        }
        return sb.toString();
    }

    private String buildTicketsCsv(List<TicketTarefaExportRow> rows) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, "id", "codigo", "status", "valor_total", "data_abertura", "data_fechamento",
                "cliente_id", "funcionario_id");
        for (TicketTarefaExportRow row : rows) {
            appendRow(sb,
                    row.id(),
                    row.codigo(),
                    row.status(),
                    row.valorTotal(),
                    row.dataAbertura(),
                    row.dataFechamento(),
                    row.clienteId(),
                    row.funcionarioId()
            );
        }
        return sb.toString();
    }

    private void appendRow(StringBuilder sb, Object... values) {
        for (int i = 0; i < values.length; i++) {
            sb.append(csvEscape(values[i]));
            if (i < values.length - 1) {
                sb.append(',');
            }
        }
        sb.append('\n');
    }

    private String csvEscape(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        boolean needsQuotes = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r");
        if (text.contains("\"")) {
            text = text.replace("\"", "\"\"");
        }
        return needsQuotes ? "\"" + text + "\"" : text;
    }

    private String buildFilename(String baseName, ExportFormat format) {
        return baseName + "-" + LocalDate.now() + "." + format.extension();
    }

    public record ExportPayload(byte[] data, MediaType contentType, String filename) {}
}
