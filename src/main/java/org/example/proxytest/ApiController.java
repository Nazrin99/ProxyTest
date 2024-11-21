package org.example.proxytest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@RestController
public class ApiController {

    // Define a simple GET endpoint
    @GetMapping("/success")
    public String getSuccessResponse() {
        try {
            // Step 1: Create a trust store and add a certificate to it
            String trustStorePath = "C:\\Users\\nazrin.nazri\\IdeaProjects\\ProxyTest\\src\\main\\resources\\mytruststore.jks";
            String trustStorePassword = "Kelana2024!!"; // Replace with your password
            String certificatePath = "C:\\Users\\nazrin.nazri\\IdeaProjects\\ProxyTest\\src\\main\\resources\\SKRSD01.crt"; // Replace with the path to your certificate file

            // Create a new empty trust store (JKS format)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);  // Load an empty trust store

            // Load the certificate from the file
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream certFileStream = new FileInputStream(certificatePath);
            Certificate cert = certificateFactory.generateCertificate(certFileStream);

            // Add the certificate to the trust store with an alias
            String alias = "skrsd01-cert";
            trustStore.setCertificateEntry(alias, cert);

            // Save the trust store to a file
            try (FileOutputStream fos = new FileOutputStream(trustStorePath)) {
                trustStore.store(fos, trustStorePassword.toCharArray());
            }

            System.out.println("Trust store created successfully at " + trustStorePath);

            // Step 2: Load the trust store into SSLContext
            System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);

            // Initialize the SSLContext with the custom trust store
            KeyStore loadedTrustStore = KeyStore.getInstance("JKS");
            try (FileInputStream trustStoreStream = new FileInputStream(trustStorePath)) {
                loadedTrustStore.load(trustStoreStream, trustStorePassword.toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedTrustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Step 3: Make an HTTPS request using the custom SSLContext
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Example HTTPS request
            String targetUrl = "https://skrsd01:80/api/v3/users/1825"; // Replace with your server URL
            URL url = new URL(targetUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set headers (Optional, based on your API requirements)
            connection.setRequestProperty("TECHNICIAN_KEY", "64D9A92E-2186-49C9-BF8E-029C6A68913C");

            // Send the request and get the response
            int responseCode = connection.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);

            String responseMessage = null;

            // Read the response (if needed)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("Response: " + response.toString());
                responseMessage = response.toString();
            }

            connection.disconnect();

            return responseMessage;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Empty";
    }
}
