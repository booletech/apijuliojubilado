package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class TicketNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // Construtor que recebe uma mensagem
    public TicketNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
