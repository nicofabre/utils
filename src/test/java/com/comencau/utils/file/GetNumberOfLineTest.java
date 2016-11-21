package com.comencau.utils.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static com.syngenta.utils.file.FileUtils.bufferedReader;
import static com.syngenta.utils.file.FileUtils.getFileFromClassPath;
import static com.syngenta.utils.file.FileUtils.getNumberOfLines;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class GetNumberOfLineTest {

    @Test
    public void test() {
        File file = getFileFromClassPath("test1.txt");
        int numberOfLines = getNumberOfLines(file);
        Assert.assertEquals(5, numberOfLines);
    }

}
