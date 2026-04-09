package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class TicketInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // Construtor que recebe uma mensagem
    public TicketInvalidoException(String mensagem) {
        super(mensagem);
    }
}
