package client.form;

import client.services.CommonService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileClient extends AbstractForm implements Login.LoginListener{

    private JFrame frame = new JFrame("FileClient");
    private JPanel mainPanel;
    private JPanel cards;
    private CardLayout cardLayout;

    private String login;
    private String password;

    public FileClient(CommonService commonService) {
        super(commonService);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        Login loginPage = new Login(commonService);
        loginPage.setLoginListener(this);
        cards.add(loginPage.getMainPanel(), "LOGIN");
        cards.add(mainPanel, "MAIN");
    }

    private void initMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        JMenuItem menuOptions = new JMenuItem("Options");
        menuOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Options(commonService);
            }
        });
        menu.add(menuOptions);
        frame.setJMenuBar(menuBar);
    }

    public void start() {
        frame.setContentPane(cards);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        initMenuBar(frame);
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setSize(new Dimension(400, 300));
    }

    @Override
    public void login(String login, String password) {
        this.login = login;
        this.password = password;
        cardLayout.show(cards, "MAIN");
    }
}
