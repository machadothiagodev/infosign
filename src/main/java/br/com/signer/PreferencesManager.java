package br.com.signer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

import br.com.signer.model.CertificateFileModel;
import br.com.signer.model.CredentialModel;

public class PreferencesManager {

	private static PreferencesManager instance;
	private Preferences preferences;

	private PreferencesManager() {
		this.preferences = Preferences.userRoot().node("signer");
	}

	public static PreferencesManager getInstance() {
		if (instance == null) {
			instance = new PreferencesManager();
		}

		return instance;
	}

	public List<CertificateFileModel> getCertFiles() {
		List<CertificateFileModel> certFiles = new LinkedList<>();

		Arrays.asList(CertTypeEnum.values()).forEach(t -> certFiles.addAll(getCertFiles(t)));

		return certFiles;
	}

	public List<CertificateFileModel> getCertFiles(CertTypeEnum type) {
		List<CertificateFileModel> certFiles = new LinkedList<>();
		int size = this.preferences.node(type.name()).getInt("size", 0);

		if (size != 0) {
			for (int i = 0; i < size; i++) {
				certFiles.add(new CertificateFileModel(this.preferences.node(type.name()).get("file_" + i, StringUtils.EMPTY), type));
			}
		}

		return certFiles;
	}

	public void addCertFile(CertificateFileModel certFile) {
		int size = this.preferences.node(certFile.getType().name()).getInt("size", 0);

		this.preferences.node(certFile.getType().name()).put("file_" + size, certFile.getFilePath());
		this.preferences.node(certFile.getType().name()).putInt("size", ++size);
	}

	public void removeCertFile(int index, CertTypeEnum type) {
		List<CertificateFileModel> certFiles = getCertFiles(type);
		int size = this.preferences.node(type.name()).getInt("size", 0);

		try {
			this.preferences.node(type.name()).removeNode();
		} catch (BackingStoreException e) {
			// Ops...
		}

		certFiles.forEach(f -> {
			if (index != certFiles.indexOf(f)) {
				addCertFile(f);
			}
		});

		this.preferences.node(type.name()).putInt("size", --size);
	}

	public CredentialModel getCredential() {
		return new CredentialModel(this.preferences.node("credential").get("instance", StringUtils.EMPTY),
				this.preferences.node("credential").get("document", StringUtils.EMPTY), null);
	}

	public void addCredential(CredentialModel credential) {
		this.preferences.node("credential").put("instance", credential.getInstance());
		this.preferences.node("credential").put("document", credential.getDocument());
	}

}
