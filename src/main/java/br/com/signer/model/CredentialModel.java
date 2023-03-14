package br.com.signer.model;

public class CredentialModel {

	private String instance;
	private String document;
	private String password;

	public CredentialModel(String instance, String document, String password) {
		super();
		this.instance = instance;
		this.document = document;
		this.password = password;
	}

	public String getInstance() {
		return instance;
	}

	public String getDocument() {
		return document;
	}

	public String getPassword() {
		return password;
	}

}
