package br.edu.infnet.JulioJubiladoapi.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;


//Funcionario service implementa CrudService. Necessario a entidade T  o tipo da chave que representa o objeto
@Service
public class FuncionarioService implements CrudService<Funcionario, Integer>{
	
	
	private final Map<Integer, Funcionario> mapa = new ConcurrentHashMap<Integer, Funcionario>();
	private final AtomicInteger nextId = new AtomicInteger(1); 
	
	private void validar(Funcionario funcionario) {
		if(funcionario == null) {
			throw new IllegalArgumentException("O funcionario não pode estar nulo e precisa ser criado!");
		}
		if (funcionario.getNome() == null || funcionario.getNome().trim().isBlank()) {
			throw new FuncionarioInvalidoException("O nome do funcionario é uma informação obrigatória!");
			
		}
		
	}
	
	@Override
	public Funcionario incluir(Funcionario funcionario) {
		validar(funcionario);
		//validação mais específica: Um novo funcionario não deve possuir id
		if(funcionario.getId() != null && funcionario.getId() != 0) {
			throw new IllegalArgumentException("Um novo funcionario não pode ter um Id na inclusão!");
				
		}
		
		funcionario.setId(nextId.getAndIncrement());
		mapa.put(funcionario.getId(), funcionario);
		
		
		return funcionario;
	}

	@Override
	public Funcionario alterar(Integer id, Funcionario funcionario) {
		validar(funcionario);
		
		if(id == null || id == 0) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
			
		}
		obterPorId(id);
		
		funcionario.setId(id);
		// substituição do funcionario
		mapa.put(funcionario.getId(), funcionario);
		
		
		return funcionario;
	
		}

	@Override
	public void excluir(Integer id) {
		if(id == null || id == 0) {
			throw new IllegalArgumentException("O ID para EXCLUSÃO não pode ser NULO/ZERO!");
		}
		
		if(!mapa.containsKey(id)) {
			throw new FuncionarioNaoEncontradoException("O funcionario com id"+ id + "não foi encontrado");
		}
		
		mapa.remove(id);
	}
	
	
	//Mudar o campo EhAtivo de True para false
	public Funcionario inativar(Integer id ) {

		if(id == null || id == 0) {
			throw new IllegalArgumentException("O ID para inativação não pode ser nulo/zero");
		}
		Funcionario funcionario = obterPorId(id);
		
		if(!funcionario.isEstaAtivo()) {
			System.out.println("Funcionario"+ funcionario.getNome() +" já está inativo!");
			return funcionario;
		}
		
		funcionario.setEstaAtivo(false);
		mapa.put(funcionario.getId(), funcionario);
		
		return funcionario;
		
	}
	

	@Override
	public List<Funcionario> obterLista() {
		// utiliza values em mapa; arraylist (semelhante ao map/hashmap);
		// Retorna uma lista (ArrayList<Funcionario>) passa os dados (mapa.values() e retorna uma coleção  List<Funcionario>
		return new ArrayList<Funcionario>(mapa.values());
	}
	
	@Override
	public Funcionario obterPorId(Integer id) {
		//através de um Id podemos encontrar o funcionario no mapa
		Funcionario funcionario = mapa.get(id); // Nomeia como funcionario o get pedido 
		
		//verificando se funcionario é null
		if (funcionario == null){
			throw new IllegalArgumentException("Impossivel obter o funcionario pelo Id" + id);
			
		}
		
		return funcionario;
	}
}
