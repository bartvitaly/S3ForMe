package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;

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
			e.printStackTrace();
		}
		return fileData.toString();

	}

	public static String getMd5(String filePath) {
		Path path = Paths.get(filePath);
		String md5 = "";

		try {
			md5 = DigestUtils.md5Hex(Files.readAllBytes(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return md5;
	}

	public static byte[] getPartBytes(String filePath) {
		Path path = Paths.get(filePath);
		byte[] data;

		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return data;
	}

	public static File getPart(String filePath, String filePathNew,
			long position, long size) {
		Path path = Paths.get(filePath);
		byte[] bytes;
		byte[] bytesNew = new byte[(int) size];

		try {
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		for (long i = position; i < position + size; i++) {
			bytesNew[(int) i] = bytes[(int) i];
		}

		bytesToFile(filePathNew, bytesNew);

		return new File(filePathNew);
	}

	public static void bytesToFile(String filePath, byte[] bytes) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(filePath);
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static long getLength(String filePath) {
		File file = new File(filePath);
		return file.length();
	}

}
