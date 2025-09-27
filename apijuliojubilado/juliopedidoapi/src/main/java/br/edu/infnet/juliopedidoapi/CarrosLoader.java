package br.edu.infnet.juliopedidoapi;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.infnet.juliopedidoapi.model.domain.CarrosRetornoQueryResult;
import br.edu.infnet.juliopedidoapi.model.service.CarrosService;

@Component
public class CarrosLoader implements ApplicationRunner {

    private final CarrosService carrosService;

    public CarrosLoader(CarrosService carrosService) {
        this.carrosService = carrosService;
    }

    @Override
    public void run(ApplicationArguments args) {
        String vehicleType = "cars";
        String brandId = "1";       // exemplo
        String modelName = "NSX 3.0"; // exemplo

  	  	// 1) Modelos por marca (resolve pelo ID)
        try {
            CarrosRetornoQueryResult retorno1 = carrosService.obterModeloPorFabricante(vehicleType, brandId);
            List<String> modelos = (retorno1 != null) ? retorno1.getListadeveiculos() : null;
            System.out.println("[CARROS] Modelos por marca " + brandId + ": "
                    + (modelos != null ? modelos : "sem resultados"));
        } catch (Exception e) {
            System.out.println("[CARROS] Falha ao buscar modelos por marca " + brandId + ": " + e.getMessage());
        }

        // 2) Anos do modelo (resolve pelo NOME → busca modelId internamente)
        try {
            List<CarrosRetornoQueryResult> anos = carrosService
                    .obterAnosPorModeloNome(vehicleType, brandId, modelName);

            List<String> anosFormatados = (anos == null) ? null
                    : anos.stream()
                          .map(a -> a.getCode() + " (" + a.getAno() + ")")
                          .toList();

            System.out.println("[CARROS] Anos disponíveis para o modelo '" + modelName + "': "
                    + (anosFormatados != null ? anosFormatados : "sem resultados"));
        } catch (Exception e) {
            System.out.println("[CARROS] Falha ao buscar anos do modelo '" + modelName + "': " + e.getMessage());
      }

   }
}