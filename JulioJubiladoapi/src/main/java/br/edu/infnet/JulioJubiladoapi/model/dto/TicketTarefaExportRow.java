package br.edu.infnet.JulioJubiladoapi.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketTarefaExportRow(
        UUID id,
        String codigo,
        String status,
        BigDecimal valorTotal,
        String dataAbertura,
        String dataFechamento,
        UUID clienteId,
        UUID funcionarioId
) {}
