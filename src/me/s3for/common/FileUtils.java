package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class FileUtils {

	public static String getRootPath() {
		try {
			return (new File(".")).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static File create(String name, String text) throws IOException {

		String[] fileName = name.split("\\.");

		File file = File.createTempFile(fileName[0], "." + fileName[1]);
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write(text);
		writer.close();

		return file;

	}

	public static void write(String path, String text) {

		try {
			PrintWriter writer = new PrintWriter(path);
			writer.println(text);
			writer.close();

		} catch (Exception e) {
		}
	}

	public static String read(String path) {

		StringBuffer fileData = new StringBuffer();
		char[] buf = new char[1024];
		int numRead = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileData.toString();

	}

}
