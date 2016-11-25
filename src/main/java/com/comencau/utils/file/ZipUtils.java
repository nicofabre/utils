package com.comencau.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class to zip and unzip files.
 *
 * @author Nicolas FABRE
 * @since 2016-11-21
 */
public class ZipUtils {


    public static File zipFiles(boolean deleteSourceFiles, File... files) {
        File zipFile = FileUtils.tempZipFile();
        return zipFiles(zipFile, deleteSourceFiles, files);
    }


    public static File zipFiles(File... files) {
        return zipFiles(false, files);
    }

    /**
     * Zip files in a zip file.
     *
     * @param zipFile           The zip file which will contain the compressed files
     * @param deleteSourceFiles if the source files must be deleted
     * @param files             The source files to be zipped
     * @return The zip file (same file as the 1st argument)
     */
    public static File zipFiles(File zipFile, boolean deleteSourceFiles, File... files) {
        byte[] buffer = Config.getBuffer();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipFilesRecurse(zos, buffer, "", files);
            if (deleteSourceFiles) {
                for (File file : files) {
                    file.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipFile;
    }

    // Internal method to manage recursively zip folders.
    private static void zipFilesRecurse(ZipOutputStream zos, byte[] buffer, String path, File... files) throws IOException {
        int len;
        for (File file : files) {
            if (file.isDirectory()) {
                zipFilesRecurse(zos, buffer, path + file.getName() + "/", file.listFiles());
            } else {
                try (FileInputStream in = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(path + file.getName());
                    zos.putNextEntry(entry);
                    while ((len = in.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    /**
     * Unzip the zip file.
     *
     * @param zipFile       File to be unzipped
     * @param deleteZipFile If the zip file must be deleted
     * @return the folder where the unzipped content of the zip file has been written
     */
    public static File unzip(File zipFile, boolean deleteZipFile) {
        try {
            File tmp = Files.createTempDirectory("unzip").toFile();
            return unzip(zipFile, tmp, deleteZipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unzip the zip file.
     *
     * @param zipFile File to be unzipped
     * @return the folder where the unzipped content of the zip file has been written
     */
    public static File unzip(File zipFile) {
        return unzip(zipFile, false);
    }

    /**
     * Unzip the zip file in the given folder.
     *
     * @param zipFile File to be unzipped
     * @param destDir Folder where the content of the zip file must be written in
     * @return The folder containing the unzipped content of the zip file (same as 2nd argument)
     */
    public static File unzip(File zipFile, File destDir) {
        return unzip(zipFile, destDir, false);
    }

    /**
     * Unzip the zip file in the given folder.
     *
     * @param zipFile       File to be unzipped
     * @param destDir       Folder where the content of the zip file must be written in
     * @param deleteZipFile If the zip file must be deleted
     * @return The folder containing the unzipped content of the zip file (same as 2nd argument)
     */
    public static File unzip(File zipFile, File destDir, boolean deleteZipFile) {
        if (!destDir.exists()) destDir.mkdir();
        if (destDir.isFile()) throw new IllegalArgumentException(destDir + " is not a directory");
        int length = destDir.listFiles().length;
        if (length > 0) throw new IllegalArgumentException(destDir + " is not empty");
        try (FileInputStream fis = new FileInputStream(zipFile); ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(destDir, ze.getName());
                f.getParentFile().mkdirs();
                byte[] buffer = Config.getBuffer();
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (deleteZipFile) zipFile.delete();
        return destDir;
    }

}
