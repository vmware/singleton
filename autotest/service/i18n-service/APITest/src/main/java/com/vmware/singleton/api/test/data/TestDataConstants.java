package com.vmware.singleton.api.test.data;

import java.nio.file.Paths;

public interface TestDataConstants {
	public static final String TEST_DATA_FOLDER = "testdata";
	public static final String LONG_STRING_FILE_PATH = Paths.get(TEST_DATA_FOLDER, "LongStringsForPOST.txt").toString();
	public static final String SUPPORTED_PRODUCTS_FILE_PATH = Paths.get(TEST_DATA_FOLDER, "SupportedProducts.json").toString();
	public static final String SUPPORTED_PRODUCTS_JSON_KEY_PRODUCTS = "products";
	public static final String SUPPORTED_PRODUCTS_JSON_KEY_PRODUCT_NAME = "name";
	public static final String SUPPORTED_PRODUCTS_JSON_KEY_PRODUCT_VERSION = "version";
}
