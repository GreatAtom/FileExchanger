package ru.fileexchanger.server.model.socket;

import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.server.model.Client;
import ru.fileexchanger.server.model.FileInfo;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;

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
            try {
                readFileFromSocket(bytesRead);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mClient.getDir().setDirUpdates(true);
            System.out.println("End of file reached...");

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(Server.BUFFER_SIZE);
            /*ReadWriteCompletionHandler readWriteCompletionHandler =
                    new ReadWriteCompletionHandler(mChannel, inputBuffer, mServer, mClient);
            mChannel.read(inputBuffer, null, readWriteCompletionHandler);*/
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
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(SocketUtil.CHARSET_NAME), 0, message.getBytes(SocketUtil.CHARSET_NAME).length);
        mChannel.write(byteBuffer);

    }
}
