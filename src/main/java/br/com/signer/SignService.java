package br.com.signer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

import br.com.signer.model.PrescriptionModel;

public class SignService {

	private static final Logger LOGGER = LogManager.getLogger(SignService.class);

	private ApiClient apiClient = new ApiClient();

	public void sign(PrivateKey pk, Provider provider, Certificate[] chain, String instance, List<PrescriptionModel> precriptions) throws Exception {
		Security.addProvider(provider);
		List<String> errors = new ArrayList<>();

		for (PrescriptionModel precription : precriptions) {
			try {
				String filePath = this.apiClient.downloadFile(precription);
				File signedFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + "-signed.pdf");

				LOGGER.info("Sign file {}", precription.getFileURL(Boolean.TRUE));

				PdfReader reader = new PdfReader(filePath);
				PdfSigner signer = new PdfSigner(reader, new FileOutputStream(signedFile), new StampingProperties());

				PdfDocument pdfDocument = signer.getDocument();

				// Create the signature appearance
				Rectangle rect = new Rectangle(30, 80, 530, 100);
				PdfSignatureAppearance appearance = signer.getSignatureAppearance();
				appearance.setPageRect(rect).setPageNumber(1);

				signer.setFieldName("sig");

				// Get the background layer and draw a gray rectangle as a background.
				PdfFormXObject n0 = appearance.getLayer0();

				float x = n0.getBBox().toRectangle().getLeft();
				float y = n0.getBBox().toRectangle().getBottom();
				float width = n0.getBBox().toRectangle().getWidth();
				float height = n0.getBBox().toRectangle().getHeight();

				PdfCanvas canvas_n0 = new PdfCanvas(n0, pdfDocument);
				canvas_n0.setFillColor(ColorConstants.GRAY);
				canvas_n0.setExtGState(new PdfExtGState().setFillOpacity(0.1f));
				canvas_n0.rectangle(x, y, width, height);
				canvas_n0.fill();

				// Set the signature information on layer 2
				PdfFormXObject n2 = appearance.getLayer2();
				
				float [] columnWidths = {20, 80};
				Table table = new Table(UnitValue.createPercentArray(columnWidths));
				
				ImageData data = ImageDataFactory.create(this.generateQRCode(precription.getFileURL(Boolean.FALSE)));
				Image img = new Image(data);
				Cell cell1 = new Cell();
				cell1.setBorder(Border.NO_BORDER);
				cell1.add(img.setAutoScale(true));
				table.addCell(cell1);
				
				Paragraph p = new Paragraph(
						"Receituário assinado digitalmente por " + this.getCommonName((X509Certificate) chain[0]) + " em "
								+ new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));

				p.setMargin(10);
				p.setFontColor(ColorConstants.BLACK);
				p.setFontSize(10);
				
				Cell cell2 = new Cell();
				cell2.setBorder(Border.NO_BORDER);
				cell2.setTextAlignment(TextAlignment.JUSTIFIED);
				cell2.setVerticalAlignment(VerticalAlignment.MIDDLE);
				cell2.add(p);
				table.addCell(cell2);

				Canvas canvas_n2 = new Canvas(n2, pdfDocument);
				canvas_n2.add(table);
				canvas_n2.close();

				IExternalDigest digest = new BouncyCastleDigest();
				IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());

				// Sign the document using the detached mode, CMS or CAdES equivalent.
				signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);

				this.apiClient.sendSignedFile(instance, precription.getPatientId(), precription.getId(), signedFile);
			} catch (Exception ex) { // Make a personalize Exception
				LOGGER.error("Error while make signer PDF", ex);
				errors.add(String.format("Falha ao assinar/enviar o documento %s: %s", precription.getId(), ex.getLocalizedMessage()));
			}
		}
		
		if (!errors.isEmpty()) {
			throw new Exception(errors.stream().collect(Collectors.joining(", ")));
		}

	}

	private String getCommonName(X509Certificate cert) {
		String tmpName, name = StringUtils.EMPTY;
		
		Principal principal = cert.getSubjectDN();
		
		int start = principal.getName().indexOf("CN");
		if (start != -1) {
			tmpName = principal.getName().substring(start + 3);
			int end = tmpName.indexOf(":");
			if (end > 0) {
				name = tmpName.substring(0, end);
			} else {
				end = tmpName.indexOf(",");
				if (end > 0) {
					name = tmpName.substring(0, end);
				} else {
					name = tmpName;
				}
			}
		}
		return name;
	}

	private byte[] generateQRCode(String barcodeText) {
		byte[] result = null;

		try {
		    QRCodeWriter barcodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
		    
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "jpg", baos);
		    result = baos.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
