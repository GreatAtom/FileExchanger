package ru.fileexchanger.server;

import com.google.gson.Gson;
import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.common.UserFileEnity;
import ru.fileexchanger.common.UserInfo;
import ru.fileexchanger.server.dao.CommonDao;
import ru.fileexchanger.server.model.Client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static ru.fileexchanger.server.model.socket.Server.BUFFER_SIZE_FOR_FILE;

/**
 * Created by Dmitry on 19.10.2016.
 */
public class MainHandler extends Thread {
    private Client client;
    private AsynchronousSocketChannel socketChannel;
    private CommonDao commonDao;
    private boolean run;

    public MainHandler(Client client, AsynchronousSocketChannel socketChannel) {
        System.out.println("MainHandler has created");
        this.client = client;
        this.socketChannel = socketChannel;
        this.run = true;
        this.commonDao = new CommonDao();
        sendFilesInfo(socketChannel, client);
    }

    @Override
    public void run() {
        try {
            while (run) {
                System.out.println("Wating code");
                String code = SocketUtil.readMessage(socketChannel, 3, 60 * 15);
                System.out.println("cod: " + code);
                switch (code) {
                    case SocketUtil.SEND_FILE_CODE: readFile(); break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFilesInfo(AsynchronousSocketChannel socketChannel, Client client) {
        try {
            System.out.println("Try to send file info");
            System.out.println("loading file info from DB");
            List<UserFileEnity> files = getUserFilesEnitry(client);
/*            if (files == null) {
                UserFileEnity f = new UserFileEnity();
                f.setId(0);
                f.setFileSize(0);
                f.setDownloadSize(0);
                f.setUserLogin("-");
                f.setFileName("-");
                files = new ArrayList<>(1);
                files.add(f);
            }*/
            UserInfo userInfo = new UserInfo();
            userInfo.setFileEnityList(files);
            System.out.println("Load: 100%");
            System.out.println("Parsing...");
            Gson gson = new Gson();
            System.out.println("Try to send: " + gson.toJson(userInfo));
            SocketUtil.sendMessage(socketChannel, gson.toJson(userInfo));
            System.out.println("File info has send");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Не удалось отрпавить информацию о файлах");
        }
    }

    private List<UserFileEnity> getUserFilesEnitry(Client client) throws SQLException {
        List<UserFileEnity> files = commonDao.loadUserFile(client.getmLogin());
        files.stream().forEach(f->{
            String filePath = client.getFilePathById(f.getId());
            File file = new File(filePath);
            if(file.isFile()){
                f.setDownloadSize(file.length());
            }
        });
        return files;
    }

    private void readFile() {
        System.out.println("receive file");
        try {
            String fileName = SocketUtil.readMessage(socketChannel, SocketUtil.FILE_NAME_LENGTH).trim();
            long fileSize = Long.valueOf(SocketUtil.readMessage(socketChannel, SocketUtil.FILE_SIZE_LENGTH).trim());
            long id = commonDao.insertFile(client.getmLogin(), fileName, fileSize);
            System.out.println(fileName + " " + fileSize+" id: "+id);

            try (RandomAccessFile aFile = new RandomAccessFile(client.getFilePathById(id), "rw")) {
                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE_FOR_FILE);
                FileChannel fileChannel = aFile.getChannel();

                long readBytes = 0;
                SocketUtil.sendMessage(socketChannel, SocketUtil.GOOD_SEND_FILE_CODE);
                do {
                    long read = socketChannel.read(inputBuffer).get();
                    readBytes += (read > 0) ? read : 0;
                    System.out.println("read=" + read + "; readBytes=" + readBytes);
                    inputBuffer.flip();
                    fileChannel.write(inputBuffer);
                    inputBuffer.clear();
                } while ((readBytes < fileSize));
                System.out.println("File has received");
            } catch (InterruptedException | ExecutionException | IOException e) {

            }
            SocketUtil.sendMessage(socketChannel, SocketUtil.GOOD_CODE);
        } catch (Exception e) {
            try {
                SocketUtil.sendMessage(socketChannel, SocketUtil.DONT_SEND_FILE_CODE);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
