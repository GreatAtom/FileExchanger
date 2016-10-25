package ru.fileexchanger.server.model;

import ru.fileexchanger.server.ServerMain;

/**
 * Created by Dmitry on 02.10.2016.
 */
public class FileInfo {

    private long size;
    private String name;

    private FileInfo(String name, long size) {
        this.size = size;
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public static FileInfo tryBildFileInfo(String name, String sSize) {
        try {
            long size = Long.parseLong(sSize);
            return new FileInfo(name, size);
        } catch (NumberFormatException nfe) {
            ServerMain.log("NumberFormatException: " + nfe.getMessage());
        }
        return null;
    }
}