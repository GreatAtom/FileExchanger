import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

class FileSender {
    public static void main(String[] args) {
        FileSender nioClient = new FileSender();
        SocketChannel socketChannel = nioClient.createChannel();
        nioClient.sendFile(socketChannel);

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

            Thread.currentThread().sleep(100);
             str = "fileRec";
             a = ByteBuffer.wrap(str.getBytes(), 0, 7);
            socketChannel.write(a);

            Thread.currentThread().sleep(100);
            str = "myRoot.zip";
            a = ByteBuffer.wrap(str.getBytes(), 0, 10);
            socketChannel.write(a);

            Thread.currentThread().sleep(100);
            str = "52994973";
            a = ByteBuffer.wrap(str.getBytes(), 0, 8);
            socketChannel.write(a);

            Thread.currentThread().sleep(100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return socketChannel;
    }


    public void sendFile(SocketChannel socketChannel) {
        RandomAccessFile aFile = null;
        try {
            File file = new File("D://Anton/download/root.zip");
            System.out.println(file.length());
            aFile = new RandomAccessFile(file, "r");
            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(655360);
            while (inChannel.read(buffer) > 0) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
            Thread.sleep(10000);
            System.out.println("End of file reached..");

            socketChannel.close();
            aFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}