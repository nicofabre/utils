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
 * @since 2016-11-21
 */
public class ZipTest {

    @Test
    public void test() throws IOException {
        // Zip
        File testDir = getFileFromClassPath("testDir");
        File zipFile = ZipUtils.zipFiles(File.createTempFile("test", ".zip"), false, testDir);

        // Test by comparing the unzip result with the initial folder
        File unzipDir = ZipUtils.unzip(zipFile, true);
        boolean b = areDirectoriesContentEqual(testDir, new File(unzipDir, testDir.getName()));
        Assert.assertTrue(b);
        deleteDirectoryRecursively(unzipDir);
    }

}
