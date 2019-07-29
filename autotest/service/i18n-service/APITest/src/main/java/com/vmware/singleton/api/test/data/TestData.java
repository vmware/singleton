package com.vmware.singleton.api.test.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vmware.singleton.api.Product;
import com.vmware.singleton.api.test.common.Config;
import com.vmware.singleton.api.test.common.Constants;
import com.vmware.singleton.api.test.common.Util;

public class TestData {
	public static String getLongString() {
		String content=null;
		Reader reader;
		BufferedReader bufferedReader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(
					TestDataConstants.LONG_STRING_FILE_PATH), StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(reader);
			String line=null;
			content = "";
			while ((line = bufferedReader.readLine()) != null) {
				content = content + line;
			}
			bufferedReader.close();
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

//	public static boolean isProductSupported(String productName) {
//		for (String supportedProduct : getSupportedProductList()) {
//			if (supportedProduct.equalsIgnoreCase(productName)) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public static List<String> getSupportedProductList() {
//		List<String> supportedList = Config.getInstance().getList(Constants.CONF_KEY_SUPPORTED_PRODUCTS);
//		return supportedList;
//	}

	public static boolean isLocaleSupported(String locale) {
		for (String supportedLocale : getSupportedLocaleList()) {
			if (supportedLocale.equalsIgnoreCase(locale)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> getSupportedLocaleList() {
		List<String> supportedList = Config.getInstance().getList(Constants.CONF_KEY_TRANSLATION_LOCALES);
		return supportedList;
	}

	/*
	 * @return: products information stored in SupportedProducts.json in test data folder,
	 * return empty List if cannot get info from json test data file
	 */
	public static List<Product> getProducts() {
		List<Product> productList = new ArrayList<Product>();
		JSONObject productsInfo = Util.readJSONObjFromFile(TestDataConstants.SUPPORTED_PRODUCTS_FILE_PATH);
		if (productsInfo==null) return productList;

		JSONArray productJsonArray = productsInfo.getJSONArray(TestDataConstants.SUPPORTED_PRODUCTS_JSON_KEY_PRODUCTS);
		Iterator<Object> infoIterator = productJsonArray.iterator();
		while (infoIterator.hasNext()) {
			JSONObject productJsonObj = (JSONObject)infoIterator.next();
			String name = productJsonObj.getString(TestDataConstants.SUPPORTED_PRODUCTS_JSON_KEY_PRODUCT_NAME);
			String version = productJsonObj.getString(TestDataConstants.SUPPORTED_PRODUCTS_JSON_KEY_PRODUCT_VERSION);
			productList.add(new Product(name, version));
		}
		return productList;
	}
}
