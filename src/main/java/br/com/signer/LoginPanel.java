package br.com.signer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.signer.listener.LoginListener;
import br.com.signer.listener.event.LoginEvent;
import br.com.signer.model.CredentialModel;
import br.com.signer.model.PrescriptionModel;

public class LoginPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(LoginPanel.class);

	private PreferencesManager preferencesManager = PreferencesManager.getInstance();
	private ApiClient apiClient = new ApiClient();

	private JLabel instanceLabel;
	private JTextField instanceField;

	private JLabel userLabel;
	private JTextField userFiled;

	private JLabel passwordLabel;
	private JPasswordField passwordField;

	private JButton authenticateButton;

	private LoginListener loginListener;

	private ProgressDialog progressDialog;

	public LoginPanel(JFrame parent) {
		super();

		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);

		CredentialModel credential = this.preferencesManager.getCredential();

		this.instanceLabel = new JLabel("Instância:");
		this.instanceField = new JTextField(credential.getInstance());
		this.instanceField.setPreferredSize(new Dimension(200, 25));
		this.instanceField.setBackground(new Color(231, 241, 254));

		this.userLabel = new JLabel("Usuário:");
		this.userFiled = new JTextField(credential.getDocument());
		this.userFiled.setPreferredSize(new Dimension(200, 25));
		this.userFiled.setBackground(new Color(231, 241, 254));

		this.passwordLabel = new JLabel("Senha:");
		this.passwordField = new JPasswordField();
		this.passwordField.setPreferredSize(new Dimension(200, 25));
		this.passwordField.setBackground(new Color(231, 241, 254));

		this.authenticateButton = new JButton("Autenticar");
		this.authenticateButton.setBackground(new Color(52, 71, 72));
		this.authenticateButton.setForeground(Color.WHITE);

		this.progressDialog = new ProgressDialog(parent);

		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.insets = new Insets(5, 5, 5, 5);

		add(this.instanceLabel, gc);

		gc.gridx = 0;
		gc.gridy = 1;

		add(this.instanceField, gc);

		gc.gridx = 1;
		gc.gridy = 0;

		add(this.userLabel, gc);

		gc.gridx = 1;
		gc.gridy = 1;

		add(this.userFiled, gc);

		gc.gridx = 2;
		gc.gridy = 0;

		add(this.passwordLabel, gc);

		gc.gridx = 2;
		gc.gridy = 1;

		add(this.passwordField, gc);

		gc.gridx = 3;
		gc.gridy = 0;
		gc.gridheight = 2;
		gc.anchor = GridBagConstraints.LAST_LINE_END;

		add(this.authenticateButton, gc);

		Border innerBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		this.authenticateButton.addActionListener(event -> {

			if (StringUtils.isBlank(this.instanceField.getText())) {
				this.instanceLabel.setForeground(Color.RED);
			} else {
				this.instanceLabel.setForeground(null);
			}

			if (StringUtils.isBlank(this.userFiled.getText())) {
				this.userLabel.setForeground(Color.RED);
			} else {
				this.userLabel.setForeground(null);
			}

			if (this.passwordField.getPassword().length == 0) {
				this.passwordLabel.setForeground(Color.RED);
			} else {
				this.passwordLabel.setForeground(null);
			}

			if (StringUtils.isNotBlank(this.instanceField.getText()) && StringUtils.isNotBlank(this.userFiled.getText()) && this.passwordField.getPassword().length != 0) {
				this.progressDialog.setVisible(Boolean.TRUE);
				this.authenticate(new CredentialModel(this.instanceField.getText(), this.userFiled.getText(), new String(this.passwordField.getPassword())));
			}

		});
	}

	private void authenticate(CredentialModel credential) {
		SwingWorker<List<PrescriptionModel>, Void> worker = new SwingWorker<>() {

			@Override
			protected List<PrescriptionModel> doInBackground() throws Exception {
				return apiClient.getFiles(credential);
			}

			@Override
			protected void done() {
				try {
					List<PrescriptionModel> prescriptions = get();
					preferencesManager.addCredential(credential);

					progressDialog.setVisible(Boolean.FALSE);
					loginListener.loginSuccess(new LoginEvent(credential, prescriptions));
				} catch (Exception ex) {
					LOGGER.info("User authentication has fail. Reason: {}", ex.getCause().getLocalizedMessage());

					progressDialog.setVisible(Boolean.FALSE);
					loginListener.loginFail(new LoginEvent(ex.getCause().getLocalizedMessage()));
				}
			}

		};

		worker.execute();
	}

	public void setFocus() {
		if (StringUtils.isBlank(this.instanceField.getText())) {
			this.instanceField.requestFocus();
		} else {
			this.passwordField.requestFocus();
		}
	}

	public void addLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

}
