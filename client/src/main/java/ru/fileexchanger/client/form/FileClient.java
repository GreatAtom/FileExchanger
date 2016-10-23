package ru.fileexchanger.client.form;

import ru.fileexchanger.client.services.Context;
import ru.fileexchanger.client.services.FileSenderService;
import ru.fileexchanger.client.services.Property;
import ru.fileexchanger.common.UserFileEnity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileClient implements Login.LoginListener {

    private JFrame frame = new JFrame("FileClient");
    private JPanel mainPanel;
    private JPanel basicPanel;
    private JPanel tablePanel;
    private JTable filesTable;
    private JTextField selectedFileTextField;
    private JButton chooseFileButton;
    private JPanel addFilePanel;
    private JButton sendFileButton;
    private JButton updateButton;
    private JPanel bottomPanel;
    private JPanel cards;
    private CardLayout cardLayout;

    private String login;
    private String password;

    private FileSenderService fileSenderSerive;
    private Property property;

    public FileClient(FileSenderService fileSenderService) {
        this.fileSenderSerive = fileSenderService;
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        Login loginPage = new Login(fileSenderService);
        loginPage.setLoginListener(this);
        cards.add(loginPage.getMainPanel(), "LOGIN");
        cards.add(mainPanel, "MAIN");
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addFilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                selectedFileTextField.setText(file.getAbsolutePath());
            }
        });
        sendFileButton.addActionListener(e -> {
            try {
                fileSenderSerive.sendFile(selectedFileTextField.getText());
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("Ne udalos!");
            }
        });
        updateButton.addActionListener(e-> {
            try {
                updateTable();
            } catch (IOException e1) {
                System.out.println("Не удалось обновить таблицу");
            }
        });

        DefaultTableModel model = (DefaultTableModel) filesTable.getModel();
        initTable(model);
    }

    private void updateTable() throws IOException {
        java.util.List<UserFileEnity> files = fileSenderSerive.getUpdatedUserFiles();

        DefaultTableModel model = (DefaultTableModel) filesTable.getModel();
        removeRows(model);
        //initTable(model);
        files.forEach(f ->
                model.addRow(f.toArray(true))
        );
    }

    private void removeRows(DefaultTableModel model) {
        int count = model.getRowCount();
        for(int i=count-1; i>=0; i--) {
            model.removeRow(i);
        }
    }

    private void initTable(DefaultTableModel model) {
        String[] columnNames = {"id", "File Name", "Size", "Download Size", "Status"};
        for (int i = 0; i < columnNames.length; i++) {
            model.addColumn(columnNames[i]);
        }
    }

    private void initMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        JMenuItem menuOptions = new JMenuItem("Options");
        JMenuItem menuRestart = new JMenuItem("Restart");

        menuOptions.addActionListener(e -> {new Options(property);});
        menuRestart.addActionListener(e -> Context.restart());
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
        try {
            updateTable();
        } catch (IOException e) {
            System.out.println("Не удалось обновить таблицу");
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

}
