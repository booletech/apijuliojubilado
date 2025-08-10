package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class FuncionarioNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // Construtor que recebe uma mensagem
    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

