package ru.fileexchanger.server.model.socket;

import ru.fileexchanger.server.model.Client;
import ru.fileexchanger.server.model.FileInfo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static ru.fileexchanger.server.model.socket.Server.BUFFER_SIZE_FOR_FILE;
import static ru.fileexchanger.server.model.socket.Server.reedLineFromClient;

/**
 * Created by Anton on 29.09.2016.
 * read client message
 */
class ReadWriteCompletionHandler implements CompletionHandler<Integer, Void> {

    private AsynchronousSocketChannel mChannel;
    private Server mServer;
    private Client mClient;
    private ByteBuffer mInputBuffer;

    public ReadWriteCompletionHandler(AsynchronousSocketChannel channel, ByteBuffer inputBuffer,
                                      Server server, Client client) {
        mChannel = channel;
        mServer = server;
        mClient = client;
        mInputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Void attachment) {

        if (mClient.getDir().isDirUpdates()) {
            mChannel.write(ByteBuffer.wrap(((String) "README.MD").getBytes()));
            mClient.getDir().setDirUpdates(false);
        }

        if (bytesRead < 1 || !mChannel.isOpen()) {
            System.out.println("Closing connection to " + mChannel);
            mServer.removeClient(mChannel);
        } else {

            byte[] buffer = new byte[bytesRead];
            mInputBuffer.rewind();
            mInputBuffer.get(buffer);
            byte[] decodeFrame = buffer;
            String message = new String(decodeFrame);
            mInputBuffer.clear();

            if (message.equals("fileRec")) {
                System.out.println("receive file");

                try {
                    String fileName = reedLineFromClient(mChannel);
                    String fileSize = reedLineFromClient(mChannel);
                    System.out.println(fileName + " " + fileSize);
                    FileInfo fileInfo = FileInfo.tryBildFileInfo(fileName, fileSize);

                    if (fileInfo == null)
                        throw new InternalError();

                    ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE_FOR_FILE);
                    ReadWriteFileCompletionHandler readWriteFileCompletionHandler =
                            new ReadWriteFileCompletionHandler(mChannel, inputBuffer, mServer, mClient, fileInfo);
                    mChannel.read(inputBuffer, null, readWriteFileCompletionHandler);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Closing connection to " + mChannel);
                    mServer.removeClient(mChannel);
                } catch (TimeoutException e) {
                    System.out.println("close connection");
                    mServer.removeClient(mChannel);
                }
            } else {
                System.out.println("Received message from " + ":" + message);
                mChannel.read(mInputBuffer, null, this);
            }
        }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        //
    }
}