package tests;

import ru.fileexchanger.server.model.socket.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.net.Socket;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Anton on 29.09.2016.
 */
public class ServerTests {
    private static Server server;

    @Before
    public void beforeEachTest() {
        server = new Server(8989);
        server.run();
    }

    @Test
    public void testGenCHRONOLOGICAL() {
        try {
            Socket[] sockets = new Socket[100];
            PrintStream[] out = new PrintStream[100];

            for (int i = 0; i < 100; i++) {
                sockets[i] = new Socket("127.0.0.1", 8989);

                out[i] = new PrintStream(sockets[i].getOutputStream());

                out[i].print("testTest");
                Thread.sleep(100);
                out[i].print("testTest");
                Thread.sleep(500);
                //assertEquals(i+1, server.mConnections.size());
            }

            for (int i = 0; i < 100; i++) {
                sockets[i].close();
                out[i].close();
            }

            Thread.sleep(500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void testFileExist() {
        //assertEquals(0, server.mConnections.size());
       // server.closeServer();
    }

}