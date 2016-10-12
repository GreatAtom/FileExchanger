import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

class FileSender {

    public static final String SEND_FILE_FLAG = "fileRec";
    private static final int FILE_BUFFER_SIZE = 65536;

    public static void main(String[] args) {
        FileSender nioClient = new FileSender();
        SocketChannel socketChannel = nioClient.createChannel();
        try {
            nioClient.sendFile(socketChannel, "D://Anton/download/root.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendFile(SocketChannel socketChannel, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.isFile()) {
            throw new FileNotFoundException("Файла по указанному пути не существует");
        }
        sendFile(socketChannel, file);
    }

    /**
     * Establishes a socket channel connection
     *
     * @return
     */
    public SocketChannel createChannel() {

        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress("localhost", 8989);
            socketChannel.connect(socketAddress);
            System.out.println("Connected..Now sending the file");
            String str = "111111111";
            ByteBuffer a = ByteBuffer.wrap(str.getBytes(), 0, 9);
            socketChannel.write(a);
            a = ByteBuffer.wrap(str.getBytes(), 0, 9);

            Thread.currentThread().sleep(100);
            socketChannel.write(a);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return socketChannel;
    }


    public void sendFile(SocketChannel socketChannel, File file) throws IOException {

        String fileName = file.getName();
        String fileSize = String.valueOf(file.length());
        sendString(socketChannel, SEND_FILE_FLAG);
        sendString(socketChannel, fileName);

        sendString(socketChannel, fileSize);
        //// TODO: 12.10.2016 получить инфо о размере файла
        long completeFileSize = 0;

        RandomAccessFile aFile = new RandomAccessFile(file, "r");
        FileChannel inChannel = aFile.getChannel();
        //inChannel.position(completeFileSize);//// TODO: 12.10.2016 проверить

        ByteBuffer buffer = ByteBuffer.allocate(FILE_BUFFER_SIZE);
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            System.out.println("qqqq");
            buffer.clear();
        }
        String mess = "";
        ByteBuffer bufferedReader = ByteBuffer.allocate(1024);
        while (mess.equals("COMPLETED")) {
            socketChannel.read(bufferedReader);
            mess = bufferedReader.toString();
            bufferedReader.clear();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        socketChannel.close();
        aFile.close();

    }

    private void sendString(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes(), 0, message.getBytes().length);
        socketChannel.write(byteBuffer);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}