package com.comencau.utils.file;

import org.junit.Test;

import java.io.*;

import static com.comencau.utils.file.FileUtils.moveFileOnDesktop;

/**
 * @author Nicolas FABRE
 * @since 24/11/2016
 */
public class TransposeMatrixTest {

    private static final String sep = "\t";

    @Test
    public void smallTest() throws IOException {
        File file = buildFile(50, 10);
        file = moveFileOnDesktop(file);
        File out = TransposeMatrixUtils.transposeMatrix(file, sep);
        moveFileOnDesktop(out);
    }

    @Test
    public void bigTest() throws IOException {
        File file = buildFile(14000, 10000);
        file = moveFileOnDesktop(file);
        File out = TransposeMatrixUtils.transposeMatrix(file, sep);
        moveFileOnDesktop(out);
    }

    private File buildFile(int nbRows, int nbCols) throws IOException {
        File f = File.createTempFile("temp", ".tmp");
        int n = 1;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (int i = 0; i < nbRows; i++) {
                bw.write(String.valueOf(n++));
                for (int j = 1; j < nbCols; j++) {
                    bw.write(sep);
                    bw.write(String.valueOf(n++));
                }
                bw.newLine();
            }
        }
        return f;
    }


}
