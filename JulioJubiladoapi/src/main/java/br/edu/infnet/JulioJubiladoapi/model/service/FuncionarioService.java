package br.edu.infnet.JulioJubiladoapi.model.service;


import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioInvalidoException;
import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.FuncionarioNaoEncontradoException;
import br.edu.infnet.JulioJubiladoapi.model.repository.FuncionarioRepository;
import jakarta.transaction.Transactional;


@Service
public class FuncionarioService implements CrudService<Funcionario, Integer>{
	
	private final FuncionarioRepository funcionarioRepository;
	
	public FuncionarioService(FuncionarioRepository funcionarioRepository) {
		this.funcionarioRepository = funcionarioRepository;
	}
	
	
	


	private void validar(Funcionario funcionario) {
		if(funcionario == null) {
			throw new IllegalArgumentException("O funcionario não pode estar nulo e precisa ser criado!");
		}
		if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
			throw new FuncionarioInvalidoException("O nome do funcionario é uma informação obrigatória!");
			
		}
		
	}
	
	
	
	
	
	@Override
	@Transactional
	public Funcionario incluir(Funcionario funcionario) {
		validar(funcionario);
		if(funcionario.getId() != null && funcionario.getId() > 0) {
			throw new IllegalArgumentException("Um novo funcionario não pode ter um Id na inclusão!");
				
		}
		
		return funcionarioRepository.save(funcionario);
	}

	
	
	
	
	
	@Override
	@Transactional
	public Funcionario alterar(Integer id, Funcionario funcionario) {
		validar(funcionario);
		
		if(id == null || id == 0) {
			throw new IllegalArgumentException("O ID para alteração não pode ser nulo/zero");
			
		}
		
		obterPorId(id);
		funcionario.setId(id);
		return funcionarioRepository.save(funcionario);
		
	
		}

	
	
	
	
	@Override
	@Transactional
	public void excluir(Integer id) {
	
		Funcionario funcionario = obterPorId(id);
		funcionarioRepository.delete(funcionario);
		
		
	}
	
	
	
	@Transactional
	public Funcionario inativar(Integer id ) {

		if(id == null || id == 0) {
			throw new IllegalArgumentException("O ID para inativação não pode ser nulo/zero");
		}
		
		Funcionario funcionario = obterPorId(id);
		
		if(!funcionario.isAtivo()) {
			System.out.println("Funcionario"+ funcionario.getNome() +" já está inativo!");
			return funcionario;
		}
		
		funcionario.setAtivo(false);
		return funcionarioRepository.save(funcionario);
		
	}
	
	
	
	
	@Override
	public List<Funcionario> obterLista() {
	
		return funcionarioRepository.findAll();
	
	}	
	
	
	
	
	
	@Override
	public Funcionario obterPorId(Integer id) {
		if(id == null || id <= 0) {
			throw new IllegalArgumentException("O ID para EXCLUSÃO não pode ser NULO/ZERO!");
		}
		
		return funcionarioRepository.findById(id).orElseThrow(() -> 
		new FuncionarioNaoEncontradoException("O funcionario com id "+id +" não foi encontrado"));
		
	}
}
