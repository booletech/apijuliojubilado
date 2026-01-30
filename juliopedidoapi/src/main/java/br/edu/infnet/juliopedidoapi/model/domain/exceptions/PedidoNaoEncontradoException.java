package br.edu.infnet.juliopedidoapi.model.domain.exceptions;

public class PedidoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PedidoNaoEncontradoException(String message) {
        super(message);
    }
}
