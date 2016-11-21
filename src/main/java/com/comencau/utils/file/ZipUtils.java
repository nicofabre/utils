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
 * TODO
 *
 * @author Nicolas FABRE
 * @since 2016-11-21
 */
public class ZipUtils {

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

    public static File unzip(File zipFile, boolean deleteZipFile) {
        try {
            File tmp = Files.createTempDirectory("unzip").toFile();
            return unzip(zipFile, tmp, deleteZipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File unzip(File zipFile) {
        return unzip(zipFile, false);
    }

    public static File unzip(File zipFile, File destDir) {
        return unzip(zipFile, destDir, false);
    }

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
