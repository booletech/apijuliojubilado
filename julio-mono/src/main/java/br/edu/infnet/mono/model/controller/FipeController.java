package br.edu.infnet.mono.model.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.mono.model.clients.FipeClient;
import br.edu.infnet.mono.model.dto.FipeAnoDTO;
import br.edu.infnet.mono.model.dto.FipeModelosResponseDTO;
import br.edu.infnet.mono.model.dto.FipeResponseDTO;
import br.edu.infnet.mono.model.dto.FipeVeiculoDTO;

@RestController
@RequestMapping("/api/fipe")
public class FipeController {

    private final FipeClient fipeClient;

    public FipeController(FipeClient fipeClient) {
        this.fipeClient = fipeClient;
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<FipeResponseDTO>> getMarcas() {
        List<FipeResponseDTO> marcas = fipeClient.getMarcas();
        if (marcas == null || marcas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(marcas);
    }

    @GetMapping("/{codigo}/modelos")
    public ResponseEntity<FipeModelosResponseDTO> getModelos(@PathVariable("codigo") String codigoMarca) {
        FipeModelosResponseDTO modelos = fipeClient.getModelos(codigoMarca);
        if (modelos == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(modelos);
    }

    @GetMapping("/{codigo}/modelos/{modeloCodigo}/anos")
    public ResponseEntity<List<FipeAnoDTO>> getAnos(@PathVariable("codigo") String codigoMarca, @PathVariable("modeloCodigo") String modeloCodigo) {
        List<FipeAnoDTO> anos = fipeClient.getAnos(codigoMarca, modeloCodigo);
        if (anos == null || anos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(anos);
    }

    @GetMapping("/{codigo}/modelos/{modeloCodigo}/anos/{anoCodigo}")
    public ResponseEntity<FipeVeiculoDTO> getVeiculo(@PathVariable("codigo") String codigoMarca,
                                                      @PathVariable("modeloCodigo") String modeloCodigo,
                                                      @PathVariable("anoCodigo") String anoCodigo) {
        FipeVeiculoDTO veiculo = fipeClient.getVeiculo(codigoMarca, modeloCodigo, anoCodigo);
        if (veiculo == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(veiculo);
    }
}
