package br.edu.infnet.mono.model.domain;

import java.math.BigDecimal;

public enum TipoTarefa {
    ALINHAMENTO(BigDecimal.valueOf(80.00)),
    BALANCEAMENTO(BigDecimal.valueOf(60.00)),
    TROCA_PNEU(BigDecimal.valueOf(150.00)),
    CALIBRAGEM(BigDecimal.valueOf(20.00)),
    CONSERTO_PNEU(BigDecimal.valueOf(40.00)),
    REVISAO(BigDecimal.valueOf(120.00)),
    DIAGNOSTICO(BigDecimal.valueOf(50.00)),
    GEOMETRIA(BigDecimal.valueOf(100.00));

    private final BigDecimal precoBase;

    TipoTarefa(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }
}