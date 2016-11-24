package com.comencau.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TransposeMatrixUtils {

    private static final Logger logger = LoggerFactory.getLogger(TransposeMatrixUtils.class);

    // Max number of bytes of the src file involved in each chunk
    public static int MAX_BYTES_PER_CHUNK = 1024 * 100_000;// 50 MB

    public static File transposeMatrix(File srcFile, String separator) throws IOException {
        File output = File.createTempFile("output", ".txt");
        transposeMatrix(srcFile, output, separator);
        return output;
    }

    public static void transposeMatrix(File srcFile, File destFile, String separator) throws IOException {
        long bytesPerColumn = assessBytesPerColumn(srcFile, separator);// rough assessment of bytes par column
        int nbColsPerChunk = (int) (MAX_BYTES_PER_CHUNK / bytesPerColumn);// number of columns per chunk according to the limit of bytes to be used per chunk
        if (nbColsPerChunk == 0) nbColsPerChunk = 1;// in case a single column has more bytes than the limit ...
        logger.debug("file length : {} bytes. max bytes per chunk : {}. nb columns per chunk : {}.", srcFile.length(), MAX_BYTES_PER_CHUNK, nbColsPerChunk);
        try (FileWriter fw = new FileWriter(destFile); BufferedWriter bw = new BufferedWriter(fw)) {
            boolean remainingColumns = true;
            int offset = 0;
            while (remainingColumns) {
                remainingColumns = writeColumnsInRows(srcFile, bw, separator, offset, nbColsPerChunk);
                offset += nbColsPerChunk;
            }
        }
    }

    private static boolean writeColumnsInRows(File srcFile, BufferedWriter bw, String separator, int offset, int nbColumns) throws IOException {
        List<String>[] newRows;
        boolean remainingColumns = true;
        try (FileReader fr = new FileReader(srcFile); BufferedReader br = new BufferedReader(fr)) {
            String[] split0 = br.readLine().split(separator);
            if (split0.length <= offset + nbColumns) remainingColumns = false;
            int lastColumnIndex = Math.min(split0.length, offset + nbColumns);
            logger.debug("chunk for column {} to {} among {}", offset, lastColumnIndex, split0.length);
            newRows = new List[lastColumnIndex - offset];
            for (int i = 0; i < newRows.length; i++) {
                newRows[i] = new ArrayList<>();
                newRows[i].add(split0[i + offset]);
            }
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(separator);
                for (int i = 0; i < newRows.length; i++) {
                    newRows[i].add(split[i + offset]);
                }
            }
        }
        for (int i = 0; i < newRows.length; i++) {
            bw.write(newRows[i].get(0));
            for (int j = 1; j < newRows[i].size(); j++) {
                bw.write(separator);
                bw.write(newRows[i].get(j));
            }
            bw.newLine();
        }
        return remainingColumns;
    }

    private static long assessBytesPerColumn(File file, String separator) throws IOException {
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            int nbColumns = br.readLine().split(separator).length;
            return file.length() / nbColumns;
        }
    }

}
