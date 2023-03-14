package br.com.signer.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaDto {

	private Boolean success;
	private String error;
	private String instancia;
	private List<PacienteDto> pacientes;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getInstancia() {
		return instancia;
	}

	public void setInstancia(String instancia) {
		this.instancia = instancia;
	}

	public List<PacienteDto> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<PacienteDto> pacientes) {
		this.pacientes = pacientes;
	}

}
