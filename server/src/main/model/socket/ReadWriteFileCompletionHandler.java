package main.model.socket;

import main.model.Client;
import main.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

import static main.model.socket.Server.BUFFER_SIZE;

/**
 * Created by Anton on 09.10.2016.
 */
public class ReadWriteFileCompletionHandler implements CompletionHandler<Integer, Void> {
    private static final String CHARSET_NAME = "UTF-8";
    private AsynchronousSocketChannel mChannel;
    private Server mServer;
    private Client mClient;
    private ByteBuffer mInputBuffer;
    private FileInfo mFile;

    public ReadWriteFileCompletionHandler(AsynchronousSocketChannel channel, ByteBuffer inputBuffer,
                                          Server server, Client client, FileInfo file) {
        mChannel = channel;
        mServer = server;
        mClient = client;
        mInputBuffer = inputBuffer;
        mFile = file;
    }

    @Override
    public void completed(Integer bytesRead, Void attachment) {
        if (bytesRead < 1 || !mChannel.isOpen()) {
            System.out.println("Closing connection to " + mChannel);
            mServer.removeClient(mChannel);
        } else {
            try {
                readFileFromSocket(bytesRead);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mClient.getDir().setDirUpdates(true);
            System.out.println("End of file reached...");

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            ReadWriteCompletionHandler readWriteCompletionHandler =
                    new ReadWriteCompletionHandler(mChannel, inputBuffer, mServer, mClient);
            mChannel.read(inputBuffer, null, readWriteCompletionHandler);
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {

    }

    /**
     * Reads the bytes from socket and writes to file
     *
     * @param
     */
    public void readFileFromSocket(int bytes) throws UnsupportedEncodingException {

        try (RandomAccessFile aFile = new RandomAccessFile(mClient.getDir().getPath() + mFile.getName(), "rw")) {

            FileChannel fileChannel = aFile.getChannel();

            long readBytes = bytes;
            //long readBytes = 0;

            do {
                long read = mChannel.read(mInputBuffer).get();
                readBytes += (read>0) ? read : 0;
                mInputBuffer.flip();
                fileChannel.write(mInputBuffer);
                mInputBuffer.clear();
            } while ((readBytes < mFile.getSize()));

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            System.out.println("Closing connection to " + mChannel);
            mServer.removeClient(mChannel);
            try {
                mChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        String message = "COMPLETED";
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(CHARSET_NAME), 0, message.getBytes(CHARSET_NAME).length);
        mChannel.write(byteBuffer);

    }
}
