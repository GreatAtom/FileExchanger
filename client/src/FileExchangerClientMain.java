import client.form.FileClient;
import client.services.CommonService;

/**
 * Created by Dmitry on 12.10.2016.
 */
public class FileExchangerClientMain {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FileClient(new CommonService()).start();
            }
        });

    }
}