package ru.fileexchanger.client.services;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;

public class FileSenderSerive {

    public static final String SEND_FILE_FLAG = "fileRec";
    private static final int FILE_BUFFER_SIZE = 65536;
    private static final String CHARSET_NAME = "UTF-8";


    public void sendFile(SocketChannel socketChannel, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new FileNotFoundException("Файла по указанному пути не существует");
        }
        sendFile(socketChannel, file);
    }

    /**
     * Establishes a socket channel connection
     *
     * @return
     */
    public SocketChannel createChannel(String login, String password, String host, int port) throws IOException {

        SocketChannel socketChannel = null;

        socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        socketChannel.connect(socketAddress);
        System.out.println("Connected..Now sending the login and password");
        sendMessage(socketChannel, login);
        sendMessage(socketChannel, password);
        String mess = readMessage(socketChannel, 1024);
        if(mess.equals("ACCESS_IS_ALLOWED")) {
            return socketChannel;
        } else {
            socketChannel.close();
            throw new AccessDeniedException("Incorrect login or password");
        }
    }

    private String readMessage(SocketChannel socketChannel, int i) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int res = socketChannel.read(byteBuffer);
        if(res>0) {
            return new String(byteBuffer.array(), 0, res, "UTF-8");
        } else {
            return null;
        }
    }


    public void sendFile(SocketChannel socketChannel, File file) throws IOException {

        String fileName = file.getName();
        String fileSize = String.valueOf(file.length());
        sendMessage(socketChannel, SEND_FILE_FLAG);
        sendMessage(socketChannel, fileName);

        sendMessage(socketChannel, fileSize);
        //// TODO: 12.10.2016 получить инфо о размере файла
        long completeFileSize = 0;

        RandomAccessFile aFile = new RandomAccessFile(file, "r");
        FileChannel inChannel = aFile.getChannel();
        //inChannel.position(completeFileSize);//// TODO: 12.10.2016 проверить

        ByteBuffer buffer = ByteBuffer.allocate(FILE_BUFFER_SIZE);
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            System.out.println("qqqq");
            buffer.clear();
        }
        aFile.close();

        String mess = "";
        ByteBuffer bufferedReader = ByteBuffer.allocate(1024);
        while (mess.equals("COMPLETED")) {
            socketChannel.read(bufferedReader);
            mess = bufferedReader.toString();
            bufferedReader.clear();
        }


    }

    private void sendMessage(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length);
        socketChannel.write(byteBuffer);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}