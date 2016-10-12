package form;

import client.FileSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by Anton on 12.10.2016.
 */
public class FileExchanger {
    private JPanel mainPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JButton buttonConnect;
    private JButton buttonSendFile;
    private JButton chooseFileButton;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JLabel messageLabel;

    private FileSender fileSender = new FileSender();
    private SocketChannel socketChannel = null;

    public FileExchanger() {
        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    textField3.setText(fileopen.getSelectedFile().getAbsolutePath());
                }
            }
        });

        buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(setChanel()) {
                    printMessage("Соединение установлено удачно");
                }
            }
        });

        buttonSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(socketChannel==null){
                    setChanel();
                }
                try {
                    fileSender.sendFile(socketChannel, textField3.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    private boolean setChanel(){
        String login = textField4.getText();
        String password = textField5.getText();
        String port = textField2.getText();
        String host = textField1.getText();
        try {
            socketChannel = fileSender.createChannel(login, password, host, Integer.valueOf(port));
            return true;
        }
        catch (NumberFormatException exception) {
            printMessage("Неккоректное значение порта");
        } catch (Exception exception) {
            printMessage("Не удалось установить соединение");
        }
        return false;
    }

    private void printMessage(String s) {
        messageLabel.setText(s);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FileExchanger");
        frame.setContentPane(new FileExchanger().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
