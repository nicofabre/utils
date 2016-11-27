package com.comencau.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class TransposeMatrixUtils {

    public static void method1() {
        //TODO
    }

    private static final Logger logger = LoggerFactory.getLogger(TransposeMatrixUtils.class);

    // Max number of bytes of the src file involved in each chunk
    public static int MAX_BYTES_PER_CHUNK = 1024 * 10_000;// 10 MB

    public static File transposeMatrix(File srcFile, String separator) throws IOException {
        File output = File.createTempFile("output", ".txt");
        transposeMatrix(srcFile, output, separator);
        return output;
    }

    public static void transposeMatrix(File src, File dest, String separator) throws IOException {
        int nbColumns = getNumberOfColumns(src, separator);
        long bytesPerColumn = src.length() / nbColumns;// rough assessment of bytes par column
        int nbColsPerChunk = (int) (MAX_BYTES_PER_CHUNK / bytesPerColumn);// number of columns per chunk according to the limit of bytes to be used per chunk
        if (nbColsPerChunk == 0) nbColsPerChunk = 1;// in case a single column has more bytes than the limit ...
        double nbChunks = Math.ceil((double) nbColumns / nbColsPerChunk);
        logger.debug("file length : {} bytes. nb columns : {}. max bytes per chunk : {}. nb columns per chunk : {}. nb chunks : {}.",
                src.length(), nbColumns, MAX_BYTES_PER_CHUNK, nbColsPerChunk, nbChunks);
        try (FileWriter fw = new FileWriter(dest); java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
            int n = 1;
            for (int i = 0; i < nbColumns; i += nbColsPerChunk) {
                int lastColumnIndex = Math.min(nbColumns, i + nbColsPerChunk);
                logger.debug("chunk {} / {}. chunk for column {} to {} among {}", n, nbChunks, i, lastColumnIndex, nbColumns);
                writeColumnsInRows(src, bw, separator, i, lastColumnIndex);
                n++;
            }
        }
    }

    private static void writeColumnsInRows(File srcFile, BufferedWriter bw, String separator, int offset, int lastColumnIndex) throws IOException {
        List<String>[] newRows;
        try (FileReader fr = new FileReader(srcFile); BufferedReader br = new BufferedReader(fr)) {
            newRows = new List[lastColumnIndex - offset];
            for (int i = 0; i < newRows.length; i++) newRows[i] = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(separator, -1);
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
    }

    private static int getNumberOfColumns(File file, String separator) throws IOException {
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            return br.readLine().split(separator).length;
        }
    }

}
