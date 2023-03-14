package br.com.signer;

import java.awt.Color;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VersionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(VersionPanel.class);

	private JLabel versionLabel;

	public VersionPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBackground(Color.WHITE);

		
		this.versionLabel = new JLabel(this.getVersion());

		add(Box.createHorizontalGlue());
		add(this.versionLabel);
		add(Box.createHorizontalGlue());
	}

	private String getVersion() {
		String version = null;
		final Properties properties = new Properties();

		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
			version = "Versão " + properties.getProperty("version");
		} catch (IOException ex) {
			LOGGER.error("Fail to load version off app", ex);
		}

		return version;
	}

}
