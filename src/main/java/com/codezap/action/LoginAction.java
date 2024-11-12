package com.codezap.action;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.codezap.client.CodeZapClient;
import com.codezap.dto.request.LoginRequest;
import com.codezap.panel.LoginInputPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class LoginAction extends AnAction {

    public static final String SUCCESS_LOGIN = "로그인 성공";
    public static final String FAIL_LOGIN = "로그인 실패";
    public static final String FAIL_MESSAGE = "로그인이 실패했습니다.\n 다시 로그인 시도해주세요.";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (CodeZapClient.existsCookie()) {
            Messages.showInfoMessage(CodeZapClient.getLoginResponse().toString(), SUCCESS_LOGIN);
            return;
        }

        LoginRequest loginRequest = LoginInputPanel.inputLogin();
        if (loginRequest == null) {
            return;
        }

        try {
            Messages.showInfoMessage(CodeZapClient.login(loginRequest).toString(), SUCCESS_LOGIN);
        } catch (IOException ex) {
            Messages.showInfoMessage(FAIL_MESSAGE, FAIL_LOGIN);
        }
    }
}
