package com.codezap.service;

import com.codezap.client.CodeZapClient;
import com.codezap.dto.request.LoginRequest;
import com.codezap.dto.response.LoginResponse;
import com.codezap.panel.LoginInputPanel;
import com.intellij.openapi.ui.Messages;

public class LoginService {

    public static final String SUCCESS_LOGIN = "로그인 성공";
    public static final String FAIL_LOGIN = "로그인 실패";
    public static final String FAIL_MESSAGE = "로그인이 실패했습니다.\n 다시 로그인 시도해주세요.";
    public static final String WELCOME_MESSAGE = "님 만나서 반가워요.";

    private static LoginResponse loginResponse;

    public synchronized boolean login() {
        if (CodeZapClient.existsCookie()) {
            Messages.showInfoMessage(loginResponse.name() + WELCOME_MESSAGE, SUCCESS_LOGIN);
            return true;
        }

        try {
            LoginRequest loginRequest = LoginInputPanel.inputLogin();
            loginResponse = CodeZapClient.login(loginRequest);
            Messages.showInfoMessage(loginResponse.name() + WELCOME_MESSAGE, SUCCESS_LOGIN);
            return true;
        } catch (Exception e) {
            Messages.showInfoMessage(FAIL_MESSAGE, FAIL_LOGIN);
            return false;
        }
    }

    public long getMemberId() {
        return loginResponse.memberId();
    }
}
