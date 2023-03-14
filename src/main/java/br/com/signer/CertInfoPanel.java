package br.com.signer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

public class CertInfoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextPane infoTextPane;

	public CertInfoPanel() {
		super();

		setLayout(new BorderLayout());

		this.infoTextPane = new JTextPane();
		this.infoTextPane.setEditable(Boolean.FALSE);

		add(new JScrollPane(this.infoTextPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

		Border innerBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Informações do Certificado");
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		Dimension dimension = getPreferredSize();
		dimension.height = 160;

		setPreferredSize(dimension);
	}

	public void setText(String text) {
		this.infoTextPane.setContentType(text != null ? "text/html" : StringUtils.EMPTY);
		this.infoTextPane.setText(text);
		this.infoTextPane.setCaretPosition(0);
	}

}
