package com.example.demo.config;

import jdk.jfr.Percentage;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyStoreConfig {

    public static KeyStore assembleKeyStore(final Certificate clientCertificate, final Certificate caCertificate, final PrivateKey privateKey, final String pwd) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JSK");
        // keystore cannot be initialized from a stream so null is passed
        keyStore.load(null, null);
        keyStore.setCertificateEntry("client-cert", clientCertificate);
        keyStore.setCertificateEntry("ca-cert", caCertificate);
        // chain (clientCertificate)– the certificate chain for the corresponding public key (only required if the given key is of type java.security.PrivateKey)
        keyStore.setKeyEntry("client-key", privateKey, pwd.toCharArray(), new Certificate[]{clientCertificate});
        return keyStore;
    }

    public static PrivateKey generatePrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        privateKey = privateKey.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "");
        privateKey = privateKey.replaceAll("\\s", "");
        // getting byte data of private key
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        // creating object of keyFactory
        KeyFactory factory = KeyFactory.getInstance("RSA");
        // generating private key from the provided key spec.
        return factory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    public static Certificate extractCertificateContent(String certificate) throws IOException, CertificateException {
        final byte[] certificateContent = extractPemContent(certificate);
        return generateCertificate(certificateContent);
    }

    private static Certificate generateCertificate(byte[] certificateContent) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X509");
        return factory.generateCertificate(new ByteArrayInputStream(certificateContent));
    }

    private static byte[] extractPemContent(String certificate) throws IOException {
        final PemObject pemObject;
        try(PemReader reader = new PemReader(new StringReader(certificate))) {
            pemObject = reader.readPemObject();
        }
        return pemObject.getContent();
    }
}


// https://github.com/Hakky54/sslcontext-kickstart

// keyFactory generate private key
//https://www.geeksforgeeks.org/keyfactory-generateprivate-method-in-java-with-examples/

// PemReader - extract content from string
//https://github.com/hyperledger/fabric-sdk-java/blob/main/src/main/java/org/hyperledger/fabric/sdk/security/CryptoPrimitives.java#L976