package com.codezap.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.annotation.Nullable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.codezap.dto.request.LoginRequest;
import com.intellij.util.ui.JBUI;

public class LoginInputPanel {

    private LoginInputPanel() {
    }

    @Nullable
    public static LoginRequest inputLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = JBUI.insets(2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        panel.add(new JLabel("아이디:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(new JTextField(20), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        panel.add(new JLabel("비밀번호:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(new JPasswordField(20), gbc);

        int option = JOptionPane.showConfirmDialog(
                null, panel, "로그인", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = ((JTextField) panel.getComponent(1)).getText();
            String password = new String(((JPasswordField) panel.getComponent(3)).getPassword());

            return new LoginRequest(username, password);
        }

        return null;
    }
}
