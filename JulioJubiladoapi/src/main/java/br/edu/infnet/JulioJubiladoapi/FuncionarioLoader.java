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
			endereco.setCep(campos[7]);
			endereco.setLocalidade(campos[8]);
			
			
			Funcionario funcionario = new Funcionario(); 
			funcionario.setNome(campos[0]);
			funcionario.setCpf(campos[1]);
			funcionario.setEmail(campos[2]);
			funcionario.setTelefone(campos[3]);
			funcionario.setTurno(campos[4]);
			funcionario.setEscolaridade(campos[5]);
			funcionario.setDataNascimento(campos[6]);
			
			funcionario.setEstaAtivo(Boolean.valueOf(campos[9]));
			funcionario.setDataContratacao(campos[10]);
			funcionario.setDataDemissao(campos[11]);
			funcionario.setSalario(Integer.valueOf(campos[12]));
			funcionario.setCargo(campos[13]);
			
			
			
			
			funcionario.setEndereco(endereco);
		
			funcionarioService.incluir(funcionario); //grava dentro do mapa (recebe um vendedor)
			System.out.println(funcionario);//imprime na tela o Funcionario completo
			
			
			
			linha = leitura.readLine();
		}		
		
		
		//TODO CHAMADA DA FUNCIONALIDADE DE ALTERAÇÃO
		
		
		//exibir todos que temos dentro do mapa (quantidade)
		System.out.println("-> " + funcionarioService.obterLista().size());
		
		
		leitura.close();
	}
}
