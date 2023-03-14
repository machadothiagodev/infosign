package br.com.signer.listener.event;

import java.util.EventObject;

public class CertRemovedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int index;

	public CertRemovedEvent(Object source, int index) {
		super(source);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
