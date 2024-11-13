package com.codezap.action;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.codezap.client.CodeZapClient;
import com.codezap.dto.request.TemplateCreateRequest;
import com.codezap.panel.CreateTemplatePanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class CreateTemplateAction extends AnAction {

    private static final String NEED_LOGIN = "로그인 필수";
    private static final String NEED_LOGIN_MESSAGE = "로그인을 먼저 해주세요.";
    private static final String NEED_FILE_SELECT_MESSAGE =
            "파일을 선택한 상태에서 실행해 주세요.\n" + "1. 프로젝트 창에서 파일 선택 또는\n" + "2. 에디터에서 파일을 연 상태로 실행";
    private static final String NEED_FILE_SELECT = "파일 선택 필수";
    private static final String SUCCESS_TEMPLATE_UPLOAD = "템플릿 생성 완료";
    private static final String SUCCESS_TEMPLATE_UPLOAD_MESSAGE = "정상적으로 템플릿이 생성되었습니다.";
    private static final String FAIL_TEMPLATE_UPLOAD_MESSAGE = "템플릿 생성이 실패했습니다.";
    private static final String FAIL_TEMPLATE_UPLOAD = "템플릿 생성 실패";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (!CodeZapClient.existsCookie()) {
            Messages.showInfoMessage(NEED_LOGIN_MESSAGE, NEED_LOGIN);
            return;
        }

        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (virtualFile == null) {
            Messages.showWarningDialog(
                    NEED_FILE_SELECT_MESSAGE,
                    NEED_FILE_SELECT
            );
            return;
        }

        try {
            String fileName = virtualFile.getName();
            String content = findContents(virtualFile, e.getData(CommonDataKeys.EDITOR));
            TemplateCreateRequest request = CreateTemplatePanel.inputCreateTemplate(fileName, content);
            CodeZapClient.createTemplate(request);
            Messages.showInfoMessage(SUCCESS_TEMPLATE_UPLOAD_MESSAGE, SUCCESS_TEMPLATE_UPLOAD);
        } catch (IOException ignored) {
            Messages.showInfoMessage(FAIL_TEMPLATE_UPLOAD_MESSAGE, FAIL_TEMPLATE_UPLOAD);
        }
    }

    private String findContents(VirtualFile virtualFile, Editor editor) {
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText != null) {
                return selectedText;
            }
        }

        try {
            return new String(virtualFile.contentsToByteArray());
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }
}

