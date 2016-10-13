package ru.fileexchanger.client;

import ru.fileexchanger.client.services.FileClientMainService;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileExchangerClientMain {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FileClientMainService().start();
            }
        });

    }

}
