package br.edu.infnet.mono.model.domain.exceptions;

public class ClienteInvalidoException extends RuntimeException {
    public ClienteInvalidoException(String message) {
        super(message);
    }
}
