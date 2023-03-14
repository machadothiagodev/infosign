package br.com.signer.listener.event;

import java.util.EventObject;

public class CertAddedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private String fileName;

	public CertAddedEvent(Object source, String fileName) {
		super(source);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

}
