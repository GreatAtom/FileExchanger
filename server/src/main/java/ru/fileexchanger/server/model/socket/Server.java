package ru.fileexchanger.server.model.socket;

import ru.fileexchanger.server.ServerMain;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anton on 01.10.2016.
 * launches main stream for communication witch client connection through AcceptCompletionHandler,
 * keeps all client connections
 */
public class Server extends Thread {
    public static final int BUFFER_SIZE_FOR_FILE = 65536;
    public static final int BUFFER_SIZE = 1024;
    public static final String DEFAULT_FILES_PATH = "data/";
    /**
     * List of current connections
     */
    private AsynchronousServerSocketChannel mListener;
    private final List<AsynchronousSocketChannel> mConnections = Collections.synchronizedList(new LinkedList<AsynchronousSocketChannel>());
    private int mPORT;
    private ServerSocket mServerSocket = null;

    public Server(int port) {
        mPORT = port;
    }

    public synchronized void addClient(AsynchronousSocketChannel client) {
        mConnections.add(client);
        List<String> temp = mConnections.stream()
                .filter(e -> e != null)
                .map(AsynchronousSocketChannel::toString)
                .collect(Collectors.toList());
        ServerMain.updateListView(temp);
    }

    public synchronized void removeClient(AsynchronousSocketChannel client) {
        mConnections.remove(client);
        List<String> temp = mConnections.stream()
                .filter(e -> e != null)
                .map(AsynchronousSocketChannel::toString)
                .collect(Collectors.toList());
        ServerMain.updateListView(temp);
    }

    @Override
    public void run() {
        try {
            mListener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(mPORT));
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(mListener, this);
            mListener.accept(null, acceptCompletionHandler);
        } catch (IOException e) {
            e.printStackTrace();
            closeServer();
        }
    }

    public boolean closeServer() {
        try {

            for (AsynchronousSocketChannel con : mConnections) {
                con.close();
            }

            mConnections.clear();

            if (mListener.isOpen()) {
                mListener.close();
            }

            ServerMain.updateListView(new ArrayList<String>(0));

            System.gc();

            System.out.println("Server has stopped");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}