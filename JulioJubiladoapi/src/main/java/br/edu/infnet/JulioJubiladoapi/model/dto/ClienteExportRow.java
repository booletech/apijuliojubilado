package br.edu.infnet.JulioJubiladoapi.model.dto;

import java.util.UUID;

public record ClienteExportRow(
        UUID id,
        String nome,
        String email,
        String cpf,
        String telefone,
        String dataNascimento,
        String dataUltimaVisita,
        double limiteCredito,
        boolean possuiFiado,
        int pontosFidelidade,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String estado
) {}
