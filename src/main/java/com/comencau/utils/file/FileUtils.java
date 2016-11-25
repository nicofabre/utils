package com.comencau.utils.file;

import com.comencau.utils.function.BiConsumer;
import com.comencau.utils.function.Consumer;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class to handle Files.
 *
 * @author Nicolas FABRE
 * @since 2016-11-18
 */
public class FileUtils {

    /**
     * Size of the buffers used to read file content
     */
    public final static int FILE_BUFFER_SIZE = 1024 * 8;

    /**
     * The Desktop folder
     */
    public static final File DESKTOP = new File(System.getProperty("user.home") + "/Desktop");

    /**
     * Create a temporary file ending with ".tmp"
     *
     * @return a temporary file.
     */
    public static File tempFile() {
        try {
            return File.createTempFile("tmp_", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a temporary file ending with ".zip"
     *
     * @return a temporary file with ".zip" extension.
     */
    public static File tempZipFile() {
        try {
            return File.createTempFile("tmp_", ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the number of lines in a file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static int getNumberOfLines(File file) {
        if (!file.exists() || !file.isFile()) throw new IllegalArgumentException("Input is not a file");
        int n = 1;
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] b = new byte[FILE_BUFFER_SIZE];
            int r = -1;
            while ((r = fis.read(b)) != -1) {
                for (int i = 0; i < r; i++) {
                    if (b[i] == '\n') n++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return n;
    }

    /**
     * Create a buffered reader to read the given file and pass it to a callback to handle it.
     *
     * @param f
     * @param bufferedReaderHandler
     */
    public static void bufferedReader(File f, Consumer<BufferedReader> bufferedReaderHandler) {
        try (BufferedReader br = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
            bufferedReaderHandler.accept(br);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void lineHandler(File f, BiConsumer<Integer, String> lineHandler) {
        bufferedReader(f, br -> {
            String line;
            int n = 1;
            while ((line = br.readLine()) != null) {
                lineHandler.accept(n, line);
                n++;
            }
        });
    }

    /**
     * Creates a BufferedWriter for the given file and give it to a Consumer to be handled.
     *
     * @param file                  the file to be written with a BufferedWriter
     * @param bufferedWriterHandler Consumer to handle the BufferedWriter
     */
    public static void bufferedWriter(File file, Consumer<BufferedWriter> bufferedWriterHandler) {
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bufferedWriterHandler.accept(bw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a temporary file and a BufferedWriter to write within. Gives the BufferedWriter to a callback
     * Consumer to handle it.
     *
     * @param bufferedWriterHandler Consumer as a callback handler for the BufferedWriter to write in the file
     * @return the file that has been written
     */
    public static File bufferedWriter(Consumer<BufferedWriter> bufferedWriterHandler) {
        File file = FileUtils.tempFile();
        bufferedWriter(file, bufferedWriterHandler);
        return file;
    }

    /**
     * Get a file from the classpath.
     *
     * @param path path relative to the classloader
     * @return the matching file
     */
    public static File getFileFromClassPath(String path) {
        URL url = ClassLoader.getSystemResource(path);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if 2 files have the same content. The checking is done byte by byte.
     *
     * @param f1 1st file to be compared
     * @param f2 2nd file to be compared
     * @return true if the 2 files have exactly the same bytes, false otherwise
     */
    public static boolean areFilesContentEqual(File f1, File f2) {
        if (!f1.isFile()) throw new IllegalArgumentException(f1 + " is not a file");
        if (!f2.isFile()) throw new IllegalArgumentException(f2 + " is not a file");
        try (FileInputStream fis1 = new FileInputStream(f1); FileInputStream fis2 = new FileInputStream(f2)) {
            byte[] b1 = new byte[FILE_BUFFER_SIZE];
            byte[] b2 = new byte[FILE_BUFFER_SIZE];
            int r1 = -1;
            while ((r1 = fis1.read(b1)) != -1) {
                int r2 = fis2.read(b2);
                if (r1 != r2) return false;
                for (int i = 0; i < r1; i++) {
                    if (b1[i] != b2[i]) return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Directories are equal if they contain the same files. By same file, it is meant that files have the same name and
     * the same content. The method is recursive and works with sub folders.
     *
     * @param d1 1st directory to be compared
     * @param d2 2nd directory to be compared
     * @return true if the 2 directories are equal (according to the definition above), false otherwise
     */
    public static boolean areDirectoriesContentEqual(File d1, File d2) {
        if (!d1.isDirectory()) throw new IllegalArgumentException(d1 + " is not a directory");
        if (!d2.isDirectory()) throw new IllegalArgumentException(d2 + " is not a directory");
        Map<String, File> files1 = Arrays.asList(d1.listFiles()).stream().collect(Collectors.toMap(File::getName, Function.identity()));
        Map<String, File> files2 = Arrays.asList(d2.listFiles()).stream().collect(Collectors.toMap(File::getName, Function.identity()));
        if (!files1.keySet().equals(files2.keySet())) return false;
        for (Map.Entry<String, File> e1 : files1.entrySet()) {
            File f1 = e1.getValue();
            File f2 = files2.get(e1.getKey());
            if (f1.isDirectory()) {
                if (!f2.isDirectory()) return false;
                if (!areDirectoriesContentEqual(f1, f2)) return false;
            } else if (f1.isFile()) {
                if (!f2.isFile()) return false;
                if (!areFilesContentEqual(f1, f2)) {
                    System.out.println(f1 + " / " + f2);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Recursively delete a directory
     *
     * @param dir the directory to be deleted
     */
    public static void deleteDirectoryRecursively(File dir) {
        if (!dir.exists() || !dir.isDirectory()) throw new IllegalArgumentException(dir + " is not a directory");
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) deleteDirectoryRecursively(file);
            else file.delete();
        }
        dir.delete();
    }

    /**
     * Move the file to the user's desktop (Windows). The file keeps the same name.
     *
     * @param f
     */
    public static File moveFileOnDesktop(File f) {
        File newFile = new File(System.getProperty("user.home") + "/Desktop", f.getName());
        f.renameTo(newFile);
        return newFile;
    }

}
