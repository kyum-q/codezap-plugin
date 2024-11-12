package com.codezap.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.codezap.client.CodeZapClient;
import com.codezap.dto.request.CreateSourceCodeRequest;
import com.codezap.dto.request.TemplateCreateRequest;
import com.codezap.dto.response.FindAllCategoriesResponse;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

public class CreateTemplatePanel {

    private CreateTemplatePanel() {
    }

    public static TemplateCreateRequest inputCreateTemplate(String fileName, String content) throws IOException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = JBUI.insetsRight(5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.2;
        JLabel fileNameLabel = new JLabel("파일명:", SwingConstants.RIGHT);
        panel.add(fileNameLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.8;
        JTextField fileNameField = new JTextField(fileName, 20);
        fileNameField.setEditable(false);
        panel.add(fileNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.2;
        JLabel sourceCodeLabel = new JLabel("소스코드:", SwingConstants.RIGHT);
        panel.add(sourceCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.8;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        JTextArea contentArea = new JTextArea(10, 20);
        contentArea.setText(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JBScrollPane(contentArea);
        panel.add(scrollPane, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel titleLabel = new JLabel("템플릿 제목을 입력해주세요.");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        panel.add(titleLabel, constraints);

        String title = JOptionPane.showInputDialog(null, panel, "템플릿 제목 입력", JOptionPane.PLAIN_MESSAGE);

        if (title != null) {
            return makeTemplateCreateRequest(title, fileName, content);
        }
        throw new RuntimeException();
    }

    private static TemplateCreateRequest makeTemplateCreateRequest(String title, String fileName, String content)
            throws IOException {
        FindAllCategoriesResponse response = CodeZapClient.getCategories();
        if (response == null) {
            throw new RuntimeException();
        }
        return new TemplateCreateRequest(
                title,
                "",
                List.of(new CreateSourceCodeRequest(fileName, content, 1)),
                1,
                response.categories().get(0).id(),
                List.of(),
                "PUBLIC"
        );
    }
}
