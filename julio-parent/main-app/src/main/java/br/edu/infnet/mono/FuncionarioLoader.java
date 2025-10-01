package br.edu.infnet.mono;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.mono.model.dto.FuncionarioRequestDTO;
import br.edu.infnet.mono.service.FuncionarioService;

@Component
@Order(1)
public class FuncionarioLoader implements ApplicationRunner {

	private final FuncionarioService funcionarioService;

	public FuncionarioLoader(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Funcionario.txt");
		if (inputStream == null) {
			throw new FileNotFoundException("Arquivo Funcionario.txt não encontrado no resources!");
		}
		for(FuncionarioRequestDTO funcionario : processarArquivoFuncionarios(inputStream)) {
			System.out.println("# " + funcionario.getNome());
		}
	}

    public List<FuncionarioRequestDTO> processarArquivoFuncionarios(InputStream inputStream) {
        List<FuncionarioRequestDTO> processedFuncionarios = new ArrayList<>();
        try (BufferedReader leitura = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String linha;
            int lineNumber = 0;
            while ((linha = leitura.readLine()) != null) {
                lineNumber++;
                try {
                    FuncionarioRequestDTO dto = parseToFuncionarioDTO(linha);
                    funcionarioService.incluir(dto);
                    processedFuncionarios.add(dto);
                } catch (Exception e) {
                    // Print detailed error so failures are explicit during startup
                    System.err.println("Erro ao processar linha " + lineNumber + " [" + linha + "] - " + e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler o arquivo de funcionários: " + e.getMessage());
            throw new RuntimeException("Falha ao processar o arquivo de funcionários.", e);
        }
        return processedFuncionarios;
    }

    private FuncionarioRequestDTO parseToFuncionarioDTO(String linha) {
        String[] campos = linha.split(";");
        if (campos.length < 21) {
            throw new IllegalArgumentException("Formato de linha inválido. Esperado pelo menos 21 campos separados por ';'.");
        }
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO();
        dto.setNome(campos[0].trim());
        dto.setCpf(campos[1].trim());
        dto.setEmail(campos[2].trim());
        dto.setTelefone(campos[3].trim());
        dto.setTurno(campos[4].trim());
        dto.setEscolaridade(campos[5].trim());
        dto.setDataNascimento(campos[6].trim());
        dto.setCep(campos[7].trim());

     
        dto.setComplemento(campos[9].trim());
        dto.setNumero(Integer.parseInt(campos[10].trim()));

        dto.setAtivo(Boolean.parseBoolean(campos[15].trim()));
        dto.setDataContratacao(campos[16].trim());
        dto.setDataDemissao(campos[17].trim());
        dto.setMatricula(campos[18].trim());
        dto.setSalario(Double.parseDouble(campos[19].trim()));
        dto.setCargo(campos[20].trim());

        return dto;
    }
}