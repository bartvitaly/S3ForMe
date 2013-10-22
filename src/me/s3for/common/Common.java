package me.s3for.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.amazonaws.services.s3.model.Bucket;

public class Common {

	private final String USER_AGENT = "Mozilla/5.0";

	public static Logger logger = Logger.getLogger(Common.class);

	public void sendGet(String urlString) throws Exception {

		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + urlString);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

	public void writeBinary(String urlString, String filePath) {

		URL url;
		try {
			url = new URL(urlString);
			URLConnection connection = url.openConnection();

			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[4096];
			int n = -1;

			OutputStream output = new FileOutputStream(new File(filePath));
			while ((n = input.read(buffer)) != -1) {
				if (n > 0) {
					output.write(buffer, 0, n);
				}
			}
			output.close();
		} catch (Exception e) {

		}

	}

	public static boolean compareLists(List<Bucket> aList, List<Bucket> bList) {

		int sizeOfTheShortestList = Math.min(aList.size(), bList.size());

		for (int i = 0; i < sizeOfTheShortestList; i++) {
			if (!((Bucket) bList.get(i)).getName().equals(
					((Bucket) aList.get(i)).getName())) {
				return false;
			}
		}

		return true;

	}
	
	public static boolean compareHeaders(Header[] aHeader, Header[] bHeader) {

		int sizeOfTheShortestList = Math.min(aHeader.length, bHeader.length);

		for (int i = 0; i < sizeOfTheShortestList; i++) {
			if (!((Bucket) bHeader.get(i)).getName().equals(
					((Bucket) aList.get(i)).getName())) {
				return false;
			}
		}

		return true;

	}
	
}
