package br.com.signer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

public class MessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel messageLabel;

	public MessagePanel() {
		super();

		setName("messagePanel");

		ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/icons/logo-310.png"));

		this.messageLabel = new JLabel(StringUtils.EMPTY, imageIcon, SwingConstants.LEFT);
		this.messageLabel.setHorizontalAlignment(JLabel.CENTER);

		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		add(this.messageLabel, BorderLayout.CENTER);

		Border innerBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
	}

}
