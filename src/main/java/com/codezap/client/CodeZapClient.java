package com.codezap.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CodeZapClient {

    private static final String CODE_ZAP_LOGIN_URL = "https://api.code-zap.com";
    private static final String HEADER_SET_COOKIE = "Set-Cookie";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_COOKIE = "Cookie";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; utf-8";

    private static String cookie;

    private CodeZapClient() {}

    public static HttpURLConnection getHttpURLConnection(String api, HttpMethod httpMethod, Object requestBody)
            throws IOException {
        URL url = new URL(CODE_ZAP_LOGIN_URL + api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpMethod.name());
        connection.setRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_JSON_UTF_8);
        connection.setRequestProperty(HEADER_ACCEPT, APPLICATION_JSON_UTF_8);
        connection.setRequestProperty(HEADER_COOKIE, cookie);

        if (requestBody != null) {
            setRequestBody(requestBody, connection);
        }
        return connection;
    }

    private static void setRequestBody(Object requestBody, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInput = objectMapper.writeValueAsString(requestBody);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    public static <T> T makeResponse(HttpURLConnection connection, MakeResponse<T> makeResponse) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.toString());
        return makeResponse.make(jsonResponse);
    }

    public static synchronized void setCookie(HttpURLConnection connection) {
        List<String> cookies = connection.getHeaderFields().get(HEADER_SET_COOKIE);
        StringBuilder newCookie = new StringBuilder();
        if (cookies == null) {
            return;
        }
        for (String c : cookies) {
            newCookie.append(c);
        }
        cookie = newCookie.toString();
    }

    public static boolean existsCookie() {
        return cookie != null;
    }
}
