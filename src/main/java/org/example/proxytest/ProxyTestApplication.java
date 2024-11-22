package org.example.proxytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@SpringBootApplication
public class ProxyTestApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProxyTestApplication.class);
        app.addListeners(event -> {
            if (event instanceof ApplicationStartingEvent) {
                String programFilesPath = System.getenv("ProgramFiles");
                String basePath = programFilesPath + "\\powerplatform";
                String trustStorePath = basePath + "\\mytruststore.jks";
                String trustStorePassword = "secret";
                String certificatePath = basePath + "\\cert.crt";

                try {
                    KeyStore trustStore = KeyStore.getInstance("JKS");
                    trustStore.load(null, null);

                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    FileInputStream certFileStream = new FileInputStream(certificatePath);
                    Certificate cert = certificateFactory.generateCertificate(certFileStream);

                    String alias = "cert";
                    trustStore.setCertificateEntry(alias, cert);

                    try (FileOutputStream fileOutputStream = new FileOutputStream(trustStorePath)) {
                        trustStore.store(fileOutputStream, trustStorePassword.toCharArray());
                    }

                    System.out.println("Trust store created successfully at " + trustStorePath);

                    System.setProperty("javax.net.ssl.trustStore", trustStorePath);
                    System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

                    KeyStore loadedTrustStore = KeyStore.getInstance("JKS");
                    try (FileInputStream trustStoreStream = new FileInputStream(trustStorePath)) {
                        loadedTrustStore.load(trustStoreStream, trustStorePassword.toCharArray());
                    }

                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init(loadedTrustStore);

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException |
                         KeyManagementException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        app.run(args);
    }

}
