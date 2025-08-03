 package br.edu.infnet.JulioJubiladoapi.model.service;

 import java.util.List;


//T - entidade
// ID - identificador (estará numa possivel tabela (chave primaria)
public interface CrudService <T, ID> {
	
	//salvar - entra com a Entidade T e depois devolve atualizada (T entity)
	T salvar(T entity);
	
	//obter - retorna uma entidade T
	T obter();
	
	//excluir - recebe a chave primaria e não retorna nada void (por enquanto...)
	void excluir(ID id);
	
	//obter lista retorna uma lista de entidades
	List<T> obterLista();
	
	
	//Aqui podemos criar mais : ObterPorId
}
