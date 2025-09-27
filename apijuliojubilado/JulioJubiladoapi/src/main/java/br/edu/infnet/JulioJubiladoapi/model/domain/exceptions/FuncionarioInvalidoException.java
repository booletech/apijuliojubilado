package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class FuncionarioInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// Construtor que recebe uma mensagem
	public FuncionarioInvalidoException(String mensagem) {
		super(mensagem);
	}
}
