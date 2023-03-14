package br.com.signer.listener.event;

public class CertSelectedEvent {

	private String text;

	public CertSelectedEvent(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
