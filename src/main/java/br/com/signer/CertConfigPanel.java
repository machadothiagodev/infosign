package br.com.signer;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import br.com.signer.listener.CertConfigListener;
import br.com.signer.listener.FileChooserListener;
import br.com.signer.listener.event.CertAddedEvent;
import br.com.signer.listener.event.CertRemovedEvent;
import br.com.signer.listener.event.CertSelectedEvent;
import br.com.signer.model.CertificateFileModel;

public class CertConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabPane;
	private FileChooserPanel certA1FileChooserPanel;
	private FileChooserPanel certA3FileChooserPanel;

	private PreferencesManager preferencesManager;

	private CertConfigListener certConfigListener;

	public CertConfigPanel() {
		super();

		this.preferencesManager = PreferencesManager.getInstance();

		setLayout(new BorderLayout());

		this.certA1FileChooserPanel = new FileChooserPanel(CertTypeEnum.A1, new String[] { "Certificado" }, 
				this.preferencesManager.getCertFiles(CertTypeEnum.A1), "Certificado (*.pfx, *.p12)", new String[] { "pfx", "p12" });

		this.certA3FileChooserPanel = new FileChooserPanel(CertTypeEnum.A3, new String[] { "Driver" }, 
				this.preferencesManager.getCertFiles(CertTypeEnum.A3), "Driver (*.dll, *.dylib)", new String[] { "dll", "dylib" });

		this.tabPane = new JTabbedPane();
		this.tabPane.addTab(CertTypeEnum.A1.name(), this.certA1FileChooserPanel);
		this.tabPane.addTab(CertTypeEnum.A3.name(), this.certA3FileChooserPanel);

		add(this.tabPane, BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

		this.certA1FileChooserPanel.addFileChooserListener(new FileChooserListener() {

			@Override
			public void certAdded(CertAddedEvent event) {
				preferencesManager.addCertFile(new CertificateFileModel(event.getFileName(), CertTypeEnum.A1));
			}

			@Override
			public void certRemoved(CertRemovedEvent event) {
				preferencesManager.removeCertFile(event.getIndex(), CertTypeEnum.A1);
			}

			@Override
			public void certSelected(CertSelectedEvent event) {
				certConfigListener.configSelected(event.getText());
			}

		});

		this.certA3FileChooserPanel.addFileChooserListener(new FileChooserListener() {

			@Override
			public void certAdded(CertAddedEvent event) {
				preferencesManager.addCertFile(new CertificateFileModel(event.getFileName(), CertTypeEnum.A3));
			}

			@Override
			public void certRemoved(CertRemovedEvent event) {
				preferencesManager.removeCertFile(event.getIndex(), CertTypeEnum.A3);
			}

			@Override
			public void certSelected(CertSelectedEvent event) {
				certConfigListener.configSelected(event.getText());
			}

		});
	}

	public void addCertConfigListener(CertConfigListener certConfigListener) {
		this.certConfigListener = certConfigListener;
	}

}
