package br.com.signer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyStoreManager {

	public KeyStore getKeyStore(CertTypeEnum certType, File file) throws Exception {
		KeyStore.Builder kb = null;

		if (CertTypeEnum.A1.equals(certType)) {
			kb = KeyStore.Builder.newInstance("PKCS12", new BouncyCastleProvider(), file, new KeyStore.CallbackHandlerProtection(new PasswordCallbackHandler()));
		} else {
			kb = KeyStore.Builder.newInstance("PKCS11", this.getProvider(file), new KeyStore.CallbackHandlerProtection(new PasswordCallbackHandler()));
		}

		return kb != null ? kb.getKeyStore() : null;
	}

	private Provider getProvider(File file) throws Exception {
		Provider provider = Security.getProvider("SunPKCS11-" + FilenameUtils.getBaseName(file.getName()));

		if (provider == null) {
			Class<?> clazz = Class.forName("sun.security.pkcs11.SunPKCS11");
			Constructor<?> constructor = clazz.getConstructor();
			Object object = constructor.newInstance();
			Method method = object.getClass().getMethod("configure", String.class);

			provider = (Provider) method.invoke(object, this.getConfigFile(FilenameUtils.getBaseName(file.getName()), file.getAbsolutePath()).getAbsolutePath());

			Security.addProvider(provider);
		}

		return provider;
	}

	private File getConfigFile(String providerName, String path) throws IOException {
		StringBuilder content = new StringBuilder("name=").append(providerName).append("\n").append("library=").append(path);

		Path configFile = Files.createTempFile("pkcs11", "cfg");
		Files.write(configFile, content.toString().getBytes(StandardCharsets.UTF_8));

		return configFile.toFile();
	}

}
