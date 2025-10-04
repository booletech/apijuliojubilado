package br.edu.infnet.mono.model.dto;

import java.util.List;

public class FipeModelosResponseDTO {
	private List<FipeModeloDTO> modelos;

	public List<FipeModeloDTO> getModelos() {
		return modelos;
	}

	public void setModelos(List<FipeModeloDTO> modelos) {
		this.modelos = modelos;
	}
}