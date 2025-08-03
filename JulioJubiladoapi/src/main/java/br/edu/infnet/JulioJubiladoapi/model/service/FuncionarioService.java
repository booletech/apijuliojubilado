package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Endereco;
import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;


//Funcionario service implementa CrudService. Necessario a entidade T  o tipo da chave que representa o objeto
@Service
public class FuncionarioService implements CrudService<Funcionario, Integer>{
	
	
	//no Map K deve ser uma classe wrapper (Integer, Long, Float, Boolean, String, Classe_criada
	// Vamos usar Integer e Funcionario e chamaremos e mapa:
	private final Map<Integer, Funcionario> mapa = new ConcurrentHashMap<Integer, Funcionario>();
	private final AtomicInteger nextId = new AtomicInteger(1); //joga o id para o funcionario e depois pega id:func pra tela (getandincrement)
	
	
	@Override
	public Funcionario salvar(Funcionario funcionario) {
		
		funcionario.setId(nextId.getAndIncrement());
		mapa.put(funcionario.getId(), funcionario);
		
		
		return funcionario;
	}

	@Override
	public Funcionario obter() {
		
		//dados mockados	
		Endereco endereco = new Endereco();
		endereco.setCep("1234567");
		endereco.setLocalidade("Rio de janeiro");
		
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Julio Cesar");
		funcionario.setEstaAtivo(true);
		funcionario.setDataContratacao("20/08/1992");
		funcionario.setSalario(9999);
		
		funcionario.setEndereco(endereco);

		return funcionario;
	}

	@Override
	public void excluir(Integer id) {
		//
		mapa.remove(id);
		
	}

	@Override
	public List<Funcionario> obterLista() {
		// utiliza values em mapa; arraylist (semelhante ao map/hashmap);
		// Retorna uma lista (ArrayList<Funcionario>) passa os dados (mapa.values() e retorna uma coleção  List<Funcionario>
		return new ArrayList<Funcionario>(mapa.values());
	}
	
}
