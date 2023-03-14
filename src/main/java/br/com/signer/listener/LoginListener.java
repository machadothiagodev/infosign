package br.com.signer.listener;

import br.com.signer.listener.event.LoginEvent;

public interface LoginListener {

	public void loginSuccess(LoginEvent event);
	public void loginFail(LoginEvent event);

}
