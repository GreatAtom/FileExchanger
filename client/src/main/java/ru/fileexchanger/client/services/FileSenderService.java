package ru.fileexchanger.client.services;

import com.google.gson.Gson;
import ru.fileexchanger.common.Common;
import ru.fileexchanger.common.SocketUtil;
import ru.fileexchanger.common.json.SharedForm;
import ru.fileexchanger.common.json.UserFileEnity;
import ru.fileexchanger.common.json.UserInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class FileSenderService {

    private static final int FILE_BUFFER_SIZE = 65536;
    private SocketChannel socketChannel;
    private UserInfo userInfo;
    private volatile boolean emptyChanel = true;
    private Property property;
    private Informer informer;

    public void sendFile(String filePath) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if(emptyChanel) {
            emptyChanel = false;
            new Thread(() -> {
                try {
                    writeInfo("Идёт загрузка");
                    sendFileProxy(filePath);
                    emptyChanel = true;
                    informer.fileHasSend();
                    writeInfo("Загрузка завершена");
                } catch (IOException e) {
                    e.printStackTrace();
                    writeInfo("Ошибка. Передача файлов прервана");
                    informer.errorOfSendingFile();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    writeInfo("Ошибка. Передача файлов прервана");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    writeInfo("Ошибка. Передача файлов прервана");
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    writeInfo("Ошибка. Передача файлов прервана");
                }
                finally {
                    emptyChanel = true;
                }
            }).start();
        }
    }

    private void writeInfo(String s) {
        informer.writeMessage(s);

    }

    private void sendFileProxy(String filePath) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new FileNotFoundException("Файла по указанному пути не существует");
        }
        sendFile(file);
    }

    /**
     * Запрашивает у сервера список файлов юзера
     * @return
     * @throws IOException
     */
    public UserInfo getUpdatedUserInfo() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if(!emptyChanel){ return userInfo;}
        System.out.println("Try to read File Info");
        SocketUtil.sendMessage(socketChannel, SocketUtil.SEND_FILE_INFO_CODE);
        //String userInfoString = SocketUtil.readMessage(socketChannel);
        long size = SocketUtil.readLong(socketChannel);
        String userInfoString = SocketUtil.readMessage(socketChannel, (int)size);
        System.out.println("File info has read:\n"+ userInfoString);
        userInfo =  new Gson().fromJson(userInfoString, UserInfo.class);
        return userInfo;
    }


    /**
     * Establishes a socket channel connection
     *
     * @return
     */
    private SocketChannel createChannel(String login, String password, String host, int port) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if(!emptyChanel){ return null;}
        SocketChannel socketChannel = SocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        socketChannel.connect(socketAddress);
        System.out.println("Connected..Now sending the login and password");
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(login, SocketUtil.LOGIN_LENGTH));
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(password, SocketUtil.PASSWORD_LENGTH));
        System.out.println("Try to read aswer from server");
        String mess = SocketUtil.readCode(socketChannel);
        if(mess.equals(SocketUtil.GOOD_CONNECTION_CODE)) {
            System.out.println("Correct login and password");
            return socketChannel;
        } else {
            System.out.println("Message '"+mess+"' not equals ACCESS_IS_ALLOWED");
            System.out.println("Incorrect login or password");
            socketChannel.close();
            throw new AccessDeniedException("Incorrect login or password");
        }
    }

    public void createChanel(String login, String password) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        socketChannel = createChannel(login, password, property.getHost(), property.getPort());
        System.out.println("Chanel has created");
    }


    private void sendFile(File file) throws IOException, InterruptedException, ExecutionException, TimeoutException {

        System.out.println("Try to send file: "+file.getName());
        String fileName = file.getName();
        SocketUtil.sendMessage(socketChannel, SocketUtil.SEND_FILE_CODE);
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(fileName, SocketUtil.FILE_NAME_LENGTH));
        SocketUtil.sendMessage(socketChannel, SocketUtil.format(file.length(), SocketUtil.FILE_SIZE_LENGTH));

        //// TODO: 12.10.2016 получить инфо о размере файла
        long completeFileSize = 0;

        RandomAccessFile aFile = new RandomAccessFile(file, "r");
        FileChannel inChannel = aFile.getChannel();
        //inChannel.position(completeFileSize);//// TODO: 12.10.2016 проверить
        String code = SocketUtil.readCode(socketChannel);
        if(code.equals(SocketUtil.GOOD_SEND_FILE_CODE)) {
            ByteBuffer buffer = ByteBuffer.allocate(FILE_BUFFER_SIZE);
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                socketChannel.write(buffer);
                System.out.println("qqqq");
                buffer.clear();
            }
            aFile.close();

            String mess = "";
            System.out.println("Try to read answer from server");
            while (!mess.equals(SocketUtil.GOOD_CODE)){
                mess = SocketUtil.readCode(socketChannel);
            }
        } else {
            System.out.println("Server is not ready to receive file");
        }
    }

    public void shareFiles(List<Integer> fileIdsForShared, List<String> userLoginsForShared) {
        if(Common.isEmpty(fileIdsForShared) || Common.isEmpty(userLoginsForShared)) {
            System.out.println("Not selected files ids or users");
            return;
        }
        if(emptyChanel) {
            emptyChanel = false;
            try {
                shareFilesProxy(fileIdsForShared, userLoginsForShared);
                emptyChanel = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            finally {
                emptyChanel = true;
            }
        }
    }

    private void shareFilesProxy(List<Integer> fileIdsForShared, List<String> userLoginsForShared) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        SharedForm sharedForm = new SharedForm();
        sharedForm.setLogins(userLoginsForShared);
        sharedForm.setFilesIds(fileIdsForShared);
        String sharJson = new Gson().toJson(sharedForm);
        SocketUtil.sendMessage(socketChannel, SocketUtil.SHARE_FILES_CODE);
        SocketUtil.sendLong(socketChannel, sharJson.length());
        SocketUtil.sendMessage(socketChannel, sharJson);
        String code = SocketUtil.readCode(socketChannel);
    }

    public void makePrivate(List<Integer> fileIdsForShared) {
        if(Common.isEmpty(fileIdsForShared)) {
            System.out.println("Not selected files ids");
            return;
        }
        if(emptyChanel) {
            emptyChanel = false;
            try {
                makePrivateProxy(fileIdsForShared);
                emptyChanel = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            finally {
                emptyChanel = true;
            }
        }
    }

    private void makePrivateProxy(List<Integer> fileIdsForShared) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        SharedForm sharedForm = new SharedForm();
        sharedForm.setFilesIds(fileIdsForShared);
        String sharJson = new Gson().toJson(sharedForm);
        SocketUtil.sendMessage(socketChannel, SocketUtil.MAKE_PRIVATE_FILES_CODE);
        SocketUtil.sendLong(socketChannel, sharJson.length());
        SocketUtil.sendMessage(socketChannel, sharJson);
        String code =  SocketUtil.readCode(socketChannel);
    }

    public interface Informer {
        void writeMessage(String message);
        void fileHasSend();
        void errorOfSendingFile();
    }

    public void setInformer(Informer informer) {
        this.informer = informer;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}