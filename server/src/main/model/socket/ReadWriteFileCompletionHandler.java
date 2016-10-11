package main.model.socket;

import main.model.Client;
import main.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

import static main.model.socket.Server.BUFFER_SIZE;
import static main.model.socket.Server.DEFAULT_FILES_PATH;

/**
 * Created by Anton on 09.10.2016.
 */
public class ReadWriteFileCompletionHandler implements CompletionHandler<Integer, Void> {
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
            readFileFromSocket(bytesRead);
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
    public void readFileFromSocket(int bytes) {
        String DirectoryPath = DEFAULT_FILES_PATH + "/" + mClient.getDir().getmToken() + "/";
        File theDir = new File(DirectoryPath);
        if (!theDir.exists()) theDir.mkdir();

        try (RandomAccessFile aFile = new RandomAccessFile(DirectoryPath + mFile.getName(), "rw")) {

            FileChannel fileChannel = aFile.getChannel();

            long readBytes = bytes;
            while (readBytes < mFile.getSize()) {
                readBytes += mChannel.read(mInputBuffer).get();
                mInputBuffer.flip();
                fileChannel.write(mInputBuffer);
                mInputBuffer.clear();
            }

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
    }
}
