package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class TarefaInvalidaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// Construtor que recebe uma mensagem
	public TarefaInvalidaException(String mensagem) {
		super(mensagem);
	}
}
