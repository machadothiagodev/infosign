package br.com.signer.listener;

import br.com.signer.listener.event.CertAddedEvent;
import br.com.signer.listener.event.CertRemovedEvent;
import br.com.signer.listener.event.CertSelectedEvent;

public interface FileChooserListener {
	
	public void certAdded(CertAddedEvent event);

	public void certRemoved(CertRemovedEvent event);

	public void certSelected(CertSelectedEvent event);

}
