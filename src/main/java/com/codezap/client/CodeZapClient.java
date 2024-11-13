package com.codezap.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.codezap.dto.request.LoginRequest;
import com.codezap.dto.request.TemplateCreateRequest;
import com.codezap.dto.response.FindAllCategoriesResponse;
import com.codezap.dto.response.FindCategoryResponse;
import com.codezap.dto.response.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CodeZapClient {

    private static final String CODE_ZAP_LOGIN_URL = "https://api.code-zap.com";
    private static final String HEADER_SET_COOKIE = "Set-Cookie";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_COOKIE = "Cookie";
    private static final String APPLICATION_JSON_UTF_8 = "application/json; utf-8";
    private static final String LOGIN_URL = "/login";
    private static final String TEMPLATES_URL = "/templates";
    private static final String CATEGORIES_URL = "/categories?memberId=";

    private static String cookie;

    private CodeZapClient() {}

    public static LoginResponse login(LoginRequest request) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection(LOGIN_URL, HttpMethod.POST, request);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                setCookie(connection);
                return makeResponse(connection, jsonResponse ->
                        new LoginResponse(jsonResponse.get("memberId").asLong(), jsonResponse.get("name").asText()));
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        throw new RuntimeException();
    }

    public static void createTemplate(TemplateCreateRequest request) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection(TEMPLATES_URL, HttpMethod.POST, request);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Nullable
    public static FindAllCategoriesResponse getCategories(long memberId) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection(CATEGORIES_URL + memberId, HttpMethod.GET, null);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return makeResponse(connection, (CodeZapClient::makeCategoriesResponse));
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @NotNull
    private static FindAllCategoriesResponse makeCategoriesResponse(JsonNode jsonResponse) {
        List<FindCategoryResponse> categories = new ArrayList<>();
        JsonNode categoriesNode = jsonResponse.get("categories");

        for (JsonNode categoryNode : categoriesNode) {
            long id = categoryNode.get("id").asLong();
            String name = categoryNode.get("name").asText();
            categories.add(new FindCategoryResponse(id, name));
        }
        return new FindAllCategoriesResponse(categories);
    }

    private static HttpURLConnection getHttpURLConnection(
            String api,
            HttpMethod httpMethod,
            Object requestBody
    ) throws IOException {
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

    private static <T> T makeResponse(HttpURLConnection connection, MakeResponse<T> makeResponse) throws IOException {
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

    private static synchronized void setCookie(HttpURLConnection connection) {
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
