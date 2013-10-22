package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesUtils {

	final static String TEST_PROPERTIES = "\\test.properties";

	public static String getProperty(String key) {

		Properties properties = new Properties();
		String result = "";
		try {
			properties.load(new BufferedReader(new FileReader((new File("."))
					.getCanonicalPath() + TEST_PROPERTIES)));
			result = properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
