package com.comencau.utils.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static com.comencau.utils.file.FileUtils.bufferedReader;
import static com.comencau.utils.file.FileUtils.getFileFromClassPath;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class BufferedReaderTest {

    @Test
    public void test() {
        File file = getFileFromClassPath("test1.txt");
        bufferedReader(file, br -> {
            String line;
            while ((line = br.readLine()) != null) {
                Assert.assertEquals("1 A", line);
                return;
            }
        });
    }

}
