package br.edu.infnet.juliopedidoapi.model.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.CarrosService;

@RestController
@RequestMapping("/api/veiculos")
public class CarrosController {

    private final CarrosService carrosService;

    public CarrosController(CarrosService carrosService) {
        this.carrosService = carrosService;
    }

    // Obter todos os fabricantes de um tipo de veículo (carros, motos, caminhões)
    @GetMapping("/{vehicleType}/marcas")
    public ResponseEntity<List<CarrosRetornoQueryResult>> listarMarcas(@PathVariable String vehicleType) {
        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        return ResponseEntity.ok(carrosService.listarFabricantes(vehicleType));
    }

    // Lista de modelos por nome do fabricante
    @GetMapping("/{vehicleType}/marcas/nome/{brandName}/modelos")
    public ResponseEntity<?> modelosPorNomeDeMarca(
            @PathVariable String vehicleType,
            @PathVariable String brandName) {

        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        validarParametro(brandName, "Nome do fabricante não pode ser nulo ou vazio");

        try {
            return ResponseEntity.ok(carrosService.obterModeloPorFabricante(vehicleType, brandName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Marca não encontrada: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    // Lista anos disponíveis para um modelo (aceitando nomes em vez de IDs)
    @GetMapping("/{vehicleType}/marcas/nome/{brandName}/modelos/nome/{modelName}/anos")
    public ResponseEntity<?> anosPorModelo(
            @PathVariable String vehicleType,
            @PathVariable String brandName,
            @PathVariable String modelName) {

        validarParametro(vehicleType, "Tipo de veículo não pode ser nulo ou vazio");
        validarParametro(brandName, "Nome do fabricante não pode ser nulo ou vazio");
        validarParametro(modelName, "Nome do modelo não pode ser nulo ou vazio");

        try {
            List<CarrosRetornoQueryResult> anos = carrosService.obterAnosPorModelo(vehicleType, brandName, modelName);
            return ResponseEntity.ok(anos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Marca ou modelo não encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    private void validarParametro(String parametro, String mensagemErro) {
        if (!StringUtils.hasText(parametro)) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }
}