package br.edu.infnet.mono.model.domain.exceptions;

public class TicketNaoEncontradoException extends RuntimeException {
    public TicketNaoEncontradoException(String message) {
        super(message);
    }
}
