package br.com.signer.model;

import br.com.signer.CertTypeEnum;

public class CertificateFileModel {

	private String filePath;
	private CertTypeEnum type;

	public CertificateFileModel(String filePath, CertTypeEnum type) {
		this.filePath = filePath;
		this.type = type;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CertTypeEnum getType() {
		return type;
	}

	public void setType(CertTypeEnum type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("(%s) %s", type.name(), filePath);
	}

}
