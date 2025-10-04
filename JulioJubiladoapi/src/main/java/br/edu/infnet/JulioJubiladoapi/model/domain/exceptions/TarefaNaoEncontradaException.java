package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class TarefaNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // Construtor que recebe uma mensagem
    public TarefaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
