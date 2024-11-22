package org.example.proxytest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@RestController
public class ApiController {
    static String baseUrl = "https://skrsd01:80/api/v3";

    // Define a simple GET endpoint
    @GetMapping("/success")
    public String getSuccessResponse() {
        try {
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
