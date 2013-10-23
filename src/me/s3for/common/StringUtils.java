package me.s3for.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

	public static String inputStreamToString(InputStream input)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String finalLine = "";

		while (true) {
			String line = reader.readLine();
			finalLine += line;
			if (line == null) {
				break;
			}
		}

		return finalLine;
	}

}
