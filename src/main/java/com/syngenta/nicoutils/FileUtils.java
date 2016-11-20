package com.syngenta.nicoutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Nicolas FABRE
 *
 */
public class FileUtils {

	/**
	 * Size of the buffers used to read file content
	 */
	public final static int FILE_BUFFER_SIZE = 1024 * 4;

	/**
	 * Get the number of lines in a file.
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getNumberOfLines(File file) throws IOException {
		if (!file.exists() || !file.isFile()) throw new IllegalArgumentException("Input is not a file");
		long n = 1;
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] b = new byte[FILE_BUFFER_SIZE];
			int r = -1;
			while ((r = fis.read(b)) != -1) {
				for (int i = 0; i < r; i++) {
					if (b[i] == '\n') n++;
				}
			}
		}
		String name = file.getName();

		return n;
	}

}
