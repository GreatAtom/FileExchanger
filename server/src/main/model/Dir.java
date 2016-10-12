package main.model;

import java.io.File;

import static main.model.socket.Server.DEFAULT_FILES_PATH;

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
        String directoryPath = DEFAULT_FILES_PATH + "/" + getmToken() + "/";
        File theDir = new File(directoryPath);
        if (!theDir.exists()) theDir.mkdir();
        return directoryPath;
    }
}
