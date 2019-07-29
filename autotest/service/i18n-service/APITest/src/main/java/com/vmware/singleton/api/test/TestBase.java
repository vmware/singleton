package com.vmware.singleton.api.test;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.jayway.jsonpath.JsonPath;
//import com.vmware.g11n.log.GLogger;
//import com.vmware.g11n.log.TestCaseConfig;
//import com.vmware.g11n.log.TestSetConfig;
import com.vmware.singleton.api.Api;
import com.vmware.singleton.api.ApiParameter;
import com.vmware.singleton.api.rest.RestRequester;
import com.vmware.singleton.api.test.common.Config;
import com.vmware.singleton.api.test.common.Constants;
import com.vmware.singleton.api.test.common.Util;
import com.vmware.singleton.api.test.data.poi.ExcelUtil;

public class TestBase extends RestRequester {
	private static HashMap<String, HashMap<String, Api>> apiListVerMap = new HashMap<String, HashMap<String, Api>>();
	private static Config cfg = Config.getInstance();
//	public static GLogger logger = GLogger.getInstance(TestBase.class.getSimpleName());

	@BeforeSuite(alwaysRun=true)
	public void suiteSetup() {
//		String bu = cfg.get(Constants.CONF_KEY_BU);
//		String buildid = cfg.get(Constants.CONF_KEY_BUILD_ID);
//		String product = cfg.get(Constants.CONF_KEY_PRODUCT);
//		String branch = cfg.get(Constants.CONF_KEY_BRANCH);
//		String buildType = cfg.get(Constants.CONF_KEY_BUILD_TYPE);
//		String user = cfg.get(Constants.CONF_KEY_RACETRACK_USER);
//		String customTestResultFolder = cfg.get(Constants.CONF_KEY_TEST_RESULT_FOLDER);
//		String logOnRacetrack = cfg.get(Constants.CONF_KEY_RACETRACK_ENABLE);
//		logger.setConfig(bu, product, branch, buildid, buildType,
//				branch, "Regression", "en_US", "win2k8r2", "x64", "none", "none",
//				"none", "none", user, logOnRacetrack, null, customTestResultFolder);
		loadAPIs();
	}

	@BeforeTest(alwaysRun=true)
	public void testSetup() throws Exception {
		String spliteReg = String.format("\\s*%s\\s*", Constants.CONF_LIST_SEPERATER);
		String[] testSheetList = cfg.get(Constants.CONF_KEY_TEST_DATA_SHEETS).trim().split(spliteReg);
		ExcelUtil.setExcelFile(
				Paths.get(getResourceFolder(), cfg.get(Constants.CONF_KEY_TEST_DATA_FILE)).toString(),
				testSheetList);
//		TestSetConfig testSetConfig = new TestSetConfig("vIP Server API Testing");
//		logger.testSetBegin(testSetConfig);
	}

