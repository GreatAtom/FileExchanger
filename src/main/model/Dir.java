package main.model;

/**
 * Created by Anton on 01.10.2016.
 */
public class Dir {
    private boolean dirUpdates;

    public Dir(String token) {
        dirUpdates = true;
    }

    public boolean isDirUpdates() {
        return dirUpdates;
    }

    public void setDirUpdates(boolean dirUpdates) {
        this.dirUpdates = dirUpdates;
    }

    /*TODO  dir*/
}
