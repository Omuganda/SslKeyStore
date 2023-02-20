package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Configuration
public class RestTemplateConfig {

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    @Value("${api.cert.caCertificate}")
    private String caCertificate;

    @Value("${api.cert.clientCertificate}")
    private String clientCertificate;

    @Value("${api.cert.privateKey}")
    private String privateKey;

    @Value("${api.cert.password}")
    private String password;


    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, InvalidKeySpecException, UnrecoverableKeyException {
        return new RestTemplate(RestTemplateHelper.restTemplate(createKeyStore(), password));
    }

    private KeyStore createKeyStore() throws CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException {
        Certificate caCertificate = KeyStoreConfig.extractCertificateContent(getCaCertificate());
        Certificate clientCertificate = KeyStoreConfig.extractCertificateContent(getClientCertificate());
        PrivateKey privateCertificateKey = KeyStoreConfig.generatePrivateKey(getPrivateKey());
        return KeyStoreConfig.assembleKeyStore(clientCertificate, caCertificate, privateCertificateKey, password);
    }

    public String getCaCertificate() {
        return new String(DECODER.decode(caCertificate), StandardCharsets.UTF_8);
    }

    public String getClientCertificate() {
        return new String(DECODER.decode(clientCertificate), StandardCharsets.UTF_8);
    }

    public String getPrivateKey() {
        return new String(DECODER.decode(privateKey), StandardCharsets.UTF_8);
    }

    public String getPassword() {
        return new String(DECODER.decode(password), StandardCharsets.UTF_8);
    }
}


