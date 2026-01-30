package br.edu.infnet.JulioJubiladoapi.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TarefaExportRow(
        UUID id,
        String descricao,
        String tipo,
        String status,
        BigDecimal valor,
        UUID ticketId,
        UUID clienteId,
        UUID funcionarioId
) {}
