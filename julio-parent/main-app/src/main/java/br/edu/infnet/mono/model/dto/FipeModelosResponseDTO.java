package br.edu.infnet.mono.model.dto;

import java.util.List;

public class FipeModelosResponseDTO {
    private List<FipeResponseDTO> modelos;
    private List<FipeAnoDTO> anos;

    public FipeModelosResponseDTO() {}

    public List<FipeResponseDTO> getModelos() {
        return modelos;
    }

    public void setModelos(List<FipeResponseDTO> modelos) {
        this.modelos = modelos;
    }

    public List<FipeAnoDTO> getAnos() {
        return anos;
    }

    public void setAnos(List<FipeAnoDTO> anos) {
        this.anos = anos;
    }
}
