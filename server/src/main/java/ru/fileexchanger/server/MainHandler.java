package ru.fileexchanger.server;

import com.google.gson.Gson;
import ru.fileexchanger.common.AsynchronousSocketChannelProxy;
import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.common.json.SharedForm;
import ru.fileexchanger.common.json.UserFileEnity;
import ru.fileexchanger.common.json.UserInfo;
import ru.fileexchanger.server.dao.CommonDao;
import ru.fileexchanger.server.model.Client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static ru.fileexchanger.server.model.socket.Server.BUFFER_SIZE_FOR_FILE;

/**
 * Created by Dmitry on 19.10.2016.
 */
public class MainHandler extends Thread {
    private Client client;
    //private AsynchronousSocketChannel socketChannel;
    private AsynchronousSocketChannelProxy socketChannel;
    private CommonDao commonDao;
    private boolean run;

    public MainHandler(Client client, AsynchronousSocketChannelProxy socketChannel) {
        ServerMain.log("MainHandler has created");
        this.client = client;
        this.socketChannel = socketChannel;
        this.run = true;
        this.commonDao = new CommonDao();
    }

    @Override
    public void run() {
        try {
            while (run) {
                ServerMain.log("Wating code");
                String code = SocketUtil.readMessage(socketChannel, 3, 60 * 15);
                ServerMain.log("cod: " + code);
                switch (code) {
                    case SocketUtil.SEND_FILE_CODE:
                        readNewFile();
                        break;
                    case SocketUtil.SEND_EXIST_FILE_CODE:
                        readExistFile();
                        break;
                    case SocketUtil.SEND_FILE_INFO_CODE:
                        sendFilesInfo();
                        break;
                    case SocketUtil.MAKE_PRIVATE_FILES_CODE:
                        makePrivateFiles();
                        break;
                    case SocketUtil.SHARE_FILES_CODE:
                        shareFiles();
                        break;
                    case SocketUtil.DOWNLOAD_FILE_CODE:
                        sendFileForDowload();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareFiles() throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        String json = readJson();
        SharedForm sharedForm = new Gson().fromJson(json, SharedForm.class);
        ServerMain.log("Good JSON");
        commonDao.sharedFiles(sharedForm.getFilesIds(), sharedForm.getLogins());
    }

    private void makePrivateFiles() throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        String json = readJson();
        SharedForm sharedForm = new Gson().fromJson(json, SharedForm.class);
        ServerMain.log("Good JSON");
        commonDao.makePrivate(sharedForm.getFilesIds());
    }


    private void sendFileForDowload() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String json = readJson();
        UserFileEnity userFileEnity = new Gson().fromJson(json, UserFileEnity.class);
        ServerMain.log("Good JSON");
        sendFile(new File(client.getFilePathById(userFileEnity.getId())), userFileEnity.getFileName());
    }

    private String readJson() throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        long length = SocketUtil.readLong(socketChannel);
        String json = SocketUtil.readMessage(socketChannel, (int) length);
        SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.GOOD_CODE);
        return json;
    }

    private void sendFilesInfo() {
        try {
            ServerMain.log("Try to send file info");
            ServerMain.log("loading file info from DB");
            UserInfo userInfo = new UserInfo();
            userInfo.setFileEnityList(getUserFilesEnitry(client));
            userInfo.setUsers(getUsersLogin(client.getmLogin()));
            userInfo.setSharedFileEnityList(getSharedFilesByLogin(client.getmLogin()));
            ServerMain.log("Load: 100%");
            ServerMain.log("Parsing...");
            Gson gson = new Gson();
            ServerMain.log("Try to send: " + gson.toJson(userInfo));
            String json = gson.toJson(userInfo);
            SocketUtil.sendLong(socketChannel.getSocketChannel(), json.length());
            SocketUtil.sendMessage(socketChannel.getSocketChannel(), json);
            ServerMain.log("File info has send");
        } catch (Exception e) {
            e.printStackTrace();
            ServerMain.log("Не удалось отрпавить информацию о файлах");
        }
    }

    private List<UserFileEnity> getSharedFilesByLogin(String login) throws SQLException {
        List<UserFileEnity> files = commonDao.loadSharedFilesForUsers(login);
        files.forEach(f -> {
            String filePath = client.getFilePathById(f.getId());
            File file = new File(filePath);
            if (file.isFile()) {
                f.setDownloadSize(file.length());
            }
        });
        return files;
    }

    private List<UserFileEnity> getUserFilesEnitry(Client client) throws SQLException {
        List<UserFileEnity> files = commonDao.loadUserFile(client.getmLogin());
        files.forEach(f -> {
            String filePath = client.getFilePathById(f.getId());
            File file = new File(filePath);
            if (file.isFile()) {
                f.setDownloadSize(file.length());
            }
        });
        return files;
    }

    private List<String> getUsersLogin(String login) throws SQLException {
        return commonDao.loadUsers(login);
    }

    private void readNewFile() throws SQLException, InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException {
        String fileName = SocketUtil.readMessage(socketChannel, SocketUtil.FILE_NAME_LENGTH).trim();
        long fileSize = Long.valueOf(SocketUtil.readMessage(socketChannel, SocketUtil.FILE_SIZE_LENGTH).trim());
        long id = commonDao.insertFile(client.getmLogin(), fileName, fileSize);
        readFile(0, id, fileSize);
    }

    private void readExistFile() throws InterruptedException, ExecutionException, TimeoutException, UnsupportedEncodingException, SQLException {
        long id = Long.valueOf(SocketUtil.readMessage(socketChannel, 6, 60 * 15).trim());
        File file = new File(client.getFilePathById(id));
        long fileSize = commonDao.geFileSize(id);
        long fileLength = file.length();
        readFile(fileLength, id, fileSize);
    }

    private void readFile(long downloaded, long fileId, long fileSize) {
        ServerMain.log("receive file");
        try {
            try (RandomAccessFile aFile = new RandomAccessFile(client.getFilePathById(fileId), "rw")) {
                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE_FOR_FILE);
                FileChannel fileChannel = aFile.getChannel();
                fileChannel.position(downloaded);
                long readBytes = downloaded;
                SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.GOOD_SEND_FILE_CODE);
                do {
                    long read = socketChannel.read(inputBuffer);
                    readBytes += (read > 0) ? read : 0;
                    ServerMain.log("read=" + read + "; readBytes=" + readBytes+"; fileSize="+fileSize);
                    inputBuffer.flip();
                    fileChannel.write(inputBuffer);
                    inputBuffer.clear();
                } while ((readBytes < fileSize));
                ServerMain.log("File has received");
            } catch (InterruptedException | ExecutionException | IOException e) {

            }
            SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.GOOD_CODE);
        } catch (Exception e) {
            try {
                SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.DONT_SEND_FILE_CODE);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void sendFile(File file, String fileName) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ServerMain.log("Try to send file to client: " + file.getName());
        SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.format(fileName, SocketUtil.FILE_NAME_LENGTH));
        SocketUtil.sendMessage(socketChannel.getSocketChannel(), SocketUtil.format(file.length(), SocketUtil.FILE_SIZE_LENGTH));

        RandomAccessFile aFile = new RandomAccessFile(file, "r");
        FileChannel inChannel = aFile.getChannel();

        String code = SocketUtil.readMessage(socketChannel, 3, 60 * 15);
        if (code.equals(SocketUtil.GOOD_SEND_FILE_CODE)) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE_FOR_FILE);
            int sumSend = 0;
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                int send = socketChannel.getSocketChannel().write(buffer).get();
                sumSend+=send;
                //ServerMain.log("sumSend="+sumSend+";  send="+send);
                buffer.clear();
                sleep(1);
            }
            aFile.close();

            String mess = "";
            ServerMain.log("Try to read answer from client");
            while (!mess.equals(SocketUtil.GOOD_CODE)) {
                mess = SocketUtil.readMessage(socketChannel, 3, 60 * 15);
            }
        } else {
            ServerMain.log("client is not ready to receive file");
        }
    }
}
