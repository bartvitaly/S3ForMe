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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.amazonaws.services.s3.model.Bucket;

public class Common {

	private final static String USER_AGENT = "Mozilla/5.0";

	public static Logger logger = Logger.getLogger(Common.class);

	public static String sendGet(String urlString) {

		String result = "";

		StringBuffer response;
		try {
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
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			result = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

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

	public static boolean compareBuckets(List<Bucket> aList, List<Bucket> bList) {

		int sizeOfTheShortestList = Math.min(aList.size(), bList.size());

		for (int i = 0; i < sizeOfTheShortestList; i++) {
			if (!((Bucket) bList.get(i)).getName().equals(
					((Bucket) aList.get(i)).getName())) {
				return false;
			}
		}

		return true;

	}

	public static boolean compareLists(List<?> list, List<?> list_2) {
		if (list == null && list_2 == null) {
			return true;
		}

		if (list == null && list_2 != null) {
			return false;
		}

		if (list != null && list_2 == null) {
			return false;
		}

		if (list.size() != list_2.size()) {
			return false;
		}

		if (!list.retainAll(list_2) && list_2.retainAll(list)) {
			return false;
		}

		if (list.retainAll(list_2) && !list_2.retainAll(list)) {
			return true;
		}

		return true;
	}

	//
	// public static boolean compareMaps(Map<String,Object> m1,
	// Map<String,Object> m2) {
	// if (m1.size() != m2.size())
	// return false;
	//
	// Iterator<Entry<String, Object>> holyDayiterator =
	// m1.entrySet().iterator();
	//
	// while (holyDayiterator.hasNext()) {
	// Entry<String, Object> holiDayEntry = holyDayiterator.next();
	// if(!m1.containsValue((holiDayEntry.getValue()))){
	// System.out.println("works perfect");
	// }
	//
	// foreach (K key: m1.keySet())
	// if (!m1.get(key).equals(m2.get(key)))
	// return false;
	// return true;
	// }
	// }

	public static Object getMapValue(Map<String, Object> m1, String key) {
		Iterator<Entry<String, Object>> holyDayiterator = m1.entrySet()
				.iterator();

		while (holyDayiterator.hasNext()) {
			Entry<String, Object> holiDayEntry = holyDayiterator.next();
			if (holiDayEntry.getKey().equals(key)) {
				return holiDayEntry.getValue();
			}

		}

		return "";
	}

	public static boolean compareHeaders(Header[] aHeader, Header[] bHeader) {

		int sizeOfTheShortestList = Math.min(aHeader.length, bHeader.length);

		for (int i = 0; i < sizeOfTheShortestList; i++) {
			// if (!((Bucket) bHeader[i]).getName().equals(
			// ((Bucket) aHeader[i].getName())) {
			// return false;
			// }
		}

		return true;

	}

	public static void waitSec(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
