package br.com.signer;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import br.com.signer.exception.SignatureCancelException;

import javax.swing.JLabel;

public class PasswordCallbackHandler implements CallbackHandler {

	private JPasswordField passwordField = new JPasswordField();

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof PasswordCallback) {
				PasswordCallback passwordCallback = (PasswordCallback) callback;
				int returnOption = JOptionPane.showConfirmDialog(null, new Object[]{new JLabel("Senha:"), this.passwordField}, 
						null, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

				if (returnOption == JOptionPane.OK_OPTION) {
					passwordCallback.setPassword(this.passwordField.getPassword());
				} else {
					throw new SignatureCancelException();
				}
				
			}
		}
	}

}
