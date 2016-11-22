package com.comencau.utils.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.comencau.utils.file.FileUtils.*;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class BufferedWriterTest {

    @Test
    public void test() throws IOException {
        File file = getFileFromClassPath("test1.txt");
        File test = File.createTempFile("test", ".txt");
        bufferedWriter(test, bw -> {
            lineHandler(file, (n, line) -> {
                bw.write(line);
                bw.newLine();
            });
        });
        Assert.assertTrue(areFilesContentEqual(file, test));
        test.delete();
    }

}
