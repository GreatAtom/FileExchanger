package ru.fileexchanger.client.services;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Anton on 07.10.2016.
 */
@Deprecated
class Transfer {

    public static void main(String[] args) {
        final String largeFile = "D://Anton/download/root.zip";
        final int BUFFER_SIZE = 65536;
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    Socket socket = new Socket("localhost", 8989);
                    FileInputStream fileInputStream = new FileInputStream(largeFile);
                    OutputStream socketOutputStream = socket.getOutputStream();
                    socketOutputStream.write(((String) "111111111").getBytes(), 0, 9);
                    Thread.currentThread().sleep(100);
                    socketOutputStream.write(((String) "111111111").getBytes(), 0, 9);
                    Thread.currentThread().sleep(100);
                    socketOutputStream.write(((String) "fileRec").getBytes(), 0, 7);
                    Thread.currentThread().sleep(100);

                    long startTime = System.currentTimeMillis();
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read;
                    int readTotal = 0;
                    while ((read = fileInputStream.read(buffer)) != -1) {
                        socketOutputStream.write(buffer, 0, read);
                        readTotal += read;
                    }
                    Thread.currentThread().sleep(10000);
                    socketOutputStream.close();
                    fileInputStream.close();
                    socket.close();
                    long endTime = System.currentTimeMillis();
                    System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
                } catch (Exception e) {
                }
            }
        }).start();
    }
}