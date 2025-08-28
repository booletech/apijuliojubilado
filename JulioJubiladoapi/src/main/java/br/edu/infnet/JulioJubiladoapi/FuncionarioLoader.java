package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;


@Component
@Order(2)
public class FuncionarioLoader implements ApplicationRunner {
	
	private final FuncionarioService funcionarioService;
	public FuncionarioLoader(FuncionarioService funcionarioService) {
		this.funcionarioService = funcionarioService;

	}
	
	
	public static void main(String[] args) {	
		
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		FileReader arquivo = new FileReader("funcionario.txt");
		BufferedReader leitura = new BufferedReader(arquivo);
		String linha = leitura.readLine();
		String [] campos = null;
		
		
		
		while(linha != null) {
			
			campos = linha.split(";");
			
			//endereco
			Endereco endereco = new Endereco();
            endereco.setCep(campos[7]);
            endereco.setLogradouro(campos[8]);
            endereco.setComplemento(campos[9]);   // opcional
            endereco.setNumero(campos[10]);      // opcional
            endereco.setBairro(campos[11]);
            endereco.setLocalidade(campos[12]);
            endereco.setUf(campos[13]);
            endereco.setEstado(campos[14]);

			
            Funcionario funcionario = new Funcionario();
            funcionario.setNome(campos[0]);
            funcionario.setCpf(campos[1]);
            funcionario.setEmail(campos[2]);
            funcionario.setTelefone(campos[3]);

            funcionario.setTurno(campos[4]);
            funcionario.setEscolaridade(campos[5]);
            funcionario.setDataNascimento(campos[6]);

            funcionario.setAtivo(Boolean.valueOf(campos[15]));
            funcionario.setDataContratacao(campos[16]);
            funcionario.setDataDemissao(campos[17]); 
            funcionario.setSalario(Double.valueOf(campos[18]));
            funcionario.setCargo(campos[19]);

            
			funcionario.setEndereco(endereco);
					
			
			try {
			
				funcionarioService.incluir(funcionario); 
				
			} catch (FuncionarioInvalidoException e) {
				System.err.println("Problema na inclusão do funcionario:" + e.getMessage());
			} catch (Exception e) {
				System.err.println("Deu Erro!" + e.getMessage());
			}	
		
			linha = leitura.readLine();
		
		}
		
		//consulta a todos os funcionarios
		List<Funcionario> funcionarios = funcionarioService.obterLista();
		funcionarios.forEach(System.out::println);
		
		
		
		leitura.close();
	}
}

