package main.model;

/**
 * Created by Anton on 01.10.2016.
 */
public class Dir {
    private boolean dirUpdates;
    private String  mToken;

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
}
