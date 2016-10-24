import ru.fileexchanger.server.dao.CommonDao;

import java.sql.SQLException;

/**
 * Created by Dmitry on 23.10.2016.
 */
public class DBOperation {
    public static void main(String[] args) throws SQLException {
        //clearFileInfo();
        //new CommonDao().insertUser("anton", "anton");
        //new CommonDao().insertUser("vasiliy", "vasiliy");
        //new CommonDao().makeQuery("create table shared (userLogin VARCHAR(32), fileId INT)");
    }

    private static void clearFileInfo() {
        new CommonDao().clearFileInfo();
    }
}
