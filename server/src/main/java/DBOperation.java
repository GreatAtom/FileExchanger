import ru.fileexchanger.server.dao.CommonDao;

/**
 * Created by Dmitry on 23.10.2016.
 */
public class DBOperation {
    public static void main(String[] args) {
        clearFileInfo();
    }

    private static void clearFileInfo() {
        new CommonDao().clearFileInfo();
    }
}
