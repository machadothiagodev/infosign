package br.com.signer.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrescriptionModel {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private String fileURL;
	private String id;
	private String patientId;
	private String patientName;
	private String requestDate;

	public PrescriptionModel(String fileURL, String id, String patientId, String patientName, Date requestDate) {
		super();

		this.fileURL = fileURL;
		this.id = id;
		this.patientId = patientId;
		this.patientName = patientName;
		this.requestDate = DATE_FORMAT.format(requestDate);
	}

	public String getFileURL(boolean onlyFileName) {
		return onlyFileName ? fileURL.substring(fileURL.lastIndexOf("/") + 1) : fileURL;
	}

	public String getId() {
		return id;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getRequestDate() {
		return requestDate;
	}

}
