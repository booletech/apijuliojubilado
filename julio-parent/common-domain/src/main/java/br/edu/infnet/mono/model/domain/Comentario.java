package br.edu.infnet.mono.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentarios")
public class Comentario {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String texto;
	private String autor;

	public Comentario(String autor, String texto) {
		this.autor = autor;
		this.texto = texto;
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTexto() {
		return texto;
	}


	public void setTexto(String texto) {
		this.texto = texto;
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String autor) {
		this.autor = autor;
	}
}
