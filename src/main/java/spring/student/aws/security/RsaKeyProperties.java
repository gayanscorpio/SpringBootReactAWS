package spring.student.aws.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class RsaKeyProperties {

	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public RsaKeyProperties() throws Exception {
		String privateKeyPEM = loadKeyFromFile("keys/private.pem");
		String publicKeyPEM = loadKeyFromFile("keys/public.pem");

		this.privateKey = loadPrivateKey(privateKeyPEM);
		this.publicKey = loadPublicKey(publicKeyPEM);
	}

	private String loadKeyFromFile(String path) throws Exception {
		InputStream inputStream = new ClassPathResource(path).getInputStream();
		return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private PrivateKey loadPrivateKey(String key) throws Exception {
		String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s+", "");
		byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	private PublicKey loadPublicKey(String key) throws Exception {
		String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s+", "");
		byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}
}
