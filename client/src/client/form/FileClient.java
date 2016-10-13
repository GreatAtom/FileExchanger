package client.form;

import client.services.FileClientMainService;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileClient extends AbstractForm implements Login.LoginListener{

    private JFrame frame = new JFrame("FileClient");
    private JPanel mainPanel;
    private JPanel basicPanel;
    private JPanel tablePanel;
    private JTable filesTable;
    private JTextField selectedFileTextField;
    private JButton chooseFileButton;
    private JPanel addFilePanel;
    private JButton sendFileButton;
    private JPanel cards;
    private CardLayout cardLayout;

    private String login;
    private String password;

    public FileClient(FileClientMainService fileClientMainService) {
        super(fileClientMainService);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        Login loginPage = new Login(fileClientMainService);
        loginPage.setLoginListener(this);
        cards.add(loginPage.getMainPanel(), "LOGIN");
        cards.add(mainPanel, "MAIN");
        addFilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    selectedFileTextField.setText(file.getAbsolutePath());
                }
            }
        });
        initTable();
        sendFileButton.addActionListener(e->{
            try {
                fileClientMainService.sendFile(selectedFileTextField.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println("Ne udalos!");
            }
        });
    }

    private void initTable() {
        String[] columnNames = {"File Name", "Size", "Download Size", "Status"};
        for(int i=0; i<columnNames.length; i++) {
            TableColumn tableColumn = new TableColumn(i);
            tableColumn.setHeaderValue(columnNames[i]);
            filesTable.getTableHeader().getColumnModel().addColumn(tableColumn);
        }
    }

    private void initMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        JMenuItem menuOptions = new JMenuItem("Options");
        JMenuItem menuRestart = new JMenuItem("Restart");

        menuOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Options(fileClientMainService);
            }
        });
        menuRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileClientMainService.restart();
            }
        });
        menu.add(menuOptions);
        menu.add(menuRestart);
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

    public JFrame getFrame() {
        return frame;
    }
}
