package ru.fileexchanger.client.services;

import ru.fileexchanger.common.SocketUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FileSenderSerive {

    private static final int FILE_BUFFER_SIZE = 65536;

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
    public SocketChannel createChannel(String login, String password, String host, int port) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        SocketChannel socketChannel = null;

        socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        socketChannel.connect(socketAddress);
        System.out.println("Connected..Now sending the login and password");
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(login, SocketUtil.LOGIN_LENGTH));
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(password, SocketUtil.PASSWORD_LENGTH));
        System.out.println("Try to read aswer from server");
        String mess = SocketUtil.readMessage(socketChannel, 3);
        if(mess.equals("200")) {
            System.out.println("Correct login and password");
            return socketChannel;
        } else {
            System.out.println("Message '"+mess+"' not equals ACCESS_IS_ALLOWED");
            System.out.println("Incorrect login or password");
            socketChannel.close();
            throw new AccessDeniedException("Incorrect login or password");
        }
    }


    public void sendFile(SocketChannel socketChannel, File file) throws IOException {
        System.out.println("Try to send file: "+file.getName());
        String fileName = file.getName();
        SocketUtil.sendMessage(socketChannel, SocketUtil.SEND_FILE_COD);
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(fileName, SocketUtil.FILE_NAME_LENGTH));
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(file.length(), SocketUtil.FILE_SIZE_LENGTH));

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
        System.out.println("Try to read answer from server");
        ByteBuffer bufferedReader = ByteBuffer.allocate(1024);
        while (mess.equals("201")) {
            socketChannel.read(bufferedReader);
            System.out.println("read");
            mess = bufferedReader.toString();
            bufferedReader.clear();
        }


    }

/*    private void sendMessage(SocketChannel socketChannel, String message) throws IOException {
        System.out.println("Try to send message: "+message);
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length);
        socketChannel.write(byteBuffer);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Message has send: "+message);
    }

    public String readMessage(SocketChannel socketChannel) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        int read;
        do {
            read = socketChannel.read(byteBuffer);
            if(read>0) {
                stringBuilder.append(new String(byteBuffer.array(), 0, read, "UTF-16"));
                byteBuffer.clear();
                byteBuffer.flip();
            }
        }
        while (read>0);

        return stringBuilder.toString();
    }*/
}