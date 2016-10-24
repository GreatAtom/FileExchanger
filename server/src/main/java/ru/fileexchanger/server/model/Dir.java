package ru.fileexchanger.server.model;

import ru.fileexchanger.server.model.socket.Server;

import java.io.File;

/**
 * Created by Anton on 01.10.2016.
 */
public class Dir {
    private boolean dirUpdates;
    private String mToken;

    public Dir(String token) {
        mToken = token;
        dirUpdates = true;
    }

    public boolean isDirUpdates() {
        return dirUpdates;
    }

    public void setDirUpdates(boolean dirUpdates) {
        this.dirUpdates = dirUpdates;
    }

    public String getmToken() {
        return mToken;
    }

    public String getPath() {
        //String directoryPath = Server.DEFAULT_FILES_PATH + "/" + getmToken() + "/";
        String directoryPath = Server.DEFAULT_FILES_PATH;//все файлы будут в одном месте, иначе придётся усложнять логику генерирования таблицы для общих файлов
        File theDir = new File(directoryPath);
        if (!theDir.exists()) theDir.mkdir();
        return directoryPath;
    }
}
