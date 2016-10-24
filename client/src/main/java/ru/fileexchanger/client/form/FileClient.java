package ru.fileexchanger.client.form;

import ru.fileexchanger.client.services.Context;
import ru.fileexchanger.client.services.FileSenderService;
import ru.fileexchanger.client.services.Property;
import ru.fileexchanger.common.json.UserFileEnity;
import ru.fileexchanger.common.json.UserInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileClient implements Login.LoginListener, FileSenderService.Informer {

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
    private JLabel infoLabel;
    private JTable usersTable;
    private JTabbedPane tabbedPane1;
    private JButton shareButton;
    private JButton makePrivateButton;
    private JPanel cards;
    private CardLayout cardLayout;

    private String login;
    private String password;

    private FileSenderService fileSenderSerive;
    private Property property;
    private List<Integer> fileIdsForShared;
    private List<String> userLoginsForShared;

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
            }
        });
        updateButton.addActionListener(e-> {
            try {
                updateFileAndUsersTables();
            } catch (IOException e1) {
                System.out.println("Не удалось обновить таблицу");
            }
        });
        shareButton.addActionListener(e->{
            fileSenderService.shareFiles(fileIdsForShared, userLoginsForShared);
        });
        makePrivateButton.addActionListener(e->{
            fileSenderService.makePrivate(fileIdsForShared);
        });

        DefaultTableModel filesTableModel= (DefaultTableModel) filesTable.getModel();
        initFilesTable(filesTableModel);
        DefaultTableModel usersTableModel = (DefaultTableModel) usersTable.getModel();
        initUsersTable(usersTableModel);
    }

    private void updateFileAndUsersTables() throws IOException {
        UserInfo userInfo = fileSenderSerive.getUpdatedUserInfo();
        java.util.List<UserFileEnity> files = userInfo.getFileEnityList();
        DefaultTableModel model = (DefaultTableModel) filesTable.getModel();
        removeRows(model);
        files.forEach(f ->
                model.addRow(f.toArray(true))
        );

        List<String> users = userInfo.getUsers();
        DefaultTableModel usersTableModel= (DefaultTableModel) usersTable.getModel();
        removeRows(usersTableModel);
        users.stream().forEach(f->usersTableModel.addRow(new Object[]{f}));
    }

    private void removeRows(DefaultTableModel model) {
        int count = model.getRowCount();
        for(int i=count-1; i>=0; i--) {
            model.removeRow(i);
        }
    }

    private void initFilesTable(DefaultTableModel model) {
        String[] columnNames = {"id", "File Name", "Size", "Download Size", "Status"};
        for (int i = 0; i < columnNames.length; i++) {
            model.addColumn(columnNames[i]);
        }
        ListSelectionModel cellSelectionModel = filesTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRow = filesTable.getSelectedRows();

                fileIdsForShared = new ArrayList<>();
                for (int i = 0; i < selectedRow.length; i++) {
                    Integer id = (Integer) filesTable.getValueAt(selectedRow[i], 0);
                    fileIdsForShared.add(id);
                }

            }

        });
    }

    private void initUsersTable(DefaultTableModel model) {
        String[] columnNames = {"login"};
        for (int i = 0; i < columnNames.length; i++) {
            model.addColumn(columnNames[i]);
        }

        ListSelectionModel cellSelectionModel = usersTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int[] selectedRow = usersTable.getSelectedRows();
                userLoginsForShared = new ArrayList<>();
                for (int i = 0; i < selectedRow.length; i++) {
                    String id = (String) usersTable.getValueAt(selectedRow[i], 0);
                    userLoginsForShared.add(id);
                }
            }
        });
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
            updateFileAndUsersTables();
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

    @Override
    public void writeMessage(String message) {
        infoLabel.setText(message);
        try {
            updateFileAndUsersTables();
        } catch (IOException e) {
            e.printStackTrace();
            infoLabel.setText("Не удалось обновить таблицу");
        }
    }

    @Override
    public void fileHasSend() {
        try {
            updateFileAndUsersTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
