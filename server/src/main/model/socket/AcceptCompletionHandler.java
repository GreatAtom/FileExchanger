package main.model.socket;

import main.model.Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static main.model.socket.Server.BUFFER_SIZE;

/**
 * Created by Anton on 02.10.2016.
 * in successful client connection launches async through ReadWriteCompletionHandler and WriteCompletionHandler
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

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
                ReadWriteCompletionHandler readWriteCompletionHandler = new ReadWriteCompletionHandler(socketChannel, inputBuffer, mServer, client);

                if(client.getDir().isDirUpdates()) {
                    socketChannel.write(ByteBuffer.wrap(((String)"README.MD").getBytes()));
                    client.getDir().setDirUpdates(false);
                }

                socketChannel.read(inputBuffer, null, readWriteCompletionHandler);

            } else {
                System.out.println("close connection");

                if (socketChannel.isOpen()) {
                    socketChannel.close();
                }

                System.gc();
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }catch (TimeoutException e) {
            System.out.println("close connection");
        }
    }

    @Override
    public void failed(Throwable arg0, Void arg1) {

    }

    private String reedLineFromClient(AsynchronousSocketChannel socketChannel) throws ExecutionException, InterruptedException, TimeoutException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        int bytesRead = 0;
        bytesRead = socketChannel.read(byteBuffer).get(20, TimeUnit.SECONDS);

        // Make the buffer ready to read
        byteBuffer.flip();

        // Convert the buffer into a line
        if (bytesRead > 0) {
            byte[] lineBytes = new byte[bytesRead];
            byteBuffer.get(lineBytes, 0, bytesRead);
            String line = new String(lineBytes);

            return line;
        }

        return null;
    }
}