package main.model.socket;

import main.model.Client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static main.model.socket.Server.BUFFER_SIZE;
import static main.model.socket.Server.reedLineFromClient;

/**
 * Created by Anton on 02.10.2016.
 * in successful client connection launches async through ReadWriteCompletionHandler and WriteCompletionHandler
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private static final String CHARSET_NAME = "UTF-8";
    private AsynchronousServerSocketChannel mListener;
    private Server mServer;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener, Server server) {
        mListener = listener;
        mServer = server;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
        System.out.println("client connected: " + socketChannel + " " + Thread.currentThread().getId());

        mListener.accept(null, this);

        try {
            String login = reedLineFromClient(socketChannel);
            String password = reedLineFromClient(socketChannel);

            Client client = Client.tryCreateClient(login, password);
            if (client != null && mListener.isOpen()) {
                System.out.println(client.getmLogin() + " " + client.getmPassword() + " " + client.getmToken());
                mServer.addClient(socketChannel);

                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                ReadWriteCompletionHandler readWriteCompletionHandler
                        = new ReadWriteCompletionHandler(socketChannel, inputBuffer, mServer, client);

                socketChannel.read(inputBuffer, null, readWriteCompletionHandler);
                sendMessage(socketChannel, "ACCESS_IS_ALLOWED");
                sendFilesInfo(socketChannel, client);
            } else {
                sendMessage(socketChannel, "ACCESS_IS_DENIED");
                System.out.println("close connection");

                if (socketChannel.isOpen()) {
                    socketChannel.close();
                }

                System.gc();
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("close connection");
        }
    }

    private void sendFilesInfo(AsynchronousSocketChannel socketChannel, Client client) {
        File dir = new File(client.getDir().getPath());
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
        }
    }

    private void sendMessage(AsynchronousSocketChannel socketChannel, String message) throws UnsupportedEncodingException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length); //// TODO: 13.10.2016
        socketChannel.write(byteBuffer);
    }

    @Override
    public void failed(Throwable arg0, Void arg1) {

    }
}