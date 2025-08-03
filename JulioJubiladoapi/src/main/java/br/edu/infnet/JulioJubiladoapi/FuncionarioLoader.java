package br.edu.infnet.JulioJubiladoapi;

import java.io.BufferedReader;
import java.io.FileReader;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.service.FuncionarioService;


@Component
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
			
			Endereco endereco = new Endereco();
			endereco.setCep("1234567");
			endereco.setLocalidade("Rio de janeiro");
			
			
			
			
			
			Funcionario funcionario = new Funcionario();
			funcionario.setNome(campos[0]);
			funcionario.setEstaAtivo(Boolean.valueOf(campos[1]));
			funcionario.setDataContratacao(campos[2]);
			funcionario.setSalario(Integer.valueOf(campos[3]));
			
			funcionario.setEndereco(endereco);
		
			funcionarioService.salvar(funcionario); //grava dentro do mapa
			//imprime na tela o Funcionario completo
			System.out.println(funcionario);
			
			
			
			linha = leitura.readLine();
		}
		
		//exibir todos que temos dentro do mapa (quantidade)
		System.out.println("-> " + funcionarioService.obterLista().size());
		
		
		leitura.close();
	}
}
