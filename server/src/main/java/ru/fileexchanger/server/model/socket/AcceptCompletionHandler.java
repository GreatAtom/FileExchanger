package ru.fileexchanger.server.model.socket;


import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.server.MainHandler;
import ru.fileexchanger.server.dao.CommonDao;
import ru.fileexchanger.server.model.Client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Anton on 02.10.2016.
 * in successful client connection launches async through ReadWriteCompletionHandler and WriteCompletionHandler
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private AsynchronousServerSocketChannel mListener;
    private Server mServer;
    private CommonDao commonDao;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener, Server server) {
        mListener = listener;
        mServer = server;
        commonDao = new CommonDao();
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
        System.out.println(this);
        System.out.println("client connected: " + socketChannel + " " + Thread.currentThread().getId());
        //mListener.accept(null, this); //не уверен, что это нужно

        try {
            String login = SocketUtil.formatUtf16(SocketUtil.readMessage(socketChannel, SocketUtil.LOGIN_LENGTH));
            String password = SocketUtil.formatUtf16(SocketUtil.readMessage(socketChannel, SocketUtil.LOGIN_LENGTH));

            Client client = tryCreateClient(login, password);
            if (client != null && mListener.isOpen()) {
                System.out.println(client.getmLogin() + " " + client.getmPassword() + " " + client.getmToken());
                mServer.addClient(socketChannel);
                SocketUtil.sendMessage(socketChannel, "200");
                new MainHandler(client, socketChannel);//.start();
                System.out.println("Работа пошла-поехала дальше");

                //ByteBuffer inputBuffer = ByteBuffer.allocateDirect(Server.BUFFER_SIZE);
                //ReadWriteCompletionHandler readWriteCompletionHandler = new ReadWriteCompletionHandler(socketChannel, inputBuffer, mServer, client);

                //socketChannel.read(inputBuffer, null, readWriteCompletionHandler);

            } else {
                SocketUtil.sendMessage(socketChannel, "403");
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

    @Override
    public void failed(Throwable arg0, Void arg1) {

    }

    public Client tryCreateClient(String login, String pass) {
        if (validUser(login, pass)) {
            MessageDigest md = null;
            try {
                String prepToken = login + pass + "SALT";
                byte[] prepTokenBytes = prepToken.getBytes("UTF-8");

                md = MessageDigest.getInstance("MD5");

                byte[] token = md.digest(prepTokenBytes);
                BigInteger bigInt = new BigInteger(1, token);
                String hashToken = bigInt.toString(16);
                // Now we need to zero pad it if you actually want the full 32 chars.
                while (hashToken.length() < 32) {
                    hashToken = "0" + hashToken;
                }

                return new Client(login, pass, hashToken);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean validUser(String login, String pass) {
        return commonDao.isValidUser(login, pass);
    }

}