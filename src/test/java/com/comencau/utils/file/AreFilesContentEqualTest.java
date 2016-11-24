package com.comencau.utils.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.comencau.utils.file.FileUtils.areFilesContentEqual;
import static com.comencau.utils.file.FileUtils.getFileFromClassPath;

/**
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class AreFilesContentEqualTest {

    @Test
    public void test() throws IOException {
        File f1 = getFileFromClassPath("test1.txt");
        File f2 = getFileFromClassPath("test1.txt");
        boolean b = areFilesContentEqual(f1, f2);
        Assert.assertTrue(b);

        b = areFilesContentEqual(f1, getFileFromClassPath("test2.txt"));
        Assert.assertFalse(b);
    }

}
