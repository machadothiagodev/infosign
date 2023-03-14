package br.com.signer.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PacienteDto {

	private @JsonProperty("id_paciente") String id;
	private String nome;
	private List<ReceitaDto> receitas;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<ReceitaDto> getReceitas() {
		return receitas;
	}

	public void setReceitas(List<ReceitaDto> receitas) {
		this.receitas = receitas;
	}

}
