package ru.fileexchanger.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dmitry on 23.10.2016.
 */

/**
 * Иногда из канала считывается больше, чем положено. В таких случаях оставим в буфере лишнее.
 */
public class AsynchronousSocketChannelProxy {

    private AsynchronousSocketChannel socketChannel;
    private String charsetName;
    private String buffer = "";

    public AsynchronousSocketChannelProxy(AsynchronousSocketChannel socketChannel, String charsetName) {
        this.socketChannel = socketChannel;
        this.charsetName = charsetName;
    }

    public String readString(ByteBuffer byteBuffer, int timeout, TimeUnit timeUnit, int maxLength) throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        String message = buffer.substring(0);
        buffer = "";
        int read =  socketChannel.read(byteBuffer).get(timeout, timeUnit);
        if(read>0) {
            message += new String(byteBuffer.array(), 0, read, charsetName);
            if (message.length()>maxLength){
                buffer = message.substring(maxLength);
                message = message.substring(0, maxLength);
            }
        }
        return message;
    }

    public void clearBuffer(){
        buffer="";
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public long read(ByteBuffer inputBuffer) throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        if(buffer!= null && !buffer.equals("")) {
            byte[] bBuffer = buffer.getBytes(charsetName);
            buffer = "";
            inputBuffer.wrap(bBuffer);
        }
        return  socketChannel.read(inputBuffer).get();
    }
}
