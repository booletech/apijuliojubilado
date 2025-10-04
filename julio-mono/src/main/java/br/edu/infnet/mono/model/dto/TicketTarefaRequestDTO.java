package br.edu.infnet.mono.model.dto;

import java.math.BigDecimal;

public class TicketTarefaRequestDTO {
    private String codigo;
    private String descricao;
    private String status;
    private String clienteCpf;
    private String funcionarioCpf;
    private BigDecimal valorTotal;
    private String dataAbertura;

    public TicketTarefaRequestDTO(String codigo, String descricao, String status, String clienteCpf, String funcionarioCpf, BigDecimal valorTotal, String dataAbertura) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.status = status;
        this.clienteCpf = clienteCpf;
        this.funcionarioCpf = funcionarioCpf;
        this.valorTotal = valorTotal;
        this.dataAbertura = dataAbertura;
    }

    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public String getClienteCpf() { return clienteCpf; }
    public String getFuncionarioCpf() { return funcionarioCpf; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getDataAbertura() { return dataAbertura; }
}
