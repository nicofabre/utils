package com.comencau.utils.file;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-21
 */
public class Config {

    public final static int BUFFER_SIZE = 1024 * 4;

    public static byte[] getBuffer() {
        return new byte[BUFFER_SIZE];
    }

}
