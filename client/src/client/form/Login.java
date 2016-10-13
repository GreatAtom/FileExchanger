package client.form;

import client.services.FileClientMainService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class Login extends AbstractForm {
    private JTextField textField1;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel passwordPanel;
    private JPanel buttonPanel;
    private JPasswordField passwordField1;
    private JButton loginButton;
    private JPanel basePanel;

    private LoginListener loginListener;

    public Login(FileClientMainService fileClientMainService) {
        super(fileClientMainService);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = textField1.getText();
                String password = new String(passwordField1.getPassword());
                if(validateAccount(login, password)){
                    loginListener.login(login, password);
                } else {
                    //// TODO: 12.10.2016 incorrect
                }
            }
        });
    }

    private boolean validateAccount(String login, String password) {
        try {
            fileClientMainService.createChanel(login, password);
            System.out.println("good link!");
            return true;
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void start(){
        JFrame frame = new JFrame("Login");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(new Dimension(600, 400));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setLoginListener(LoginListener loginListener){
        this.loginListener = loginListener;
    }

    public interface LoginListener {
        void login(String login, String password);
    }

}
