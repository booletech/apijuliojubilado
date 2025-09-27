package br.edu.infnet.mono.model.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

import br.edu.infnet.mono.model.dto.FipeMarcaDTO;

@FeignClient(name = "fipe", url = "${fipe.url}")
public interface FipeClient {
    @GetMapping
    List<FipeMarcaDTO> getMarcas();

    @GetMapping("/{codigo}/modelos")
    Object getModelos(@PathVariable("codigo") String codigoMarca);
}