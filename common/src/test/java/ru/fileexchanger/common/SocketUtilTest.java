package ru.fileexchanger.common;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dmitry on 19.10.2016.
 */
public class SocketUtilTest {
    @Test
    public void format() throws Exception {
        assertEquals(SocketUtil.format("login", 10), "login     ");
        assertEquals(SocketUtil.format("login", 4), "logi");
    }

}