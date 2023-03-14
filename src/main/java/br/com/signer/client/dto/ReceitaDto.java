package br.com.signer.client.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReceitaDto {

	private @JsonProperty("id_receituario") String id;
	private @JsonProperty("data_pedido") @JsonFormat(locale = "pt-BR", timezone = "Brazil/East", pattern = "yyyy-MM-dd") Date pedido;
	private String pdf;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getPedido() {
		return pedido;
	}

	public void setPedido(Date pedido) {
		this.pedido = pedido;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

}
