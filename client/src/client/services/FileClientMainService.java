package client.services;

/**
 * Created by Dmitry on 12.10.2016.
 */

import client.form.FileClient;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Место хранения всех сервисов
 * Чтобы у всех фреймов были одни и те же инстансы всех сервисов.
 * Некая такая обобщённая инъекция зависимостей
 */
public class FileClientMainService {
    private PropertyService propertyService;
    private FileClient fileClient;

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
}
