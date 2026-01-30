package br.edu.infnet.juliopedidoapi.model.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.CarrosService;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/veiculos")
@Validated
public class CarrosController {

	private final CarrosService carrosService;

	public CarrosController(CarrosService carrosService) {
		this.carrosService = carrosService;
	}

	@GetMapping("/{vehicleType}/marcas")
	public ResponseEntity<List<CarrosRetornoQueryResult>> listarMarcas(
			@PathVariable
			@NotBlank(message = "Tipo de veiculo nao pode ser nulo ou vazio.")
			String vehicleType) {
		return ResponseEntity.ok(carrosService.listarFabricantes(vehicleType));
	}

	@GetMapping("/{vehicleType}/{brandName}/modelos")
	public ResponseEntity<CarrosRetornoQueryResult> modelosPorNomeDeMarca(
			@PathVariable
			@NotBlank(message = "Tipo de veiculo nao pode ser nulo ou vazio.")
			String vehicleType,
			@PathVariable
			@NotBlank(message = "Nome do fabricante nao pode ser nulo ou vazio.")
			String brandName) {
		return ResponseEntity.ok(carrosService.obterModeloPorFabricante(vehicleType, brandName));
	}

	@GetMapping("/{vehicleType}/{brandName}/{modelName}/anos")
	public ResponseEntity<List<CarrosRetornoQueryResult>> anosPorModelo(
			@PathVariable
			@NotBlank(message = "Tipo de veiculo nao pode ser nulo ou vazio.")
			String vehicleType,
			@PathVariable
			@NotBlank(message = "Nome do fabricante nao pode ser nulo ou vazio.")
			String brandName,
			@PathVariable
			@NotBlank(message = "Nome do modelo nao pode ser nulo ou vazio.")
			String modelName) {
		List<CarrosRetornoQueryResult> anos = carrosService.obterAnosPorModelo(vehicleType, brandName, modelName);
		return ResponseEntity.ok(anos);
	}
}
