package ru.fileexchanger.client.services;

/**
 * Created by Dmitry on 12.10.2016.
 */

import ru.fileexchanger.client.form.FileClient;

import javax.swing.*;
import java.awt.*;

/**
 * Место хранения всех сервисов
 * Чтобы у всех фреймов были одни и те же instances всех сервисов.
 * Некая такая обобщённая инъекция зависимостей
 */
public class Context {
    private Property property;
    private FileSenderService fileSenderSeirvice;
    private FileClient fileClient;
    private static Context context;


    public Context() {
        this.context =this;
        start();
    }
    
    public void doRestart(){
        Dimension size = fileClient.getFrame().getSize();
        Point location = fileClient.getFrame().getLocation();
        fileClient.getFrame().dispose();
        start();
        fileClient.getFrame().setSize(size);
        fileClient.getFrame().setLocation(location);
    }

    public static void restart(){
        if(context!=null) {
            context.doRestart();
        }
    }

    private void start(){
        this.property = new Property();
        setLookAndField();
        this.fileSenderSeirvice = new FileSenderService();
        fileSenderSeirvice.setProperty(property);
        this.fileClient = new FileClient(fileSenderSeirvice);
        fileClient.setProperty(property);
        fileSenderSeirvice.setInformer(fileClient);
        fileClient.start();
    }

    public void setLookAndField(){
        switch (property.getDesign()) {
            case "DEC": setLookAndFeelClassName(UIManager.getCrossPlatformLookAndFeelClassName());JFrame.setDefaultLookAndFeelDecorated(true);
                break;
            case "SYS": setLookAndFeelClassName(UIManager.getSystemLookAndFeelClassName());
                break;
            case "DEF": setLookAndFeelClassName(UIManager.getCrossPlatformLookAndFeelClassName()); JFrame.setDefaultLookAndFeelDecorated(false);
                break;
        }
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
