package br.com.signer;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import br.com.signer.listener.CertConfigListener;
import br.com.signer.util.Utils;

public class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private CertInfoPanel certInfoPanel;
	private CertConfigPanel certConfigPanel;

	public ConfigDialog(JFrame parent) {
		super(parent, "Configuração de Certificados A1/A3", Boolean.TRUE);

		setLayout(new BorderLayout());

		this.certInfoPanel = new CertInfoPanel();
		this.certConfigPanel = new CertConfigPanel();

		add(this.certInfoPanel, BorderLayout.PAGE_START);
		add(this.certConfigPanel, BorderLayout.CENTER);

		setSize(650, 400);
		setResizable(Boolean.FALSE);

		Utils.centralizeWindow(this);

		this.certConfigPanel.addCertConfigListener(new CertConfigListener() {
			
			@Override
			public void configSelected(String text) {
				certInfoPanel.setText(text);
			}

		});
	}

}
