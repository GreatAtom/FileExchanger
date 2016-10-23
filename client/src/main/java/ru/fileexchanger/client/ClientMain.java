package ru.fileexchanger.client;

import ru.fileexchanger.client.services.Context;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class ClientMain {
    public static void main(String[] args) {
        System.out.println("ТестоваяБД.accdb                                                ".equals(
                           "ТестоваяБД.accdb                                      "));
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Context();
            }
        });

    }

}
