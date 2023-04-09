package com.vmware.singleton.api.test.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

public class Util {
	public static JSONObject readJSONObjFromFile(String filePath) {
		JSONObject obj = null;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(filePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String content = "";
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				content = content + line;
			}
			bufferedReader.close();
			obj = new JSONObject(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}


	public static byte[] streamToByte(InputStream inputStream) {
		ByteArrayOutputStream stream = getByteArray(inputStream);
		if (stream==null) {
			return null;
		}
		else {
			return stream.toByteArray();
		}
	}

	public static String streamToString(InputStream inputStream) {
		ByteArrayOutputStream stream = getByteArray(inputStream);
		if (stream==null) {
			return null;
		}
		else {
			try {
				return stream.toString("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static String encodeURL(String str) throws UnsupportedEncodingException {
		String url = URLEncoder.encode(str, "UTF-8");
		return url.replace("+", "%20");
	}
	public static String encodeURL(List <String> strs) throws UnsupportedEncodingException {
		String temp="";
		int n = strs.size();
		for (int i=0; i<n; i++){
			temp = temp + strs.get(i);
			if (i!=n-1){
				temp = temp + ",";
			}
		}
		return encodeURL(temp);
	}
	private static ByteArrayOutputStream getByteArray(InputStream inputStream) {
		int len=0;
		ByteArrayOutputStream buffer=new ByteArrayOutputStream();
		byte[] buf=new byte[1024];
		try {
			while((len=inputStream.read(buf))!=-1)
				buffer.write(buf, 0, len);
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T getRandomItem(List<T> list) {
		Random random = new Random();
		T item = list.get(random.nextInt(list.size()));
		return item;
	}

	public static String trimStr(String targetStr, String strToTrim) {
		int trimLen = strToTrim.length();
		if (targetStr.startsWith(strToTrim)) {
			targetStr = targetStr.substring(trimLen);
		}
		if (targetStr.endsWith(strToTrim)) {
			targetStr = targetStr.substring(0, targetStr.length()-trimLen);
		}
		return targetStr;
	}

	public static String joinNonEmptyStrings(String delimiter, String... list) {
		String combined = "";
		for (int i=0; i < list.length; i++) {
			if (list[i] == null || list[i]=="") continue;

			combined = combined + (list[i].startsWith(delimiter)?"":delimiter) + list[i];
		}
		return trimStr(combined, delimiter);
	}

	public static HashMap<String, String> convertStringListToMap(String list,
			String listSeparator, String keyValueSeparator) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		String actuallistSeparator;
		String actualkeyValueSeparator;
		if (list.contains("!\"#$%&'()*+,-./01:;<=>?@AB xy[\\]^_`{|}~keyname")) {
			actuallistSeparator = ";;";
			actualkeyValueSeparator = "==";
		}else {
			actuallistSeparator = listSeparator;
			actualkeyValueSeparator = keyValueSeparator;
		}if (!list.isEmpty()) {
			for (String item : list.split(actuallistSeparator)) {
				String[] keyValuePair = new String[2];
				int index = item.lastIndexOf(actualkeyValueSeparator);
				keyValuePair[0] = item.substring(0, index);
				keyValuePair[1] = item.substring(index + actualkeyValueSeparator.length(), item.length());

				if (keyValuePair.length==2) {
					String key = keyValuePair[0].trim();
					String value = keyValuePair[1].trim();
					map.put(key, value);
				} else {
					throw new Exception(String.format("key value pair '%s' is not completed", item));
				}
			}
		}
		return map;
	}
}
