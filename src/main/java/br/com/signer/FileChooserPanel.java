package br.com.signer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.signer.listener.FileChooserListener;
import br.com.signer.listener.event.CertAddedEvent;
import br.com.signer.listener.event.CertRemovedEvent;
import br.com.signer.listener.event.CertSelectedEvent;
import br.com.signer.model.CertificateFileModel;

public class FileChooserPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(FileChooserPanel.class);

	private JTable table;
	private JButton addButton;
	private JButton removeButton;
	private JFileChooser fileChooser;

	private FileChooserListener fileChooserListener;
	
	public FileChooserPanel(CertTypeEnum certType, String[] tableColumnsName, List<CertificateFileModel> items, String fileNameExtensionDescription, String[] fileNamesExtensions) {
		super();
		
		setLayout(new GridBagLayout());		
		
		this.table = new JTable(new CertTableModel(tableColumnsName, items.stream().map(CertificateFileModel::getFilePath).collect(Collectors.toList())));
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.addButton = new JButton("Adicionar");
		this.removeButton = new JButton("Remover");

		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileFilter(new FileNameExtensionFilter(fileNameExtensionDescription, fileNamesExtensions));

		JPanel panel = new JPanel();

		GridLayout layout = new GridLayout(2, 1);
		layout.setVgap(10);

		panel.setLayout(layout);
		panel.add(this.addButton);
		panel.add(this.removeButton);
		
		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 100;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(5, 5, 5, 5);

		add(new JScrollPane(this.table), gc);

		gc.gridx = 1;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(panel, gc);

		this.table.getSelectionModel().addListSelectionListener(event -> {
			if (BooleanUtils.isFalse(event.getValueIsAdjusting()) && ((DefaultListSelectionModel)event.getSource()).getAnchorSelectionIndex() != -1) {
				try {
					KeyStore keyStore = new KeyStoreManager().getKeyStore(certType, new File(((CertTableModel) this.table.getModel()).getItems().get(this.table.getSelectedRow())));
					Enumeration<String> aliases = keyStore.aliases();

					this.fileChooserListener.certSelected(new CertSelectedEvent(this.formatCertInfo((X509Certificate) keyStore.getCertificate(aliases.nextElement()))));
				} catch (Exception ex) {
					this.fileChooserListener.certSelected(new CertSelectedEvent(null));
					JOptionPane.showMessageDialog(null, "Falha ao carregar o driver selecionado. Verifique os logs da aplicação para maiores detalhes.", null, JOptionPane. ERROR_MESSAGE);
					LOGGER.error(StringUtils.EMPTY, ex);
				}
			}
		});

		this.addButton.addActionListener(event -> {
			if (this.fileChooser.showOpenDialog(FileChooserPanel.this) == JFileChooser.APPROVE_OPTION) {
				String fileName = this.fileChooser.getSelectedFile().toString();

				((CertTableModel) this.table.getModel()).addItem(fileName);
				this.fileChooserListener.certAdded(new CertAddedEvent(this, fileName));
			}
		});

		this.removeButton.addActionListener(event -> {
			if (this.table.getSelectedRow() != -1) {
				int selectedRow = this.table.getSelectedRow();

				((CertTableModel) this.table.getModel()).removeItem(selectedRow);
				this.fileChooserListener.certRemoved(new CertRemovedEvent(this, selectedRow));
			}
		});

	}

	public void addFileChooserListener(FileChooserListener fileChooserListener) {
		this.fileChooserListener = fileChooserListener;
	}

	private String formatCertInfo(X509Certificate certificate) {
		StringBuilder info = new StringBuilder();
		
		info.append("<strong>Issued To</strong><br><ul>");
		String[] str1 = certificate.getSubjectDN().getName().split(", ");
		for (int i = 0; i < str1.length; i++) {
			info.append("<li>" + str1[i] + "</li>");
		}

		info.append("</ul>");
		
		info.append("<strong>Issued By</strong><br><ul>");
		String[] str2 = certificate.getIssuerDN().getName().split(", ");
		for (int i = 0; i < str2.length; i++) {
			info.append("<li>" + str2[i] + "</li>");
		}
		
		info.append("</ul>");
		
		info.append("<strong>Validity Period</strong><br><ul>");
		info.append("<li>Issued On=" + certificate.getNotBefore() + "</li>");
		info.append("<li>Expires On=" + certificate.getNotAfter() + "</li>");

		info.append("</ul>");
		
		return info.toString();
	}

}
