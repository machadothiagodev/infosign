package br.com.signer.listener.event;

import java.util.List;

import br.com.signer.model.CredentialModel;
import br.com.signer.model.PrescriptionModel;

public class LoginEvent {

	private CredentialModel credential;
	private List<PrescriptionModel> prescriptions;
	private String error;

	public LoginEvent(CredentialModel credential, List<PrescriptionModel> prescriptions) {
		this.credential = credential;
		this.prescriptions = prescriptions;
	}

	public LoginEvent(String error) {
		this.error = error;
	}

	public CredentialModel getCredential() {
		return credential;
	}

	public List<PrescriptionModel> getPrescriptions() {
		return prescriptions;
	}

	public String getError() {
		return error;
	}

}
