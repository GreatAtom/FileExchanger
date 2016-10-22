package ru.fileexchanger.client.services;

/**
 * Created by Dmitry on 12.10.2016.
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ru.fileexchanger.client.form.FileClient;
import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.common.UserFileEnity;
import ru.fileexchanger.common.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Место хранения всех сервисов
 * Чтобы у всех фреймов были одни и те же инстансы всех сервисов.
 * Некая такая обобщённая инъекция зависимостей
 */
public class FileClientMainService {
    private PropertyService propertyService;
    private FileSenderSerive fileSenderSeirvice;
    private FileClient fileClient;
    private SocketChannel socketChannel;
    private List<UserFileEnity> userFiles;

    public void setLookAndField(){
        String des = propertyService.getDesign();
        switch (propertyService.getDesign()) {
            case "DEC": setLookAndFeelClassName(UIManager.getCrossPlatformLookAndFeelClassName());JFrame.setDefaultLookAndFeelDecorated(true);
                break;
            case "SYS": setLookAndFeelClassName(UIManager.getSystemLookAndFeelClassName());
                break;
            case "DEF": setLookAndFeelClassName(UIManager.getCrossPlatformLookAndFeelClassName()); JFrame.setDefaultLookAndFeelDecorated(false);
                break;
        }
    }

    public FileClientMainService() {
        this.propertyService = new PropertyServiceImpl();
        this.fileSenderSeirvice = new FileSenderSerive();
    }
    
    public void restart(){
        Dimension size = fileClient.getFrame().getSize();
        Point location = fileClient.getFrame().getLocation();
        fileClient.getFrame().dispose();
        start();
        fileClient.getFrame().setSize(size);
        fileClient.getFrame().setLocation(location);
    }



    public void start(){
        setLookAndField();
        fileClient = new FileClient(this);
        fileClient.start();
    }

    public FileSenderSerive getFileSenderSeirvice() {
        return fileSenderSeirvice;
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }

    private static void setLookAndFeelClassName(String systemLookAndFeelClassName) {
        try {
            UIManager.setLookAndFeel(systemLookAndFeelClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public void createChanel(String login, String password) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        socketChannel = fileSenderSeirvice.createChannel(login, password, propertyService.getHost(), propertyService.getPort());
        System.out.println("Chanel has created");
        System.out.println("Try to read fileinfo");
        //// TODO: 17.10.2016 сейчас сервер, ка ктолько устанавливает соединение сразу передаёт инфу о файлах. Поэтому пока что сразу тут её и считаем
        readFileInfo();
    }

    public void readFileInfo() throws IOException {
        String userInfoString = SocketUtil.readMessage(socketChannel);
        System.out.println("File info has read:\n"+ userInfoString);
        UserInfo userInfo =  new Gson().fromJson(userInfoString, UserInfo.class);
        userFiles = userInfo.getFileEnityList();
    }

    public List<UserFileEnity> getUserFiles() {
        return userFiles;
    }

    public void sendFile(String filePath) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        fileSenderSeirvice.sendFile(socketChannel, filePath);
    }
}
