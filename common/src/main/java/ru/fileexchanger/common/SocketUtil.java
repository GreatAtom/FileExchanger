package ru.fileexchanger.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dmitry on 19.10.2016.
 */
public class SocketUtil {

    public static final String CHARSET_NAME = "UTF-8";
    public static final String SEND_FILE_COD = "901";
    public static final int LOGIN_LENGTH = 32;
    public static final int PASSWORD_LENGTH = 32;

    public static final int FILE_NAME_LENGTH = 64;
    public static final int FILE_SIZE_LENGTH = 64;

    private static final int DEFAULT_TIMEOUT = 20;

    public static String format(String message, int length) {
        if (message.length() > length) {
            return message.substring(0, length);
        } else {
            StringBuilder builder = new StringBuilder(length);
            builder.append(message);
            for (int i = message.length(); i < length; i++) {
                builder.append(' ');
            }
            return builder.toString();
        }
    }

    public static String format(long message, int length) {
        return format(String.valueOf(message), length);
    }

    /**
     * @param socketChannel
     * @param size
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static String readMessage(AsynchronousSocketChannel socketChannel, final int size, int timeout) throws ExecutionException, InterruptedException, TimeoutException, UnsupportedEncodingException {
        //int size = length*2;
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.clear();
        int read = socketChannel.read(byteBuffer).get(timeout, TimeUnit.SECONDS);
        return new String(byteBuffer.array(), 0, read, CHARSET_NAME);

    }

    public static String readMessage(SocketChannel socketChannel, final int size) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        String ans = "";
        //int size = length*2;
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        while (ans.length()<size) {
            byteBuffer.clear();
            int read = socketChannel.read(byteBuffer);
            ans += new String(byteBuffer.array(), 0, read, CHARSET_NAME);
        }
        return ans;
    }

    public static String readMessage(AsynchronousSocketChannel socketChannel, int size) throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        return readMessage(socketChannel, size, DEFAULT_TIMEOUT);
    }

    public static String readMessage(SocketChannel socketChannel) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        int read;
        do {
            read = socketChannel.read(byteBuffer);
            if(read>0) {
                stringBuilder.append(new String(byteBuffer.array(), 0, read, CHARSET_NAME));
                byteBuffer.clear();
                byteBuffer.flip();
            }
        }
        while (read>0);

        return stringBuilder.toString();
    }

    public static void sendMessage(AsynchronousSocketChannel socketChannel, String message) throws UnsupportedEncodingException {
        System.out.println("Try to send message: " + message);
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length);
        socketChannel.write(byteBuffer);
        System.out.println("Message has send: " + message);
    }

    public static void sendMessage(SocketChannel socketChannel, String message) throws IOException {
        System.out.println("Try to send message: " + message);
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length);
        String s = new String (message.getBytes(CHARSET_NAME), CHARSET_NAME);
        System.out.println("Message: "+message+"; byte length: "+message.getBytes(CHARSET_NAME).length+"  :  " +s);
        socketChannel.write(byteBuffer);
        System.out.println("Message has send: " + message);
    }

    public static String formatUtf16(String s) {
        return s.trim().replace(new String(new char[]{'\uFEFF'}), "");
    }
}
