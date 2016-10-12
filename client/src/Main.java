import client.FileSender;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by Anton on 12.10.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        FileSender nioClient = new FileSender();
        //SocketChannel socketChannel = nioClient.createChannel();
        //nioClient.sendFile(socketChannel, "D://Anton/download/root.zip");
        //Thread.sleep(10000);
        //socketChannel.close();
    }
}
