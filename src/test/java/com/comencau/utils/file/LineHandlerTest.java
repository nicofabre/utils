package com.comencau.utils.file;

import org.junit.Test;

import java.io.File;

import static com.syngenta.utils.file.FileUtils.getFileFromClassPath;
import static com.syngenta.utils.file.FileUtils.lineHandler;
import static org.junit.Assert.assertEquals;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class LineHandlerTest {

    @Test
    public void test() {
        File file = getFileFromClassPath("test1.txt");
        lineHandler(file, (n, line) -> {
            if (n == 2) {
                assertEquals("2 B", line);
            }
        });
    }

}
