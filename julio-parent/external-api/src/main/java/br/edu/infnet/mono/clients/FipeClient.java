package br.edu.infnet.mono.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.edu.infnet.mono.model.dto.FipeAnoDTO;
import br.edu.infnet.mono.model.dto.FipeModelosResponseDTO;
import br.edu.infnet.mono.model.dto.FipeResponseDTO;
import br.edu.infnet.mono.model.dto.FipeVeiculoDTO;

@FeignClient(name = "fipe", url = "${api.fipe.url}")
public interface FipeClient {
    @GetMapping
    List<FipeResponseDTO> getMarcas();

    @GetMapping("/{codigo}/modelos")
    FipeModelosResponseDTO getModelos(@PathVariable("codigo") String codigoMarca);

    @GetMapping("/{codigo}/modelos/{codigoModelo}/anos")
    List<FipeAnoDTO> getAnos(@PathVariable("codigo") String codigoMarca, @PathVariable("codigoModelo") String codigoModelo);

    @GetMapping("/{codigo}/modelos/{codigoModelo}/anos/{codigoAno}")
    FipeVeiculoDTO getVeiculo(@PathVariable("codigo") String codigoMarca,
                               @PathVariable("codigoModelo") String codigoModelo,
                               @PathVariable("codigoAno") String codigoAno);
}