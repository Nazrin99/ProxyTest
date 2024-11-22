package org.example.proxytest;

import org.springframework.http.HttpMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Connection {
    private static final String BASE_URL = System.getenv("PP_SERVICE_DESK_BASE_URL");
    private static final String TECHNICIAN_KEY = System.getenv("PP_SERVICE_DESK_TECHNICIAN_KEY");
    private static final String INTEGRATION_KEY = System.getenv("PP_SERVICE_DESK_INTEGRATION_KEY");

    public static HttpsURLConnection getConnection(String endpointPath, HttpMethod method, KeyType keyType) throws IOException {
        URL url = new URL(BASE_URL + endpointPath);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(String.valueOf(method));

        connection.setRequestProperty(keyType == KeyType.TECHNICIAN_KEY ? "TECHNICIAN_KEY" : "INTEGRATION_KEY",
                keyType == KeyType.TECHNICIAN_KEY ? TECHNICIAN_KEY : INTEGRATION_KEY);

        return connection;
    }
}
