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
import java.util.HashMap;
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
			if (!bList.get(i).getName().equals(aList.get(i).getName())) {
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

	public static Map<String, Object> compareMaps(Map<String, Object> map1,
			Map<String, Object> map2) {

		Map<String, Object> resultMap1 = getMapInconsistencies(map1, map2);
		Map<String, Object> resultMap2 = getMapInconsistencies(map2, map1);

		Map<String, Object> mergedMap = mergeMaps(resultMap1, resultMap2);

		return mergedMap;
	}

	public static Map<String, Object> compareMaps(Map<String, Object> map1,
			Map<String, Object> map2, String[] avoidKeys) {

		Map<String, Object> resultMap1 = getMapInconsistencies(map1, map2,
				avoidKeys);
		Map<String, Object> resultMap2 = getMapInconsistencies(map2, map1,
				avoidKeys);

		Map<String, Object> mergedMap = mergeMaps(resultMap1, resultMap2);

		return mergedMap;
	}

	public static void printMap(Map<String, Object> map) {
		Iterator<Entry<String, Object>> mapIterator = map.entrySet().iterator();

		while (mapIterator.hasNext()) {
			Entry<String, Object> mapEntry1 = mapIterator.next();

			String key1 = mapEntry1.getKey();
			Object[] value1 = (Object[]) mapEntry1.getValue();

			System.out.println("\n key: '" + key1 + "' value: '"
					+ value1[0].toString() + "' : '" + value1[1].toString()
					+ "'");
		}
	}

	public static Map<String, Object> mergeMaps(Map<String, Object> map1,
			Map<String, Object> map2) {

		Iterator<Entry<String, Object>> mapIterator1 = map1.entrySet()
				.iterator();

		while (mapIterator1.hasNext()) {
			Entry<String, Object> mapEntry1 = mapIterator1.next();
			String key1 = mapEntry1.getKey();
			Object value1 = mapEntry1.getValue();
			map2.put(key1, value1);
		}

		return map2;

	}

	public static Map<String, Object> getMapInconsistencies(
			Map<String, Object> map1, Map<String, Object> map2) {

		Iterator<Entry<String, Object>> mapIterator1 = map1.entrySet()
				.iterator();

		Map<String, Object> resultMap = new HashMap<String, Object>();

		while (mapIterator1.hasNext()) {
			Entry<String, Object> mapEntry1 = mapIterator1.next();
			String key1 = mapEntry1.getKey();
			Object value1 = mapEntry1.getValue();
			Object value2 = getMapValue(map2, key1);

			if (!value1.equals(value2)) {
				Object[] values = { value1, value2 };

				resultMap.put(key1, values);
			}

		}

		return resultMap;

	}

	public static Map<String, Object> getMapInconsistencies(
			Map<String, Object> map1, Map<String, Object> map2,
			String[] avoidKeys) {

		Iterator<Entry<String, Object>> mapIterator1 = map1.entrySet()
				.iterator();

		Map<String, Object> resultMap = new HashMap<String, Object>();

		while (mapIterator1.hasNext()) {
			Entry<String, Object> mapEntry1 = mapIterator1.next();
			String key1 = mapEntry1.getKey();
			Object value1 = mapEntry1.getValue();
			Object value2 = getMapValue(map2, key1);

			if (isObjectInArray(avoidKeys, key1)) {
				continue;
			}

			if (!value1.equals(value2)) {
				Object[] values = { value1, value2 };

				resultMap.put(key1, values);
			}

		}

		return resultMap;

	}

	public static Object getMapValue(Map<String, Object> map, String key) {
		Iterator<Entry<String, Object>> mapIterator = map.entrySet().iterator();

		while (mapIterator.hasNext()) {
			Entry<String, Object> mapEntry = mapIterator.next();
			if (mapEntry.getKey().equals(key)) {
				return mapEntry.getValue();
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

	public static boolean isObjectInArray(Object[] objectArray, Object o) {
		for (int i = 0; i < objectArray.length; i++) {
			if (objectArray[i].equals(o)) {
				return true;
			}
		}
		return false;
	}

	public static void waitSec(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
