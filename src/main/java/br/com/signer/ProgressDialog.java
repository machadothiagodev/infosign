package br.com.signer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.apache.commons.lang3.StringUtils;

import br.com.signer.util.Utils;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JLabel label;

	public ProgressDialog(JFrame parent) {
		super(parent, StringUtils.EMPTY, Boolean.TRUE);

		setLayout(new BorderLayout());

		ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/icons/loading.gif"));

		this.label = new JLabel("  CARREGANDO...", imageIcon, SwingConstants.LEFT);
		this.label.setHorizontalAlignment(JLabel.CENTER);

		add(this.label, BorderLayout.CENTER);

		setSize(170, 65);
		setResizable(Boolean.FALSE);
		setUndecorated(Boolean.TRUE);
		getContentPane().setBackground(Color.WHITE);
		getRootPane().setBorder(new LineBorder(new Color(52, 71, 72)));

		Utils.centralizeWindow(this);
	}

	@Override
	public void setVisible(final boolean visible) {
		SwingUtilities.invokeLater(() -> ProgressDialog.super.setVisible(visible));
	}

}