	@Test(dataProvider="API_Test")
	public void test(String apiVerName, String apiName, HashMap<String, String> params,
			String requestBody, HashMap<String, String> requestProperties, HashMap<String, String> checkPointMap) throws Exception {
//		logger.testCaseBegin(testCase);

		if (params.isEmpty() || checkPointMap.isEmpty()) {
			Assert.assertNotNull(null, "parameters or checkpoint is empty,"
					+ "please check your test data.");
//			logger.verifyNotNull("parameters or checkpoint is empty,"
//					+ "please check your test data.", null);
		} else {
			System.out.println(String.format("Request parameters: '%s'", params.toString()));
			System.out.println(String.format("Checkpoint: '%s'", checkPointMap.toString()));
			String response = request(apiVerName, apiName, params, requestBody, requestProperties);
			if (response==null || response.isEmpty()) {
				Assert.assertNotNull(response, "response is not null");
//				logger.verifyNotNull("response is not null", response);
			} else {
				try {
					verify(new JSONObject(response), checkPointMap);
				} catch (JSONException e) {
					Assert.assertEquals(response, checkPointMap, "response is not json");
//					logger.verifyEqual("Verify response json", response, checkPointMap, "response is not json");
				}
			}
		}

//		logger.testCaseEnd();
	}

//	/**
//	 * Method to clean up
//	 */
//	@AfterTest(alwaysRun=true)
//	public static void testCleanUp() {
//		try {
//			logger.testSetEnd();
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}


	public static void loadAPIs() {
		String spliteReg = String.format("\\s*%s\\s*", Constants.CONF_LIST_SEPERATER);
		String[] fileList = cfg.get(Constants.CONF_KEY_API_INFO_FILES).trim().split(spliteReg);
		if (fileList==null || fileList.length==0) {
			Assert.assertTrue(false, String.format("API loading files are empty, please specific '%s' in %s",
					Constants.CONF_KEY_API_INFO_FILES, Constants.CONFIG_FILE_NAME));
//			logger.error(String.format("API loading files are empty, please specific '%s' in %s",
//					Constants.CONF_KEY_API_INFO_FILES, Constants.CONFIG_FILE_NAME));
		}

		for (String file : fileList) {
			String filePath = Paths.get(getResourceFolder(), file.trim()).toString();
			if (!filePath.endsWith(".json")) {
				filePath = filePath + ".json";
			}
			String apiVerName = file.split("\\.")[0];
			System.out.println(String.format("loading APIs from '%s'", filePath));
			JSONObject apiInfo = Util.readJSONObjFromFile(filePath);
			if (apiInfo==null) {
				Assert.assertTrue(false, "Loading APIs information failed!");
//				logger.error("Loading APIs information failed!");
			}
			HashMap<String, Api> apiMap = new HashMap<String, Api>();
			///TODO: put these key to constants
			JSONArray groups = apiInfo.getJSONArray("groups");
			Iterator<Object> groupIterator = groups.iterator();
			while (groupIterator.hasNext()) {
				JSONObject group = (JSONObject) groupIterator.next();
				String groupName = group.getString("name");
				JSONArray apis = group.getJSONArray("apis");
				Iterator<Object> apiIterator = apis.iterator();
				while (apiIterator.hasNext()) {
					JSONObject api = (JSONObject) apiIterator.next();
					String apiName = api.getString("name");
					String apiPath = api.getString("path");
					String apiMethod = api.getString("method");
					String apiDesc = api.getString("description");
					JSONArray params = api.getJSONArray("params");
					Iterator<Object> paramIterator = params.iterator();
					List<ApiParameter> apiParameters = new ArrayList<ApiParameter>();
					while (paramIterator.hasNext()) {
						JSONObject param = (JSONObject) paramIterator.next();
						String paramName = param.getString("name");
						Boolean required = param.getBoolean("required");
						String desc = param.getString("desc");
						String paramType = param.getString("paramType");
						String dataType = param.getString("dataType");
						ApiParameter parameterObj = new ApiParameter(paramName,
								required, desc, paramType, dataType);
						apiParameters.add(parameterObj);
					}
					Api apiObj = new Api(groupName, apiName, apiPath, apiDesc, apiMethod, apiParameters);
					apiMap.put(apiName, apiObj);
				}
			}
			apiListVerMap.put(apiVerName, apiMap);
		}
	}

	public static String request(String apiVerName, String apiName,
			HashMap<String, String> params, String requestBody, HashMap<String, String> requestProperties) throws Exception {
		HashMap<String, Api> apiMap = apiListVerMap.get(apiVerName);
		String responseStr = "";
		Api apiObj = apiMap.get(apiName);
		if (apiObj==null) {
			throw new Exception(String.format("There is no API named '%s'", apiName));
		}
		String queryUrl = apiObj.getPath();
		boolean hasQueryMark = false;
		for (String paramName : params.keySet()) {
			String paramValue = params.get(paramName);
			HashMap<String, ApiParameter> paramMap = apiObj.getParamMap();
			ApiParameter targetParam = paramMap.get(paramName);
			String encodedParamName;
			String encodedParamValue;

			if (targetParam==null) {
				Assert.assertTrue(false, String.format("Parameter '%s' does not exist in API json description file, "
						+ "will not send request to server, please make sure test data correct", paramName));
//				logger.error(String.format("Parameter '%s' does not exist in API json description file, "
//						+ "will not send request to server, please make sure test data correct", paramName));
				return responseStr;
			}
			switch (targetParam.getType().toLowerCase()) {
			case Constants.URL_PARAM_TYPE_PATH:
				try {
					encodedParamName = Util.encodeURL(paramName);
					encodedParamValue = Util.encodeURL(paramValue);
				} catch (UnsupportedEncodingException e) {
					System.out.println("encoding failed: " + e.getMessage());
					encodedParamName = paramName;
					encodedParamValue = paramValue;
				}
				queryUrl = queryUrl.replace("{"+paramName+"}", encodedParamValue);
				break;
			case Constants.URL_PARAM_TYPE_QUERY:
				try {
					encodedParamName = Util.encodeURL(paramName);
					encodedParamValue = Util.encodeURL(paramValue);
				} catch (UnsupportedEncodingException e) {
					System.out.println("encoding failed: " + e.getMessage());
					encodedParamName = paramName;
					encodedParamValue = paramValue;
				}
				if (hasQueryMark) {
					queryUrl = queryUrl + "&" + encodedParamName + "=" + encodedParamValue;
				} else {
					queryUrl = queryUrl + "?";
					hasQueryMark = true;
					queryUrl = queryUrl + encodedParamName + "=" + encodedParamValue;
				}
				break;
			default:
				Assert.assertTrue(false, String.format("parameter type '%s' is not supported, "
						+ "please check API json description file", targetParam.getType()));
//				logger.error(String.format("parameter type '%s' is not supported, "
//						+ "please check API json description file", targetParam.getType()));
				break;
			}
		}
		String serverURL = Util.trimStr(cfg.get(Constants.TEST_SERVER), "/");
		String requestURL = serverURL+queryUrl;
		String requestMethod = apiObj.getMethod();
		System.out.println(String.format("Request URL: '%s'", requestURL));
		System.out.println(String.format("Request body: '%s'", requestBody));
		responseStr = getResponse(requestURL, requestMethod, requestBody, requestProperties);
		System.out.println(String.format("Response Json: '%s'", responseStr));
		return responseStr;
	}

	public HashMap<String, Api> getAPIMap(String apiVerName) {
		return apiListVerMap.get(apiVerName);
	}

	public Api getAPIObj(String apiVerName, String apiName) {
		HashMap<String, Api> apiMap = apiListVerMap.get(apiVerName);
		return apiMap.get(apiName);
	}

	public void verify(JSONObject jsonObj, HashMap<String, String> jsonPointerExpectMap) {
		for (String jsonPointer : jsonPointerExpectMap.keySet() ) {
			String expected = jsonPointerExpectMap.get(jsonPointer);
			try {
				String jsonPath = covertToJsonPath(jsonPointer);
				Object queryObj = JsonPath.<Object>read(jsonObj.toString(), jsonPath);

				if (queryObj==null) {
					queryObj="null";
				}
				//deal with list
				if (expected.startsWith("[") && expected.endsWith("]")) {
					String expectedWithoutSquareBrackets = expected.substring(1, expected.length()-1);
					if (expectedWithoutSquareBrackets.isEmpty()) {
						//handle expected is empty list, like []
						System.out.println("expected is a empty list []");
//						logger.verifyEqual("verify json path '"+jsonPointer+"'", queryObj.toString(), "[]");
						Assert.assertEquals(queryObj.toString(), "[]");
					} else {
						String expectedWithoutQuota = expectedWithoutSquareBrackets.substring(1,
								expectedWithoutSquareBrackets.length()-1);
						String[] expectedList = expectedWithoutQuota.split("\",\"");
						if (expectedList.length == 1) {
							//try to split using ", " instead of ","
							String[] expectedList2 = expectedWithoutQuota.split("\", \"");
							if (expectedList2.length > 1) {
								//split using ", "
								expectedList = expectedList2;
							}
						}
						List<String> list = Arrays.asList(expectedList);
						Iterator<String> expectedIterator = list.iterator();

						String actualResponse = queryObj.toString();
						if (!(actualResponse.startsWith("[") && actualResponse.endsWith("]"))) {
//							logger.verifyTrue("verify json path '"+jsonPointer+"'", false, "expect list but actual is not");
							Assert.assertTrue(false, "verify json path '"+jsonPointer+"'");
							return;
						}
						String actualResponseWithoutSquareBrackets = actualResponse.substring(1, actualResponse.length()-1);
						String actualResponseWithoutQuota = actualResponseWithoutSquareBrackets.substring(1,
								actualResponseWithoutSquareBrackets.length()-1);
						String[] actualResponseList = actualResponseWithoutQuota.split("\",\"");
						if (actualResponseList.length == 1) {
							//try to split using ", " instead of ","
							String[] actualResponseList2 = actualResponseWithoutQuota.split("\", \"");
							if (actualResponseList2.length > 1) {
								//split using ", "
								actualResponseList = actualResponseList2;
							}
						}
						List<String> actualList = new ArrayList<String>(Arrays.asList(actualResponseList));

						List<String> missingList = new ArrayList<String>();
						while (expectedIterator.hasNext()) {
							String expectedValue = expectedIterator.next();
							boolean found = false;
							for (int j=0; j < actualList.size(); j++) {
								if (actualList.get(j).equals(expectedValue)) {
									actualList.remove(j);
									found = true;
									break;
								}
							}
							if (!found) {
								missingList.add(expectedValue);
							}
						}
						String errorMsg = "";
						if (!missingList.isEmpty()) {
							errorMsg = String.format("missing expected list '%s'", missingList);
						}
						if (!actualList.isEmpty()) {
							errorMsg = errorMsg + ", " + String.format("unexpected list in response '%s'", actualList);
						}

						boolean result = false;
						if (errorMsg.isEmpty()) {
							result = true;
						}
						Assert.assertTrue(result, "verify response list no missing and no unexpected value");
//						logger.verifyTrue("verify response list no missing and no unexpected value", result, errorMsg);
					}
				}
				else {
					//if there is array filter like '[?(@.languageTag=='en')]/displayName',
					//the json path query result will return a list,
					//but actually, the expected it not list, so we need to remove [].
					String queryStr = queryObj.toString();
					if (jsonPath.contains("].")) {
						//							int indexLastDot = jsonPath.lastIndexOf(".");
						//							if (jsonPath.substring(indexLastDot-1, indexLastDot).equals("]")) {
						System.out.println("[jsonpath spec]found array filter, need remove '[' and ']' in result");
						queryStr = queryStr.substring(1, queryStr.length()-1);
						if (queryStr.startsWith("\"") && queryStr.endsWith("\"")) {
							System.out.println("[jsonpath spec]remove '\"' at the start and end");
							queryStr = queryStr.substring(1, queryStr.length()-1);
						}
						//							}
					}
					Assert.assertEquals(queryStr, expected);
//					logger.verifyEqual("verify json path '"+jsonPointer+"'", queryStr, expected);
				}
			} catch (Exception e) {
				Assert.assertTrue(false, String.format("query json path is not existed, json path is '%s'", jsonPointer));
//				logger.error(String.format("query json path is not existed, json path is '%s'", jsonPointer));
			}
		}
	}

	private String covertToJsonPath(String jsonPointer) {
		String jsonPointerSeparator = "/";
		String jsonPathSeparator = ".";
		if (jsonPointer.startsWith(jsonPointerSeparator)) {
			jsonPointer = jsonPointer.substring(1);
		}
		String[] pathList = jsonPointer.split(jsonPointerSeparator);
		String jsonPath = "$"+jsonPathSeparator;
		for (String pathPart : pathList) {
			if (pathPart.contains(jsonPathSeparator)) {
				pathPart = String.format("['%s']", pathPart);
			}
			jsonPath = jsonPath + pathPart + jsonPathSeparator;
		}
		jsonPath = jsonPath.substring(0, jsonPath.length()-1);//remove last character '/'
		return jsonPath;
	}

	@DataProvider(name="API_Test")
	public Object[][] getTestCase() throws Exception {
		Collection<Object[]> testMatrix = new ArrayList<Object[]>();

		int colId = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_ID));
		int colAPIName = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_NAME));
		int colAuto = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_AUTO));
		int colDesc = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_DESC));
		int colRequestParam = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_PARAM));
		int colRequestBody = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_BODY));
		int colRequestHeader = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_HEADER));
		int colExpect = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_EXPECTED));
		int colPriority = Integer.parseInt(cfg.get(Constants.CONF_KEY_TEST_DATA_COLUMN_PRIORITY));
		String testCasePriority = cfg.get(Constants.CONF_KEY_TEST_DATA_FILTER_PRIORITY).toLowerCase();

		for (XSSFSheet sheet : ExcelUtil.getSheetList()) {
			int lastRowNum = sheet.getLastRowNum();
			String apiVerName = sheet.getSheetName();
			for(int currRow = 1; currRow <= lastRowNum; currRow++) {
				String currPriority = ExcelUtil.getCellData(sheet, currRow, colPriority);
				String auto = ExcelUtil.getCellData(sheet, currRow, colAuto);
				if (ExcelUtil.getCellData(sheet, currRow, colId).isEmpty() ||
					!auto.equalsIgnoreCase("automated") ||
					(!currPriority.isEmpty() && !testCasePriority.contains(currPriority.toLowerCase()))
					) {
					continue;//skip this row
				}

				Object[] data = new Object[6];
				String apiName = ExcelUtil.getCellData(sheet, currRow, colAPIName);
				String id = ExcelUtil.getCellData(sheet, currRow, colId).split("\\.")[0];
				String description = ExcelUtil.getCellData(sheet, currRow, colDesc);
				String requestParamList = ExcelUtil.getCellData(sheet, currRow, colRequestParam);
				String requestBody = ExcelUtil.getCellData(sheet, currRow, colRequestBody);
				String requestHeaderList = ExcelUtil.getCellData(sheet, currRow, colRequestHeader);
				String expectList = ExcelUtil.getCellData(sheet, currRow, colExpect);
				if (apiName.isEmpty()) {
					apiName = "miss";
				}
//				TestCaseConfig testCaseConfig = new TestCaseConfig(""+id, apiName, "level2", "API", "p1");
//				if (!description.equals("")) {
//					testCaseConfig.setDescription(description);
//				}

				HashMap<String, String> requestParam;
				try {
					requestParam = Util.convertStringListToMap(requestParamList, ";", "=");
				} catch (Exception e) {
					throw new Exception(String.format("column '%s' request parameter key value pair is not correct, details:%s",
							colRequestParam, e.getMessage()));
				}

				HashMap<String, String> requestProperties;
				try {
					requestProperties = Util.convertStringListToMap(requestHeaderList, ";", "=");
				} catch (Exception e) {
					throw new Exception(String.format("column '%s' request header key value pair is not correct, details:%s",
							colRequestHeader, e.getMessage()));
				}

				HashMap<String, String> checkPointMap;
				try {
					checkPointMap = Util.convertStringListToMap(expectList, ";", "=");
				} catch (Exception e) {
					throw new Exception(String.format("column '%s' checkpoint key value pair is not correct, details:%s",
							colExpect, e.getMessage()));
				}
				data[0] = apiVerName;
				data[1] = apiName;
//				data[2] = testCaseConfig;
				data[2] = requestParam;
				data[3] = requestBody;
				data[4] = requestProperties;
				data[5] = checkPointMap;
				testMatrix.add(data);
			}
		}
		return testMatrix.toArray(new Object[0][]);
	}

	private static String getResourceFolder() {
		Path path = Paths.get(Constants.RESOURCE_FOLDER,
				cfg.get(Constants.CONF_KEY_BRANCH));
		if (!Files.exists(path)) {
			path = Paths.get(Constants.RESOURCE_FOLDER,
					cfg.get(Constants.CONF_KEY_TEST_DATA_DEFAULT_BRANCH));
		}
		return path.toString();
	}
}
