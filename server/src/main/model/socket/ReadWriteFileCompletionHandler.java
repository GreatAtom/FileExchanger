package main.model.socket;

import main.model.Client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Anton on 09.10.2016.
 */
public class ReadWriteFileCompletionHandler implements CompletionHandler<Integer, Void> {
    private AsynchronousSocketChannel mChannel;
    private Server mServer;
    private Client mClient;
    private ByteBuffer mInputBuffer;

    public ReadWriteFileCompletionHandler(AsynchronousSocketChannel channel, ByteBuffer inputBuffer, Server server, Client client) {
        mChannel = channel;
        mServer = server;
        mClient = client;
        mInputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Void attachment) {

        //mClient.getDir().setDirUpdates(false);


        if (bytesRead < 1 || !mChannel.isOpen()) {
            System.out.println("Closing connection to " + mChannel);
            mServer.removeClient(mChannel);
        } else {

            readFileFromSocket(bytesRead);

        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {

    }

    /**
     * Reads the bytes from socket and writes to file
     *
     * @param bytesRead
     */
    public void readFileFromSocket(Integer bytesRead) {
        try (RandomAccessFile aFile = new RandomAccessFile("D://a.zip", "rw")) {

            FileChannel fileChannel = aFile.getChannel();
            while (mChannel.read(mInputBuffer).get(5000, TimeUnit.MILLISECONDS) != null) {
                mInputBuffer.flip();
                fileChannel.write(mInputBuffer);
                mInputBuffer.clear();
            }

            System.out.println("End of file reached..Closing channel");
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("End of file reached...Closing channel");
        }
    }
}
