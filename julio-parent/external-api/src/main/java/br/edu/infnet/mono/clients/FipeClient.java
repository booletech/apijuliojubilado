package br.edu.infnet.mono.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import br.edu.infnet.mono.model.dto.*;
import java.util.List;
import java.util.Optional;

@FeignClient(name = "fipe", url = "https://parallelum.com.br/fipe/api/v1")
public interface FipeClient {

	@GetMapping("/carros/marcas")
	List<FipeMarcaDTO> obterMarcas();

	@GetMapping("/carros/marcas/{marcaId}/modelos")
	FipeModelosResponseDTO obterModelos(@PathVariable("marcaId") String marcaId);

	@GetMapping("/carros/marcas/{marcaId}/modelos/{modeloId}/anos")
	List<FipeAnoDTO> obterAnos(@PathVariable("marcaId") String marcaId, @PathVariable("modeloId") String modeloId);

	@GetMapping("/carros/marcas/{marcaId}/modelos/{modeloId}/anos/{ano}")
	FipeVeiculoDTO obterDetalhes(@PathVariable("marcaId") String marcaId, @PathVariable("modeloId") String modeloId,
			@PathVariable("ano") String ano);

}