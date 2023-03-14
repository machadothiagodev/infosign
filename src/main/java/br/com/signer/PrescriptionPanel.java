package br.com.signer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.signer.exception.SignatureCancelException;
import br.com.signer.model.CertificateFileModel;
import br.com.signer.model.CredentialModel;
import br.com.signer.model.PrescriptionModel;

public class PrescriptionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(PrescriptionPanel.class);
	
	private SignService signService = new SignService();
	private ApiClient apiClient = new ApiClient();

	private JTable prescriptionTable;
	private JButton updateButton;
	private JButton signButton;

	private ProgressDialog progressDialog;

	private CredentialModel credential;

	public PrescriptionPanel(JFrame parent) {
		super();

		setName("prescriptionPanel");

		this.prescriptionTable = new JTable();
		this.prescriptionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		this.updateButton = new JButton("Atualizar");
		this.updateButton.setBackground(new Color(52, 71, 72));
		this.updateButton.setForeground(Color.WHITE);
		
		this.signButton = new JButton("Assinar");
		this.signButton.setBackground(new Color(52, 71, 72));
		this.signButton.setForeground(Color.WHITE);

		this.progressDialog = new ProgressDialog(parent);

		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);

		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(5, 5, 5, 5);

		add(new JScrollPane(this.prescriptionTable), gc);

		gc.gridx = 0;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LAST_LINE_START;

		add(this.updateButton, gc);

		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LAST_LINE_END;

		add(this.signButton, gc);

		Border innerBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		updateButton.addActionListener(event -> {
			
		});

		signButton.addActionListener(event -> {
			ListSelectionModel listSelectionModel = this.prescriptionTable.getSelectionModel();
			if (listSelectionModel.isSelectionEmpty()) {
				JOptionPane.showMessageDialog(null, "Necessário selecionar pelo menos um receituário para assinatura", null, JOptionPane.WARNING_MESSAGE);
			} else {
				List<CertificateFileModel> certFiles = PreferencesManager.getInstance().getCertFiles();

				if (certFiles.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Nenhum certificado cadastrado", null, JOptionPane.WARNING_MESSAGE);
				} else {
					CertificateFileModel selectedCertFile = null;
			
					if (certFiles.size() == 1) {
						selectedCertFile = certFiles.get(0);
					} else {
						selectedCertFile = (CertificateFileModel)JOptionPane.showInputDialog(null, "Escolha um certificado", null, JOptionPane.INFORMATION_MESSAGE, null, certFiles.toArray(), null);
					}

					if (selectedCertFile != null) {
						List<PrescriptionModel> selectedPrescriptions = new ArrayList<>();

						int minSelectionIndex = listSelectionModel.getMinSelectionIndex();
						int maxSelectionIndex = listSelectionModel.getMaxSelectionIndex();

						for (int i = minSelectionIndex; i <= maxSelectionIndex; i++) {
							selectedPrescriptions.add(((PrescriptionTableModel)prescriptionTable.getModel()).getTableItems().get(i));
						}

						this.progressDialog.setVisible(Boolean.TRUE);
						this.sign(selectedCertFile, selectedPrescriptions);
					}
				}
			}
		});
	}

	private void sign(CertificateFileModel certFile, List<PrescriptionModel> selectedPrescriptions) {
		SwingWorker<Void, Void> worker = new SwingWorker<>() {

			@Override
			protected Void doInBackground() throws Exception {
				KeyStore keyStore = new KeyStoreManager().getKeyStore(certFile.getType(), new File(certFile.getFilePath()));
				String alias = keyStore.aliases().nextElement();

				signService.sign((PrivateKey) keyStore.getKey(alias, null), keyStore.getProvider(),
						keyStore.getCertificateChain(alias), credential.getInstance(), selectedPrescriptions);

				return null;
			}

			@Override
			protected void done() {
				try {
					get();

					progressDialog.setVisible(Boolean.FALSE);
					JOptionPane.showMessageDialog(null, "Operação realizada com sucesso", null, JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					if (ExceptionUtils.getRootCause(ex) instanceof SignatureCancelException) {
						progressDialog.setVisible(Boolean.FALSE);
					} else {
						LOGGER.error("Process of sign documents has fail", ex);

						progressDialog.setVisible(Boolean.FALSE);
						JOptionPane.showMessageDialog(null, "Processo de assinatura falhou. Verifique os logs da aplicação para maiores detalhes.", null, JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

				progressDialog.setVisible(Boolean.TRUE);
				refreshTableModel();
			}

		};

		worker.execute();
	}

	private void refreshTableModel() {
		SwingWorker<List<PrescriptionModel>, Void> worker = new SwingWorker<>() {

			@Override
			protected List<PrescriptionModel> doInBackground() throws Exception {
				return apiClient.getFiles(credential);
			}

			@Override
			protected void done() {
				try {
					setTableModel(get());
				} catch (Exception ex) {
					LOGGER.info("User authentication has fail. Reason: {}", ex.getLocalizedMessage());
				} finally {
					progressDialog.setVisible(Boolean.FALSE);
				}
			}

		};

		worker.execute();
	}

	public void setTableModel(List<PrescriptionModel> prescriptions) {
		PrescriptionTableModel model = new PrescriptionTableModel();
		model.setTableItems(prescriptions);

		this.prescriptionTable.setModel(model);
	}

	public void setCredential(CredentialModel credential) {
		this.credential = credential;
	}

}
