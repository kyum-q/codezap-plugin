package com.codezap.service;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.codezap.client.CodeZapClient;
import com.codezap.client.HttpMethod;
import com.codezap.dto.request.LoginRequest;
import com.codezap.dto.response.LoginResponse;
import com.codezap.panel.LoginInputPanel;
import com.intellij.openapi.ui.Messages;

public class LoginService {

    public static final String SUCCESS_LOGIN = "로그인 성공";
    public static final String FAIL_LOGIN = "로그인 실패";
    public static final String FAIL_MESSAGE = "로그인이 실패했습니다.\n 다시 로그인 시도해주세요.";
    public static final String WELCOME_MESSAGE = "님 만나서 반가워요.";
    private static final String LOGIN_URL = "/login";

    private LoginResponse loginResponse;

    public boolean login() {
        if (CodeZapClient.existsCookie()) {
            Messages.showInfoMessage(loginResponse.name() + WELCOME_MESSAGE, SUCCESS_LOGIN);
            return true;
        }

        try {
            LoginRequest loginRequest = LoginInputPanel.inputLogin();
            setLoginResponse(login(loginRequest));
            Messages.showInfoMessage(loginResponse.name() + WELCOME_MESSAGE, SUCCESS_LOGIN);
            return true;
        } catch (Exception e) {
            Messages.showInfoMessage(FAIL_MESSAGE, FAIL_LOGIN);
            return false;
        }
    }

    private LoginResponse login(LoginRequest request) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = CodeZapClient.getHttpURLConnection(LOGIN_URL, HttpMethod.POST, request);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                CodeZapClient.setCookie(connection);
                return CodeZapClient.makeResponse(connection, jsonResponse ->
                        new LoginResponse(jsonResponse.get("memberId").asLong(), jsonResponse.get("name").asText()));
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        throw new RuntimeException();
    }

    public long getMemberId() {
        return loginResponse.memberId();
    }

    private void setLoginResponse(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }
}
