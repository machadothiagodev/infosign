package br.com.signer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.signer.client.dto.PacienteDto;
import br.com.signer.client.dto.RespostaDto;
import br.com.signer.model.CredentialModel;
import br.com.signer.model.PrescriptionModel;

public class ApiClient {

	private static final Logger LOGGER = LogManager.getLogger(ApiClient.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String SERVICE_URL = "http://212.47.238.224/infoservices/infocert/api.php";
	private static final String DOWNLOADED_FILES_DIRECTORY = System.getProperty("user.home") + File.separator + "InfoSign";

	private List<PacienteDto> autenticate(CredentialModel credential) throws IOException {
		LOGGER.info("Authenticate user {} in {} instance", credential.getDocument(), credential.getInstance());

		Authenticate auth = new Authenticate(credential);

		HttpPost post = new HttpPost(SERVICE_URL);
		post.setEntity(new StringEntity(MAPPER.writeValueAsString(auth)));

		try (
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post);
		) {
			RespostaDto resposta = MAPPER.readValue(response.getEntity().getContent(), RespostaDto.class);

			if (BooleanUtils.isTrue(resposta.getSuccess())) {
				return resposta.getPacientes();
			}

			throw new IOException(resposta.getError());
		}
	}

	public List<PrescriptionModel> getFiles(CredentialModel credential) throws IOException {
		List<PrescriptionModel> files = new ArrayList<>();

		List<PacienteDto> pacientes = this.autenticate(credential);

		pacientes.forEach(p -> {
			p.getReceitas().forEach(r -> {
				files.add(new PrescriptionModel(r.getPdf(), r.getId(), p.getId(), p.getNome(), r.getPedido()));
			});
		});

		return files;
	}

	public String downloadFile(PrescriptionModel prescription) throws IOException {
		LOGGER.info("Download file from {}", prescription.getFileURL(Boolean.FALSE));

		new File(DOWNLOADED_FILES_DIRECTORY).mkdir();
		String filePath = DOWNLOADED_FILES_DIRECTORY + File.separator + this.createUniqueFileName(prescription);

		HttpGet get = new HttpGet(prescription.getFileURL(Boolean.FALSE));

		try (
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(get);
		) {
			OutputStream outStream = new FileOutputStream(new File(filePath));
			response.getEntity().writeTo(outStream);
			outStream.close();

			return filePath;
		}
	}

	private String createUniqueFileName(PrescriptionModel prescription) {
		String fileURL = prescription.getFileURL(Boolean.FALSE);
		return prescription.getPatientId() + "-" + prescription.getId() + "-" + fileURL.substring(fileURL.lastIndexOf("/") + 1);
	}

	public void sendSignedFile(String instance, String patientId, String prescriptionId, File file) throws IOException {
		LOGGER.info("Send signed file {}", file.getName());

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("token", "c20ef152b81e559ef400f564fd0e14651a4e6e76");
		builder.addTextBody("act", "assinado");
		builder.addTextBody("instancia", instance);
		builder.addTextBody("id_paciente", patientId);
		builder.addTextBody("id_receituario", prescriptionId);
		builder.addBinaryBody("pdf", file);

		HttpPost post = new HttpPost(SERVICE_URL);
		post.setEntity(builder.build());

		try (
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post);
		) {
			RespostaDto resposta = MAPPER.readValue(response.getEntity().getContent(), RespostaDto.class);

			if (BooleanUtils.isFalse(resposta.getSuccess())) {
				throw new IOException(resposta.getError());
			}
		}
	}

	@SuppressWarnings("unused")
	private class Authenticate {

		private String token = "c20ef152b81e559ef400f564fd0e14651a4e6e76";
		private String act = "autenticacao";
		private String instancia;
		private String cpf;
		private String senha;

		public Authenticate(CredentialModel credential) {
			this.instancia = credential.getInstance();
			this.cpf = credential.getDocument();
			this.senha = credential.getPassword();
		}
		
		public String getToken() {
			return token;
		}

		public String getAct() {
			return act;
		}

		public String getInstancia() {
			return instancia;
		}

		public String getCpf() {
			return cpf;
		}

		public String getSenha() {
			return senha;
		}

	}

}
