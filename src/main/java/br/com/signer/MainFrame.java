package br.com.signer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Taskbar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.signer.listener.LoginListener;
import br.com.signer.listener.event.LoginEvent;
import br.com.signer.util.Utils;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(MainFrame.class);

	private LoginPanel loginPanel;
	private MessagePanel messagePanel;
	private PrescriptionPanel prescriptionPanel;
	private VersionPanel versionPanel;
	private ConfigDialog configDialog;

	public MainFrame() {
		super("Info Sign");

		try {
			setIconImages(Arrays.asList(
				ImageIO.read(this.getClass().getResourceAsStream("/icons/logo-128.png")),
				ImageIO.read(this.getClass().getResourceAsStream("/icons/logo-64.png")),
				ImageIO.read(this.getClass().getResourceAsStream("/icons/logo-32.png")),
				ImageIO.read(this.getClass().getResourceAsStream("/icons/logo-16.png"))
			));

			if (SystemUtils.IS_OS_MAC_OSX) {
				Taskbar.getTaskbar().setIconImage(getIconImage());
			}
		} catch (IOException ex) {
			LOGGER.error("Fail to image icon", ex);
		}

		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception ex) {
			LOGGER.error("Fail to load and set Nimbus Look and Feel", ex);

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				LOGGER.error("Fail to load and set System Look and Feel", e);
			}
		}

		setJMenuBar(this.createMenuBar());

		setLayout(new BorderLayout());

		this.loginPanel = new LoginPanel(this);
		this.messagePanel = new MessagePanel();
		this.prescriptionPanel = new PrescriptionPanel(this);
		this.versionPanel = new VersionPanel();
		this.configDialog = new ConfigDialog(this);

		add(this.loginPanel, BorderLayout.PAGE_START);

		JPanel overlaidPanel = new JPanel(new CardLayout());

		overlaidPanel.add(this.messagePanel, this.messagePanel.getName());
		overlaidPanel.add(this.prescriptionPanel, this.prescriptionPanel.getName());

		add(overlaidPanel, BorderLayout.CENTER);

		add(this.versionPanel, BorderLayout.PAGE_END);

		setSize(800, 650);

		Utils.centralizeWindow(this);

		setResizable(Boolean.FALSE);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setVisible(Boolean.TRUE);

		this.loginPanel.addLoginListener(new LoginListener() {
			
			@Override
			public void loginSuccess(LoginEvent event) {
				prescriptionPanel.setCredential(event.getCredential());
				prescriptionPanel.setTableModel(event.getPrescriptions());

				if (event.getPrescriptions().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Nenhum receituário encontrado", null, JOptionPane.INFORMATION_MESSAGE);
				}

				((CardLayout)overlaidPanel.getLayout()).show(overlaidPanel, prescriptionPanel.getName());
			}

			@Override
			public void loginFail(LoginEvent event) {
				JOptionPane.showMessageDialog(null, event.getError(), null, JOptionPane.ERROR_MESSAGE);
			}

		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent event) {
				loginPanel.setFocus();
				LOGGER.info("Application has been started");
			}

			@Override
			public void windowClosing(WindowEvent event) {
				LOGGER.info("Application has been ended");
			}

		});
	}

	private JMenuBar createMenuBar() {
		JMenuItem addCertItem = new JMenuItem("Certificado A1/A3");

		JMenu configMenu = new JMenu("Configuração");
		configMenu.add(addCertItem);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(configMenu);

		addCertItem.addActionListener(event -> configDialog.setVisible(Boolean.TRUE));

		return menuBar;
	}

}
