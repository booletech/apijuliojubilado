
package br.edu.infnet.JulioJubiladoapi.model.domain.exceptions;

public class ClienteInvalidoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	// Construtor que recebe uma mensagem
	public ClienteInvalidoException(String mensagem) {
		super(mensagem);
	}
}
