package br.edu.infnet.juliopedidoapi.model.domain.exceptions;

public class LocalidadeNaoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LocalidadeNaoEncontradaException(String message) {
        super(message);
    }
}
