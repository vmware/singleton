/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.vmware.i18n.common.DayEnum;
import com.vmware.i18n.common.OfficialStatusEnum;
import com.vmware.i18n.utils.timezone.CldrTimeZoneUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.common.Constants;

public class CLDRUtils {

	private static Logger logger = LoggerFactory.getLogger(CLDRUtils.class);

    public static final String RESOURCE_PATH = "src" + File.separator + "main" + File.separator + "resources";
    public static final String CONFIG_PATH = RESOURCE_PATH + File.separator + "config.properties";
    public static final Properties PROP = PropertiesFileUtil.loadFromFile(CONFIG_PATH);
    public static final String CLDR_VERSION = PROP.getProperty("cldr.version");
    public static final String CLDR_URLS = PROP.getProperty("cldr.urls");
    public static final String CLDR_ADDITIONAL_CONFIG = PROP.getProperty("cldr.additional.config");
    public static final String FILE_NAME = CLDR_VERSION + ".zip";
    public static final String CLDR_PATH = RESOURCE_PATH + File.separator + "cldr" + File.separator;
    public static final String CLDR_DOWNLOAD_DIR = CLDR_PATH + "data" + File.separator + CLDR_VERSION + File.separator;
    public static final String GEN_CLDR_CORE_DIR = CLDR_PATH + "core" + File.separator;
    public static final String GEN_CLDR_PATTERN_BASE_PATH = CLDR_PATH + "pattern" + File.separator;
    public static final String GEN_CLDR_PATTERN_DIR = GEN_CLDR_PATTERN_BASE_PATH + "common" + File.separator;
    public static final String GEN_CLDR_LOCALEDATA_DIR = CLDR_PATH + "localedata" + File.separator;
    public static final String GEN_CLDR_PATTERN_TIMEZONE_DIR = GEN_CLDR_PATTERN_BASE_PATH + "timezone" + File.separator;

    /**
     * Download CLDR data
     *
     * @return a map collection, store the zip file name and save path
     */
	public static void download() {
		int byteread = 0;
		InputStream is = null;
		FileOutputStream fs = null;
		String fileName = null;
		try {
			logger.info("Start to download the CLDR data ... ");
			for (String url : CLDRUtils.CLDR_URLS.split(",")) {
				url = url + FILE_NAME;
				logger.info("CLDR Download URL is:" + url);
				System.out.println("CLDR Download URL is:" + url);
				URL urlObj = new URL(url);
				URLConnection conn = urlObj.openConnection();
				conn.setConnectTimeout(30 * 1000);
				fileName = conn.getHeaderField("Content-Disposition").split("=")[1];
				is = conn.getInputStream();
				System.out.println(fileName);
				File savePath = new File(CLDR_DOWNLOAD_DIR + fileName);
				if (!savePath.getParentFile().exists()) {
					savePath.getParentFile().mkdirs();
				}
				if (!savePath.exists()) {
					if (!savePath.createNewFile()) {
						logger.error("create file failure when CLDR download!");
					}
				}
				try {
					fs = new FileOutputStream(CLDR_DOWNLOAD_DIR + fileName);
					byte[] buffer = new byte[2048];
					while ((byteread = is.read(buffer)) != -1) {
						fs.write(buffer, 0, byteread);
					}
					fs.flush();
				}finally {
					if(fs != null) {
						fs.close();
					}
					IOUtil.closeInputStream(is);
				}
			}
		} catch (Exception e) {
			logger.error("CLDR Download error:" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(fs != null) {
					fs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtil.closeInputStream(is);
			logger.info("CLDR Download complete!");
		}
	}

	/**
	 * Read the specific JSON file in the ZIP file
	 *
	 * @param fileName the file name in the ZIP file. e.g.
	 *                 cldr-numbers-full-32.0.0/main/en/numbers.json
	 * @param zipPath  the ZIP file path
	 * @return JSON context
	 */
	@SuppressWarnings("unchecked")
	public static String readZip(String fileName, String zipPath) {
		StringBuffer sb = new StringBuffer();
		ZipFile zf = null;
		try {
			zf = new ZipFile(zipPath);
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zf.entries();
			ZipEntry ze = null;
			Scanner scanner = null;
			while (entries.hasMoreElements()) {
				ze = entries.nextElement();
				if (ze.isDirectory()) {
					continue;
				}
				if (ze.getName().equals(fileName)) {
					scanner = new Scanner(zf.getInputStream(ze), Constants.UTF8);
					while (scanner.hasNextLine()) {
						sb.append(scanner.nextLine().trim());
					}
					scanner.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeZipFile(zf);
		}
		return sb.toString();
	}

	public static Map<String, Object> dateDataExtract(String locale) {
		String zipPath = CLDRConstants.DATE_ZIP_FILE_PATH;
		String fileName = MessageFormat.format(CLDRConstants.CLDR_DATES_FULL_CA_GREGORIAN, CLDR_VERSION, locale);
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject dateContents = JSONUtil.string2JSON(json);

		// dayPeriodsFormat
		Map<String, Object> dayPeriodsFormatMap = dayPeriodsFormatExtract(locale, dateContents);

		// dayPeriodsStandalone
		Map<String, Object> dayPeriodsStandaloneMap = dayPeriodsStandaloneExtract(locale, dateContents);

		// daysFormat
		Map<String, Object> daysFormatMap = daysFormatExtract(locale, dateContents);

		// daysStandalone
		Map<String, Object> daysStandaloneMap = daysStandaloneExtract(locale, dateContents);

		// monthsFormat
		Map<String, Object> monthsFormatMap = monthsFormatExtract(locale, dateContents);

		// monthsStandalone
		Map<String, Object> monthsStandaloneMap = monthsStandaloneExtract(locale, dateContents);

		// eras
		Map<String, Object> erasMap = erasExtract(locale, dateContents);

		Map<String, Object> supplementalWeekData = getSupplementalWeekData();
		// first day of week
		@SuppressWarnings("unchecked")
		Integer firstDayOfWeek = extractFirstDay(locale, (Map<String, String>) supplementalWeekData.get("firstDay"));
		// weekend range
		List<Integer> weekendRange = extractWeekendRange(locale, supplementalWeekData);

		// dateFormats
		Map<String, Object> dateFormatMap = dateFormatExtract(locale, dateContents);

		// dateFormats
		Map<String, Object> timeFormatMap = timeFormatExtract(locale, dateContents);

		// datetimeFormats
		Map<String, Object> dateTimeMap = dateTimeFormatExtract(locale, dateContents);

		Map<String, Object> dateMap = new LinkedHashMap<String, Object>();
		dateMap.put(Constants.DAY_PERIODS_FORMAT, dayPeriodsFormatMap);
		dateMap.put(Constants.DAY_PERIODS_STANDALONE, dayPeriodsStandaloneMap);
		dateMap.put(Constants.DAYS_FORMAT, daysFormatMap);
		dateMap.put(Constants.DAYS_STANDALONE, daysStandaloneMap);
		dateMap.put(Constants.MONTH_FORMAT, monthsFormatMap);
		dateMap.put(Constants.MONTHS_STANDALONE, monthsStandaloneMap);
		dateMap.put(Constants.ERAS, erasMap);
		dateMap.put(Constants.FIRST_DAY_OF_WEEK, firstDayOfWeek);
		dateMap.put(Constants.WEEKEND_RANGE, weekendRange);
		dateMap.put(Constants.DATE_FORMATS, dateFormatMap);
		dateMap.put(Constants.TIME_FORMATS, timeFormatMap);
		dateMap.put(Constants.DATE_TIME_FORMATS, dateTimeMap);
		return dateMap;
	}

	private static Map<String, Object> dateFieldsFormatExtract(String locale) {
		String fileName = MessageFormat.format(CLDRConstants.CLDR_DATES_FULL_DATE_FIELDS, CLDR_VERSION, locale);
		String json = CLDRUtils.readZip(fileName, CLDRConstants.DATE_ZIP_FILE_PATH);
		String fieldsFormat = JSONUtil
				.select(JSONUtil.string2JSON(json), MessageFormat.format(CLDRConstants.DATE_FIELDS_KEY_PATH, locale))
				.toString();
		Map<String, Object> fieldsFormatMap = JSONUtil.getMapFromJson(fieldsFormat);
		Map<String, Object> dateFieldsFormatMap = new LinkedHashMap<String, Object>();
		dateFieldsFormatMap.put(Constants.YEAR, fieldsFormatMap.get(Constants.YEAR));
		dateFieldsFormatMap.put(Constants.MONTH, fieldsFormatMap.get(Constants.MONTH));
		dateFieldsFormatMap.put(Constants.DAY, fieldsFormatMap.get(Constants.DAY));
		dateFieldsFormatMap.put(Constants.HOUR, fieldsFormatMap.get(Constants.HOUR));
		dateFieldsFormatMap.put(Constants.MINUTE, fieldsFormatMap.get(Constants.MINUTE));
		dateFieldsFormatMap.put(Constants.SECOND, fieldsFormatMap.get(Constants.SECOND));
		return dateFieldsFormatMap;
	}

	private static Map<String, Object> dayPeriodsFormatExtract(String locale, JSONObject content) {
		// dayPeriodsFormat narrow
		String dayPeriodsFormatNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.format.narrow").toString();
		Map<String, Object> dayPeriodsFormatNarrowMap = JSONUtil.getMapFromJson(dayPeriodsFormatNarrow);
		List<Object> dayPeriodsFormatNarrowArr = new ArrayList<Object>();
		//am&pm are always defined
		dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.AM).toString());
		dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.PM).toString());
		if (dayPeriodsFormatNarrowMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.NOON) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.MORNING1) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.MORNING2) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.EVENING1) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.EVENING2) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.NIGHT1) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsFormatNarrowMap.get(Constants.NIGHT2) != null) {
			dayPeriodsFormatNarrowArr.add(dayPeriodsFormatNarrowMap.get(Constants.NIGHT2).toString());
		}
		// dayPeriodsFormat abbreviated
		String dayPeriodsFormatAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.format.abbreviated")
				.toString();
		Map<String, Object> dayPeriodsFormatAbbrMap = JSONUtil.getMapFromJson(dayPeriodsFormatAbbreviated);
		List<Object> dayPeriodsFormatAbbrArr = new ArrayList<Object>();
		dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.AM).toString());
		dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.PM).toString());
		if (dayPeriodsFormatAbbrMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.NOON) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.MORNING1) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.MORNING2) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.EVENING1) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.EVENING2) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.NIGHT1) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsFormatAbbrMap.get(Constants.NIGHT2) != null) {
			dayPeriodsFormatAbbrArr.add(dayPeriodsFormatAbbrMap.get(Constants.NIGHT2).toString());
		}

		// dayPeriodsFormat wide
		String dayPeriodsFormatWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.format.wide").toString();
		Map<String, Object> dayPeriodsFormatWideMap = JSONUtil.getMapFromJson(dayPeriodsFormatWide);
		List<Object> dayPeriodsFormatWideArr = new ArrayList<Object>();
		dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.AM).toString());
		dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.PM).toString());
		if (dayPeriodsFormatWideMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.NOON) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.MORNING1) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.MORNING2) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.EVENING1) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.EVENING2) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.NIGHT1) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsFormatWideMap.get(Constants.NIGHT2) != null) {
			dayPeriodsFormatWideArr.add(dayPeriodsFormatWideMap.get(Constants.NIGHT2).toString());
		}

		Map<String, Object> dayPeriodsFormatMap = new LinkedHashMap<String, Object>();
		dayPeriodsFormatMap.put("narrow", dayPeriodsFormatNarrowArr);
		dayPeriodsFormatMap.put("abbreviated", dayPeriodsFormatAbbrArr);
		dayPeriodsFormatMap.put("wide", dayPeriodsFormatWideArr);
		return dayPeriodsFormatMap;
	}

	private static Map<String, Object> dayPeriodsStandaloneExtract(String locale, JSONObject content) {
		// dayPeriodsStandalone narrow
		String dayPeriodsStandaloneNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.stand-alone.narrow")
				.toString();
		Map<String, Object> dayPeriodsStandaloneNarrowMap = JSONUtil.getMapFromJson(dayPeriodsStandaloneNarrow);
		List<Object> dayPeriodsStandaloneNarrowArr = new ArrayList<Object>();
		dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.AM).toString());
		dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.PM).toString());
		if (dayPeriodsStandaloneNarrowMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.NOON) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.MORNING1) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.MORNING2) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.EVENING1) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.EVENING2) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.NIGHT1) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsStandaloneNarrowMap.get(Constants.NIGHT2) != null) {
			dayPeriodsStandaloneNarrowArr.add(dayPeriodsStandaloneNarrowMap.get(Constants.NIGHT2).toString());
		}

		// dayPeriodsStandalone abbreviated
		String dayPeriodsStandaloneAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.stand-alone.abbreviated")
				.toString();
		Map<String, Object> dayPeriodsStandaloneAbbrMap = JSONUtil.getMapFromJson(dayPeriodsStandaloneAbbreviated);
		List<Object> dayPeriodsStandaloneAbbrArr = new ArrayList<Object>();
		dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.AM).toString());
		dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.PM).toString());
		if (dayPeriodsStandaloneAbbrMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.NOON) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.MORNING1) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.MORNING2) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.EVENING1) != null){
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.EVENING2) != null){
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.NIGHT1) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsStandaloneAbbrMap.get(Constants.NIGHT2) != null) {
			dayPeriodsStandaloneAbbrArr.add(dayPeriodsStandaloneAbbrMap.get(Constants.NIGHT2).toString());
		}

		// dayPeriodsStandalone wide
		String dayPeriodsStandaloneWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dayPeriods.stand-alone.wide")
				.toString();
		Map<String, Object> dayPeriodsStandaloneWideMap = JSONUtil.getMapFromJson(dayPeriodsStandaloneWide);
		List<Object> dayPeriodsStandaloneWideArr = new ArrayList<Object>();
		dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.AM).toString());
		dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.PM).toString());
		if (dayPeriodsStandaloneWideMap.get(Constants.AMALTVARIANT) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.AMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.PMALTVARIANT) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.PMALTVARIANT).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.MIDNIGHT) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.MIDNIGHT).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.NOON) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.NOON).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.MORNING1) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.MORNING1).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.MORNING2) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.MORNING2).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.AFTERNOON1) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.AFTERNOON1).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.AFTERNOON2) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.AFTERNOON2).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.EVENING1) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.EVENING1).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.EVENING2) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.EVENING2).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.NIGHT1) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.NIGHT1).toString());
		}
		if (dayPeriodsStandaloneWideMap.get(Constants.NIGHT2) != null) {
			dayPeriodsStandaloneWideArr.add(dayPeriodsStandaloneWideMap.get(Constants.NIGHT2).toString());
		}

		Map<String, Object> dayPeriodsStandaloneMap = new LinkedHashMap<String, Object>();
		dayPeriodsStandaloneMap.put("narrow", dayPeriodsStandaloneNarrowArr);
		dayPeriodsStandaloneMap.put("abbreviated", dayPeriodsStandaloneAbbrArr);
		dayPeriodsStandaloneMap.put("wide", dayPeriodsStandaloneWideArr);

		return dayPeriodsStandaloneMap;
	}

	private static Map<String, Object> daysFormatExtract(String locale, JSONObject content) {
		// daysFormat narrow
		String daysFormatNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.format.narrow").toString();
		Map<String, Object> daysFormatNarrowMap = JSONUtil.getMapFromJson(daysFormatNarrow);
		List<Object> daysFormatNarrowArr = new ArrayList<Object>();
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("sun").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("mon").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("tue").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("wed").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("thu").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("fri").toString());
		daysFormatNarrowArr.add(daysFormatNarrowMap.get("sat").toString());

		// daysFormat abbreviated
		String daysFormatAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.format.abbreviated").toString();
		Map<String, Object> daysFormatAbbrMap = JSONUtil.getMapFromJson(daysFormatAbbreviated);
		List<Object> daysFormatAbbrArr = new ArrayList<Object>();
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("sun").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("mon").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("tue").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("wed").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("thu").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("fri").toString());
		daysFormatAbbrArr.add(daysFormatAbbrMap.get("sat").toString());

		// daysFormat wide
		String daysFormatWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.format.wide").toString();
		Map<String, Object> daysFormatWideMap = JSONUtil.getMapFromJson(daysFormatWide);
		List<Object> daysFormatWideArr = new ArrayList<Object>();
		daysFormatWideArr.add(daysFormatWideMap.get("sun").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("mon").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("tue").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("wed").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("thu").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("fri").toString());
		daysFormatWideArr.add(daysFormatWideMap.get("sat").toString());

		// daysFormat short
		String daysFormatShort = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.format.short").toString();
		Map<String, Object> daysFormatShortMap = JSONUtil.getMapFromJson(daysFormatShort);
		List<Object> daysFormatShortArr = new ArrayList<Object>();
		daysFormatShortArr.add(daysFormatShortMap.get("sun").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("mon").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("tue").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("wed").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("thu").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("fri").toString());
		daysFormatShortArr.add(daysFormatShortMap.get("sat").toString());

		Map<String, Object> daysFormatMap = new LinkedHashMap<String, Object>();
		daysFormatMap.put("narrow", daysFormatNarrowArr);
		daysFormatMap.put("abbreviated", daysFormatAbbrArr);
		daysFormatMap.put("wide", daysFormatWideArr);
		daysFormatMap.put("short", daysFormatShortArr);

		return daysFormatMap;
	}

	private static Map<String, Object> daysStandaloneExtract(String locale, JSONObject content) {
		// daysStandalone narrow
		String daysStandaloneNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.stand-alone.narrow").toString();
		Map<String, Object> daysStandaloneNarrowMap = JSONUtil.getMapFromJson(daysStandaloneNarrow);
		List<Object> daysStandaloneNarrowArr = new ArrayList<Object>();
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("sun").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("mon").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("tue").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("wed").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("thu").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("fri").toString());
		daysStandaloneNarrowArr.add(daysStandaloneNarrowMap.get("sat").toString());

		// daysStandalone abbreviated
		String daysStandaloneAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.stand-alone.abbreviated")
				.toString();
		Map<String, Object> daysStandaloneAbbrMap = JSONUtil.getMapFromJson(daysStandaloneAbbreviated);
		List<Object> daysStandaloneAbbrArr = new ArrayList<Object>();
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("sun").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("mon").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("tue").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("wed").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("thu").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("fri").toString());
		daysStandaloneAbbrArr.add(daysStandaloneAbbrMap.get("sat").toString());

		// daysStandalone wide
		String daysStandaloneWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.stand-alone.wide").toString();
		Map<String, Object> daysStandaloneWideMap = JSONUtil.getMapFromJson(daysStandaloneWide);
		List<Object> daysStandaloneWideArr = new ArrayList<Object>();
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("sun").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("mon").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("tue").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("wed").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("thu").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("fri").toString());
		daysStandaloneWideArr.add(daysStandaloneWideMap.get("sat").toString());

		// daysStandalone short
		String daysStandaloneShort = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.days.stand-alone.short").toString();
		Map<String, Object> daysStandaloneShortMap = JSONUtil.getMapFromJson(daysStandaloneShort);
		List<Object> daysStandaloneShortArr = new ArrayList<Object>();
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("sun").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("mon").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("tue").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("wed").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("thu").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("fri").toString());
		daysStandaloneShortArr.add(daysStandaloneShortMap.get("sat").toString());

		Map<String, Object> daysStandaloneMap = new LinkedHashMap<String, Object>();
		daysStandaloneMap.put("narrow", daysStandaloneNarrowArr);
		daysStandaloneMap.put("abbreviated", daysStandaloneAbbrArr);
		daysStandaloneMap.put("wide", daysStandaloneWideArr);
		daysStandaloneMap.put("short", daysStandaloneShortArr);

		return daysStandaloneMap;
	}

	private static Map<String, Object> monthsFormatExtract(String locale, JSONObject content) {
		// monthsFormat narrow
		String monthsFormatNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.format.narrow").toString();
		Map<String, Object> monthsFormatNarrowMap = JSONUtil.getMapFromJson(monthsFormatNarrow);
		List<Object> monthsFormatNarrowArr = new ArrayList<Object>();
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("1").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("2").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("3").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("4").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("5").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("6").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("7").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("8").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("9").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("10").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("11").toString());
		monthsFormatNarrowArr.add(monthsFormatNarrowMap.get("12").toString());

		// monthsFormat abbreviated
		String monthsFormatAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.format.abbreviated").toString();
		Map<String, Object> monthsFormatAbbrMap = JSONUtil.getMapFromJson(monthsFormatAbbreviated);
		List<Object> monthsFormatAbbrArr = new ArrayList<Object>();
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("1").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("2").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("3").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("4").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("5").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("6").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("7").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("8").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("9").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("10").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("11").toString());
		monthsFormatAbbrArr.add(monthsFormatAbbrMap.get("12").toString());

		// monthsFormat wide
		String monthsFormatWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.format.wide").toString();
		Map<String, Object> monthsFormatWideMap = JSONUtil.getMapFromJson(monthsFormatWide);
		List<Object> monthsFormatWideArr = new ArrayList<Object>();
		monthsFormatWideArr.add(monthsFormatWideMap.get("1").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("2").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("3").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("4").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("5").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("6").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("7").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("8").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("9").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("10").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("11").toString());
		monthsFormatWideArr.add(monthsFormatWideMap.get("12").toString());

		Map<String, Object> monthsFormatMap = new LinkedHashMap<String, Object>();
		monthsFormatMap.put("narrow", monthsFormatNarrowArr);
		monthsFormatMap.put("abbreviated", monthsFormatAbbrArr);
		monthsFormatMap.put("wide", monthsFormatWideArr);

		return monthsFormatMap;
	}

	private static Map<String, Object> monthsStandaloneExtract(String locale, JSONObject content) {
		// monthsStandalone narrow
		String monthsStandaloneNarrow = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.stand-alone.narrow").toString();
		Map<String, Object> monthsStandaloneNarrowMap = JSONUtil.getMapFromJson(monthsStandaloneNarrow);
		List<Object> monthsStandaloneNarrowArr = new ArrayList<Object>();
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("1").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("2").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("3").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("4").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("5").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("6").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("7").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("8").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("9").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("10").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("11").toString());
		monthsStandaloneNarrowArr.add(monthsStandaloneNarrowMap.get("12").toString());

		// monthsStandalone abbreviated
		String monthsStandaloneAbbreviated = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.stand-alone.abbreviated")
				.toString();
		Map<String, Object> monthsStandaloneAbbrMap = JSONUtil.getMapFromJson(monthsStandaloneAbbreviated);
		List<Object> monthsStandaloneAbbrArr = new ArrayList<Object>();
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("1").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("2").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("3").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("4").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("5").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("6").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("7").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("8").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("9").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("10").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("11").toString());
		monthsStandaloneAbbrArr.add(monthsStandaloneAbbrMap.get("12").toString());

		// monthsStandalone wide
		String monthsStandaloneWide = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.months.stand-alone.wide").toString();
		Map<String, Object> monthsStandaloneWideMap = JSONUtil.getMapFromJson(monthsStandaloneWide);
		List<Object> monthsStandaloneWideArr = new ArrayList<Object>();
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("1").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("2").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("3").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("4").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("5").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("6").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("7").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("8").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("9").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("10").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("11").toString());
		monthsStandaloneWideArr.add(monthsStandaloneWideMap.get("12").toString());

		Map<String, Object> monthsStandaloneMap = new LinkedHashMap<String, Object>();
		monthsStandaloneMap.put("narrow", monthsStandaloneNarrowArr);
		monthsStandaloneMap.put("abbreviated", monthsStandaloneAbbrArr);
		monthsStandaloneMap.put("wide", monthsStandaloneWideArr);

		return monthsStandaloneMap;
	}

	private static Map<String, Object> erasExtract(String locale, JSONObject content) {
		// eras narrow(eraNarrow)
		String erasNarrow = JSONUtil.select(content, "main." + locale + ".dates.calendars.gregorian.eras.eraNarrow")
				.toString();
		Map<String, Object> erasNarrowMap = JSONUtil.getMapFromJson(erasNarrow);
		List<Object> erasNarrowArr = new ArrayList<Object>();
		erasNarrowArr.add(erasNarrowMap.get("0").toString());
		erasNarrowArr.add(erasNarrowMap.get("1").toString());

		// eras abbreviated(eraAbbr)
		String erasAbbreviated = JSONUtil.select(content, "main." + locale + ".dates.calendars.gregorian.eras.eraAbbr")
				.toString();
		Map<String, Object> erasAbbrMap = JSONUtil.getMapFromJson(erasAbbreviated);
		List<Object> erasAbbrArr = new ArrayList<Object>();
		erasAbbrArr.add(erasAbbrMap.get("0").toString());
		erasAbbrArr.add(erasAbbrMap.get("1").toString());

		// eras wide(eraNames)
		String erasWide = JSONUtil.select(content, "main." + locale + ".dates.calendars.gregorian.eras.eraNames")
				.toString();
		Map<String, Object> erasWideMap = JSONUtil.getMapFromJson(erasWide);
		List<Object> erasWideArr = new ArrayList<Object>();
		erasWideArr.add(erasWideMap.get("0").toString());
		erasWideArr.add(erasWideMap.get("1").toString());

		Map<String, Object> erasMap = new LinkedHashMap<String, Object>();
		erasMap.put("narrow", erasNarrowArr);
		erasMap.put("abbreviated", erasAbbrArr);
		erasMap.put("wide", erasWideArr);

		return erasMap;
	}

	/**
	 * Get the first day of one Country, the implementation is according to
	 * https://www.unicode.org/reports/tr35/tr35-49/tr35-dates.html#Week_Data
	 *
	 * @param localeStr
	 * @param firstDayData
	 * @return
	 */
	private static Integer extractFirstDay(String localeStr, Map<String, String> firstDayData) {
		String firstDay = firstDayData.get(Constants.TERRITORY_001);
		String territory = LocaleDataUtils.getInstance().getDefaultRegionCode(localeStr,
				LocaleDataUtils.getInstance().getRegionsData(localeStr));
		if (territory != null && firstDayData.get(territory) != null)
			firstDay = firstDayData.get(territory);
		return DayEnum.getIndexByDay(firstDay);
	}

	/**
	 * Get the weekend range of one Country, the implementation is according to
	 * https://www.unicode.org/reports/tr35/tr35-49/tr35-dates.html#Week_Data
	 *
	 * @param localeStr
	 * @param supplementalWeekData
	 * @return
	 */
	private static List<Integer> extractWeekendRange(String localeStr, Map<String, Object> supplementalWeekData) {
		@SuppressWarnings("unchecked")
		Map<String, String> weekendStartData = (Map<String, String>) supplementalWeekData.get("weekendStart");
		@SuppressWarnings("unchecked")
		Map<String, String> weekendEndData = (Map<String, String>) supplementalWeekData.get("weekendEnd");
		String weekendStart = weekendStartData.get(Constants.TERRITORY_001);
		String weekendEnd = weekendEndData.get(Constants.TERRITORY_001);
		String territory = LocaleDataUtils.getInstance().getDefaultRegionCode(localeStr,
				LocaleDataUtils.getInstance().getRegionsData(localeStr));
		if (territory != null) {
			if (weekendStartData.get(territory) != null)
				weekendStart = weekendStartData.get(territory);
			if (weekendEndData.get(territory) != null)
				weekendEnd = weekendEndData.get(territory);
		}
		return Arrays.asList(DayEnum.getIndexByDay(weekendStart), DayEnum.getIndexByDay(weekendEnd));
	}

	private static Map<String, Object> dateFormatExtract(String locale, JSONObject content) {
		String dateFormat = JSONUtil.select(content, "main." + locale + ".dates.calendars.gregorian.dateFormats")
				.toString();
		return JSONUtil.getMapFromJson(dateFormat);
	}

	private static Map<String, Object> timeFormatExtract(String locale, JSONObject content) {
		String timeFormat = JSONUtil.select(content, "main." + locale + ".dates.calendars.gregorian.timeFormats")
				.toString();
		return JSONUtil.getMapFromJson(timeFormat);
	}

	private static Map<String, Object> dateTimeFormatExtract(String locale, JSONObject content) {
		String dateTimeShort = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.short").toString();
		String dateTimeMedium = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.medium").toString();
		String dateTimeLong = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.long").toString();
		String dateTimeFull = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.full").toString();
		String availableFormats = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.availableFormats")
				.toString();
		String appendItems = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.appendItems")
				.toString();
		String intervalFormats = JSONUtil
				.select(content, "main." + locale + ".dates.calendars.gregorian.dateTimeFormats.intervalFormats")
				.toString();
		Map<String, Object> dateTimeMap = new LinkedHashMap<String, Object>();
		dateTimeMap.put("short", dateTimeShort);
		dateTimeMap.put("medium", dateTimeMedium);
		dateTimeMap.put("long", dateTimeLong);
		dateTimeMap.put("full", dateTimeFull);
		dateTimeMap.put("appendItems", JSONUtil.string2SortMap(appendItems));
		dateTimeMap.put("intervalFormats", JSONUtil.string2SortMap(intervalFormats));

		Map<String, Object> timeFormatMap = timeFormatExtract(locale, content);
		Map<String, Object> availableFormatsMap = JSONUtil.string2SortMap(availableFormats);
		List<String> configs = Arrays.asList(CLDRUtils.CLDR_ADDITIONAL_CONFIG.split(","));

		if (!configs.isEmpty() && configs.contains("availableFormats") && timeFormatMap.containsKey("full")) {
			String regEx = "(\\.ss|:ss| ss 's'|ss秒| s초| ss ວິນາທີ| ss วินาที)";
			availableFormatsMap.put("hmz", timeFormatMap.get("full").toString().replaceAll(regEx, ""));
		}
		dateTimeMap.put("availableFormats", availableFormatsMap);
		return dateTimeMap;
	}

	public static Map<String, Object> numberDataExtract(String locale) {
		String zipPath = CLDRConstants.NUMBER_ZIP_FILE_PATH;
		String fileName = MessageFormat.format(CLDRConstants.CLDR_NUMBERS_FULL_NUMBERS, CLDR_VERSION, locale);
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject numberContents = JSONUtil.string2JSON(json);

		String defaultNumberingSystem = JSONUtil
				.select(numberContents, MessageFormat.format(CLDRConstants.DEFAULT_NUMBER_SYSTEM, locale)).toString();

		String decimal = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.DECIMAL)).toString();
		String group = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM, locale,
				defaultNumberingSystem, Constants.GROUP)).toString();
		String list = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM, locale,
				defaultNumberingSystem, Constants.LIST)).toString();
		String percentSign = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.PERCENT_SIGN)).toString();
		String plusSign = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.PLUS_SIGN)).toString();
		String minusSign = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.MINUS_SIGN)).toString();
		String exponential = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.EXPONENTIAL)).toString();
		String superscriptingExponent = JSONUtil
				.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM, locale,
						defaultNumberingSystem, Constants.SUPER_SCRIPT_EXPONENT))
				.toString();
		String perMille = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.PER_MILLE)).toString();
		String infinity = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.INFINITY)).toString();
		String nan = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM, locale,
				defaultNumberingSystem, Constants.NAN)).toString();
		String timeSeparator = JSONUtil.select(numberContents, MessageFormat.format(CLDRConstants.SYMBOLS_NUMBER_SYSTEM,
				locale, defaultNumberingSystem, Constants.TIME_SEPARATOR)).toString();
		Map<String, Object> numberSymbolsMap = new LinkedHashMap<String, Object>();
		numberSymbolsMap.put(Constants.DECIMAL, decimal);
		numberSymbolsMap.put(Constants.GROUP, group);
		numberSymbolsMap.put(Constants.LIST, list);
		numberSymbolsMap.put(Constants.PERCENT_SIGN, percentSign);
		numberSymbolsMap.put(Constants.PLUS_SIGN, plusSign);
		numberSymbolsMap.put(Constants.MINUS_SIGN, minusSign);
		numberSymbolsMap.put(Constants.EXPONENTIAL, exponential);
		numberSymbolsMap.put(Constants.SUPER_SCRIPT_EXPONENT, superscriptingExponent);
		numberSymbolsMap.put(Constants.PER_MILLE, perMille);
		numberSymbolsMap.put(Constants.INFINITY, infinity);
		numberSymbolsMap.put(Constants.NAN, nan);
		numberSymbolsMap.put(Constants.TIME_SEPARATOR, timeSeparator);

		Map<String, Object> numberMap = new LinkedHashMap<>();
		numberMap.put(Constants.DEFAULT_NUMBER_SYSTEM, defaultNumberingSystem);
		numberMap.put(Constants.NUMBER_SYMBOLS, numberSymbolsMap);
		numberMap.put(Constants.NUMBER_FORMATS,
				getNumberFormatData(numberContents, locale, defaultNumberingSystem, Constants.STANDARD));
		return numberMap;
	}

	private static Map<String, Object> getNumberFormatData(JSONObject numberContents, String locale,
			String defaultNumberingSystem, String tag) {
		Object decimalFormats = JSONUtil.select(numberContents,
				MessageFormat.format(CLDRConstants.DECIMAL_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, tag));
		Object percentFormats = JSONUtil.select(numberContents,
				MessageFormat.format(CLDRConstants.PERCENT_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, tag));
		Object currencyFormats = JSONUtil.select(numberContents, MessageFormat
				.format(CLDRConstants.CURRENCY_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, tag));
		Object scientificFormats = JSONUtil.select(numberContents, MessageFormat
				.format(CLDRConstants.SCIENTIFIC_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, tag));
		Object decimalFormats_long = JSONUtil.select(numberContents, MessageFormat
				.format(CLDRConstants.DECIMAL_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, Constants.LONG));
		Object decimalFormats_short = JSONUtil.select(numberContents, MessageFormat
				.format(CLDRConstants.DECIMAL_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, Constants.SHORT));
		Object currencyFormats_short = JSONUtil.select(numberContents, MessageFormat
				.format(CLDRConstants.CURRENCY_FORMATS_NUMBER_SYSTEM, locale, defaultNumberingSystem, Constants.SHORT));

		Map<String, Object> numberFormatsMap = new LinkedHashMap<>();
		numberFormatsMap.put(Constants.DECIMAL_FORMATS, decimalFormats);
		numberFormatsMap.put(Constants.PERCENT_FORMATS, percentFormats);
		numberFormatsMap.put(Constants.CURRENCY_FORMATS, currencyFormats);
		numberFormatsMap.put(Constants.SCIENTIFIC_FORMATS, scientificFormats);
		numberFormatsMap.put(Constants.DECIMAL_FORMATS_LONG, CommonUtil.isEmpty(decimalFormats_long) ? null
				: dataSort(decimalFormats_long, Constants.DECIMAL_FORMAT));
		numberFormatsMap.put(Constants.DECIMAL_FORMATS_SHORT, CommonUtil.isEmpty(decimalFormats_long) ? null
				: dataSort(decimalFormats_short, Constants.DECIMAL_FORMAT));
		numberFormatsMap.put(Constants.CURRENCY_FORMATS_SHORT,
				CommonUtil.isEmpty(decimalFormats_long) ? null : dataSort(currencyFormats_short, Constants.STANDARD));
		return numberFormatsMap;

	}

	private static Map<String, Object> dataSort(Object data, String key) {
		if (data instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> datMap = (Map<String, Object>) data;
			Map<String, Object> sortedMap = new HashMap<>();
			sortedMap.put(key, JSONUtil.string2SortMap(datMap.get(key).toString()));
			return sortedMap;
		}

		return null;
	}

	/**
	 * Get all data from cldr 'likelySubtags.json' file
	 *
	 * @return likelysubtags.json file data as Map<String,Object> form
	 */
	public static Map<String, Object> getLikelySubtagsData() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = "cldr-core-" + CLDR_VERSION + "/supplemental/likelySubtags.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject likelySubtagsDataContents = JSONUtil.string2JSON(json);
		String likelySubtagsJson = JSONUtil.select(likelySubtagsDataContents, "supplemental.likelySubtags").toString();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		Map<String, Object> map = JSONUtil.getMapFromJson(likelySubtagsJson);
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			result.put(entry.getKey().toLowerCase(), entry.getValue());
		}
		return result;
	}

	/**
	 * Get all locales from cldr availableLocales.json file for extraction and
	 * currency matching
	 *
	 * @return locales map collection
	 */
	public static Map<String, String> getAllCldrLocales() {
		Map<String, String> localesMap = new TreeMap<String, String>();
		String fileName = "cldr-core-" + CLDR_VERSION + "/availableLocales.json";
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject allLocalesContents = JSONUtil.string2JSON(json);
		JSONArray array = (JSONArray) JSONUtil.select(allLocalesContents, "availableLocales.full");
		for (Object item : array) {
			String locale = item.toString();
			if ("yue".equals(locale)) {// no data for yue locale, so skip it, or data extract process will throw exception
				continue;
			}
			localesMap.put(locale.toLowerCase(), locale);
		}
		return localesMap;
	}

	/**
	 * Get all currency data from 'currencyData.json' file for symbol and name
	 *
	 * @param locale
	 * @return Map<String , Object>
	 */
	public static Map<String, Object> getCurrencyData(String locale) {
		String zipPath = CLDRConstants.NUMBER_ZIP_FILE_PATH;
		String fileName = "cldr-numbers-full-" + CLDR_VERSION + "/main/" + locale + "/currencies.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject currencyDataContents = JSONUtil.string2JSON(json);
		String currenciesJson = JSONUtil.select(currencyDataContents, "main." + locale + ".numbers.currencies")
				.toString();
		return JSONUtil.string2SortMap(currenciesJson);
	}

	/**
	 * Get all currency data from 'currencyData.json' file for symbol and name
	 *
	 * @return Map<String , Object>
	 */
	public static Map<String, Object> getCurrencySupplementalData() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = "cldr-core-" + CLDR_VERSION + "/supplemental/currencyData.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject currencyDataContents = JSONUtil.string2JSON(json);
		String currencyDataRegionJson = JSONUtil.select(currencyDataContents, "supplemental.currencyData").toString();
		return JSONUtil.getMapFromJson(currencyDataRegionJson);
	}

	/**
	 * Get all plural data from 'plurals.json' file for single plural
	 *
	 * @param locale
	 * @return
	 */
	public static Map<String, Object> getPluralsData(String locale) {
		Map<String, Object> plural = new HashMap<String, Object>();
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = CLDRConstants.CLDR_CORE_PLURALS;
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject pluralDataContents = JSONUtil.string2JSON(json);
		String node = CLDRConstants.PLURALS_KEY_PATH + "." + locale;
		if (CommonUtil.isEmpty(JSONUtil.select(pluralDataContents, node))) {
			// VIP-1815:[GetPattern APIs]Plurals returned wrongly when language=pt and
			// region=PT or locale=pt-PT
			node = CLDRConstants.PLURALS_KEY_PATH + "." + locale.split("-")[0];
			if (CommonUtil.isEmpty(JSONUtil.select(pluralDataContents, node))) {
				plural.put(Constants.PLURAL_RULES, "");
				return plural;
			}
		}
		String pluralDataJson = JSONUtil.select(pluralDataContents, node).toString();
		plural.put(Constants.PLURAL_RULES, JSONUtil.getMapFromJson(pluralDataJson));
		return plural;
	}

	/**
	 * Get CLDR measurement data
	 *
	 * @param locale
	 * @return
	 */
	public static Map<String, Object> getMeasurementsData(String locale) {
		String zipPath = CLDRConstants.UNIT_ZIP_FILE_PATH;
		String fileName = "cldr-units-full-" + CLDR_VERSION + "/main/" + locale + "/units.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject unitsDataContents = JSONUtil.string2JSON(json);
		String node = "main." + locale + ".units";
		if (CommonUtil.isEmpty(JSONUtil.select(unitsDataContents, node))) {
			return null;
		}
		String unitsDataJson = JSONUtil.select(unitsDataContents, node).toString();
		return JSONUtil.getMapFromJson(unitsDataJson);
	}

	/**
	 * Get all data from cldr 'territoryInfo.json' file
	 *
	 * @return territoryInfo.json file data as Map<String,Object> form
	 */
	public static Map<String, Object> getSupplementalTerritoryInfo() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = "cldr-core-" + CLDR_VERSION + "/supplemental/territoryInfo.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject territoryDataContents = JSONUtil.string2JSON(json);
		Object territoryDataObject = JSONUtil.select(territoryDataContents, "supplemental.territoryInfo");
		if (CommonUtil.isEmpty(territoryDataObject)) {
			return null;
		}
		String resultDataJson = territoryDataObject.toString();
		return JSONUtil.string2SortMap(resultDataJson);
	}

	/**
	 * Get supplemental week data from
	 * 'cldr-core-{cldr.version}/supplemental/weekData.json' file
	 *
	 * @return Map<String , Object>
	 */
	public static Map<String, Object> getSupplementalWeekData() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = "cldr-core-" + CLDR_VERSION + "/supplemental/weekData.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject weekDataContents = JSONUtil.string2JSON(json);
		String weekDataJsonStr = JSONUtil.select(weekDataContents, "supplemental.weekData").toString();
		return JSONUtil.getMapFromJson(weekDataJsonStr);
	}

	public static void writePatternDataIntoFile(String filePath, Map<String, Object> patternMap) {
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		FileOutputStream outputStream = null;
		try {
			File f = new File(filePath);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			if (!f.exists()) {
				if (!f.createNewFile()) {
					logger.error("create file failure when write Pattern Data Into File");
				}
			}
			outputStream = new FileOutputStream(f);
			write = new OutputStreamWriter(outputStream, Constants.UTF8);
			writer = new BufferedWriter(write);
			writer.write(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(patternMap));
			writer.flush();

			logger.info("Write pattern data complete! The file path is: " + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeWriter(writer);
			IOUtil.closeWriter(write);
			IOUtil.closeOutputStream(outputStream);
		}
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void dataRecordForParse(Map<String, Object> likelySubtagMap, Map<String, String> recordMap) {
        Map<String, Object> map = new TreeMap();
        map.put("likelySubtag", new TreeMap(likelySubtagMap));
        map.put("localePath", new TreeMap(recordMap));
        writePatternDataIntoFile(GEN_CLDR_CORE_DIR + "parse.json", map);
    }

	public static void patternDataExtract() {
		logger.info("Start to extract i18n pattern data ... ");
		Map<String, Object> likelySubtags = getLikelySubtagsData();
		Map<String, String> allLocales = getAllCldrLocales();
		Map<String, Object> patternMap = null;
		Map<String, Object> rootMap = null;
		Map<String, String> recordMap = new HashMap<String, String>();
		String filePath = "";
		for (String locale : allLocales.values()) {
			rootMap = new LinkedHashMap<String, Object>();
			rootMap.put(Constants.LOCALE_ID, locale);
			patternMap = new LinkedHashMap<String, Object>();
			patternMap.put(Constants.DATES, dateDataExtract(locale));
			patternMap.put(Constants.NUMBERS, numberDataExtract(locale));
			patternMap.put(Constants.PLURALS, getPluralsData(locale));
			patternMap.put(Constants.MEASUREMENTS, getMeasurementsData(locale));
			patternMap.put(Constants.CURRENCIES, getCurrencyData(locale));
			rootMap.put(Constants.CATEGORIES, patternMap);
			filePath = GEN_CLDR_PATTERN_DIR + locale + File.separator + Constants.PATTERN_JSON;
			writePatternDataIntoFile(filePath, rootMap);
			recordMap.put(locale.toLowerCase(), locale);
			dateFieldsExtract(locale);
		}
		dataRecordForParse(likelySubtags, recordMap);
		logger.info("Extract i18n pattern data complete!");
	}

	/**
	 * Get dateFields and generate dateFields.json file
	 * 
	 * @param locale
	 */
	public static void dateFieldsExtract(String locale) {
		Map<String, Object> rootMap = new LinkedHashMap<>();
		Map<String, Object> dateFieldsMap = dateFieldsFormatExtract(locale);
		rootMap.put(Constants.LOCALE_ID, locale);
		rootMap.put(Constants.DATE_FIELDS, dateFieldsMap);
		String filePath = GEN_CLDR_PATTERN_DIR + locale + File.separator + Constants.DATE_FIELDS_JSON;
		writePatternDataIntoFile(filePath, rootMap);
	}

	/**
	 * Get a valid locale according to given Region
	 */
	@SuppressWarnings("unchecked")
	public static void regionDataExtract() {
		logger.info("Start to extract region data ... ");
		Map<String, Object> territoryInfo = getSupplementalTerritoryInfo();
		if (territoryInfo.isEmpty()) {
			logger.info("territoryInfo.json does not exist ");
			return;
		}

		Map<String, String> availableLocales = getAllCldrLocales();
		Map<String, String> defaultContentLocales = LocaleDataUtils.getInstance().getDefaultContentLocales();
		Map<String, Object> localeAliases = getSupplementalAliases();

		Map<String, Object> recordMap = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : territoryInfo.entrySet()) {
			Map<String, Object> itemMap = (Map<String, Object>) entry.getValue();
			String locale = null;
			if (!CommonUtil.isEmpty(itemMap.get(Constants.LANGUAGE_POPULATION))) {
				Map<String, String> maxPopulationLanguage = new HashMap<>();
				Map<String, Object> map = JSONUtil
						.string2SortMap(itemMap.get(Constants.LANGUAGE_POPULATION).toString());
				for (Map.Entry<String, Object> item : map.entrySet()) {
					Map<String, String> data = (Map<String, String>) item.getValue();
					boolean isMaxLanguage = getMaxLanguage(maxPopulationLanguage, data);
					boolean existPattern = isPatternExist(entry.getKey(), item.getKey(), availableLocales,
							defaultContentLocales, localeAliases);
					if (isMaxLanguage && existPattern) {
						maxPopulationLanguage.put(Constants.LANGUAGE, item.getKey());
						maxPopulationLanguage.put(Constants.OFFICIAL_STATUS, data.get(Constants.OFFICIAL_STATUS));
						maxPopulationLanguage.put(Constants.POPULATION_PERCENT, data.get(Constants.POPULATION_PERCENT));
					}
				}
				locale = maxPopulationLanguage.get(Constants.LANGUAGE);
			}
			recordMap.put(entry.getKey(), locale);
		}

		Map<String, Object> regionMap = new LinkedHashMap<>();
		regionMap.put(Constants.REGION_INFO, recordMap);
		String filePath = CLDRConstants.GEN_CLDR_REGION_DIR + File.separator + Constants.REGION_LANGUAGE_MAPPING_JSON;
		writePatternDataIntoFile(filePath, regionMap);
		logger.info("Extract region data complete!");
	}

	/**
	 * Find the largest language based on "populationPercent" and "officialStatus"
	 */
	private static boolean getMaxLanguage(Map<String, String> maxPopulationLanguage, Map<String, String> data) {
		if (maxPopulationLanguage.size() == 0) {
			return true;
		}
		OfficialStatusEnum maxOfficialStatus = OfficialStatusEnum
				.getOfficialStatusEnumByText(maxPopulationLanguage.get(Constants.OFFICIAL_STATUS));
		OfficialStatusEnum officialStatus = OfficialStatusEnum
				.getOfficialStatusEnumByText(data.get(Constants.OFFICIAL_STATUS));
		double maxPopulationPercent = maxPopulationLanguage.get(Constants.POPULATION_PERCENT) == null ? -1
				: Double.valueOf(maxPopulationLanguage.get(Constants.POPULATION_PERCENT));
		double newPopulationPercent = data.get(Constants.POPULATION_PERCENT) == null ? -1
				: Double.valueOf(data.get(Constants.POPULATION_PERCENT));
		if (officialStatus == maxOfficialStatus) {
			if (newPopulationPercent > maxPopulationPercent) {
				return true;
			}
		} else if (officialStatus.getType() < maxOfficialStatus.getType()) {
			return true;
		}
		return false;
	}

	/**
	 * Query the availableLocales.JSON of CLDR to determine if there is a pattern
	 * json and if not, query defaultContent.json
	 *
	 * @param region
	 * @param language
	 * @param availableLocales
	 * @param defaultContentLocales
	 * @param localeAliases
	 * @return
	 */
	private static boolean isPatternExist(String region, String language, Map<String, String> availableLocales,
			Map<String, String> defaultContentLocales, Map<String, Object> localeAliases) {
		if (language.contains("_")) {
			language = language.replace("_", "-");
		}
		String newLocale = language + "-" + region;
		String matchLocale = CommonUtil.getMatchingLocale(localeAliases, newLocale);
		if (!CommonUtil.isEmpty(matchLocale)) {
			newLocale = matchLocale;
		}

		for (String locale : availableLocales.values()) {
			if (locale.equals(newLocale)) {
				return true;
			}
		}

		for (String defaultRegion : defaultContentLocales.values()) {
			if (defaultRegion.equals(newLocale)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extract defaultContent.json
	 */
	public static void defaultContentExtract() {
		logger.info("Start to extract defaultContent data ... ");

		Map<String, String> allDefaultRegions = LocaleDataUtils.getInstance().getDefaultContentLocales();
		String filePath = CLDRConstants.GEN_CLDR_DEFAULT_CONTENT_DIR + File.separator + Constants.DEFAULT_CONTENT_JSON;
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put(Constants.DEFAULT_CONTENT, allDefaultRegions);
		writePatternDataIntoFile(filePath, resultMap);

		logger.info("Extract defaultContent data complete!");
	}

	/**
	 * Get all data from cldr 'aliases.json' file
	 *
	 * @return aliases.json file data as Map<String,Object> form
	 */
	public static Map<String, Object> getSupplementalAliases() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = CLDRConstants.CLDR_CORE_ALIASES;
		String json = readZip(fileName, zipPath);
		JSONObject aliasesDataContents = JSONUtil.string2JSON(json);
		Object aliasesDataObject = JSONUtil.select(aliasesDataContents, CLDRConstants.ALIAS_KEY_PATH);
		if (CommonUtil.isEmpty(aliasesDataObject)) {
			return null;
		}
		String resultDataJson = aliasesDataObject.toString();
		return JSONUtil.string2SortMap(resultDataJson);
	}

	/**
	 * Extract aliases.json
	 */
	public static void aliasesExtract() {
		logger.info("Start to extract aliases data ... ");

		Map<String, Object> aliases = getSupplementalAliases();
		String filePath = CLDRConstants.GEN_CLDR_ALIASES_DIR + File.separator + Constants.ALIASES_JSON;
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put(Constants.LANGUAGE_ALIASES, aliases);
		writePatternDataIntoFile(filePath, resultMap);

		logger.info("Extract extract data complete!");
	}

	/**
	 * Get all plural data from 'plurals.json' file for single plural
	 *
	 * @return
	 */
	public static Map<String, Object> getPluralsData() {
		String json = CLDRUtils.readZip(CLDRConstants.CLDR_CORE_PLURALS, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject pluralDataContents = JSONUtil.string2JSON(json);
		Object pluralDataObject = JSONUtil.select(pluralDataContents, CLDRConstants.PLURALS_KEY_PATH);
		if (CommonUtil.isEmpty(pluralDataObject)) {
			return null;
		}

		String resultDataJson = pluralDataObject.toString();
		return JSONUtil.string2SortMap(resultDataJson);
	}

	/**
	 * Extract plurals.json
	 */
	public static void pluralsExtract() {
		logger.info("Start to extract plurals data ... ");

		Map<String, Object> pluralsData = getPluralsData();
		String filePath = CLDRConstants.GEN_CLDR_PLURALS_DIR + File.separator + Constants.PLURALS_JSON;
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put(Constants.PLURAL_INFO, pluralsData);
		writePatternDataIntoFile(filePath, resultMap);

		logger.info("Extract plurals data complete!");
	}

	/**
	 * Get all scripts data from 'languageData.json' file for single plural
	 *
	 * @return
	 */
	public static Map<String, Object> getLanguageData() {
		String json = CLDRUtils.readZip(CLDRConstants.CLDR_CORE_LANGUAGE_DATA, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject languageDataContents = JSONUtil.string2JSON(json);
		Object languageDataObject = JSONUtil.select(languageDataContents, CLDRConstants.LANGUAGE_DATA_KEY_PATH);
		if (CommonUtil.isEmpty(languageDataObject)) {
			return null;
		}

		String resultDataJson = languageDataObject.toString();
		return JSONUtil.string2SortMap(resultDataJson);
	}

	/**
	 * Extract languageData.json
	 */
	public static void languageDataExtract() {
		logger.info("Start to extract languageData.json data ... ");

		Map<String, Object> languageData = getLanguageData();

		String filePath = CLDRConstants.GEN_CLDR_LANGUAGE_DATA_DIR + File.separator + Constants.LANGUAGE_DATA_JSON;
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put(Constants.LANGUAGE_DATA, languageData);
		writePatternDataIntoFile(filePath, resultMap);

		logger.info("Extract languageData.json data complete!");
	}

	public static void writeJsonStr2File(String filePath, String jsonStr) {
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		FileOutputStream outputStream = null;
		try {
			File f = new File(filePath);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			if (!f.exists()) {
				if (!f.createNewFile()) {
					logger.error("create file failure when write Json String to File");
				}
			}
			logger.info(f.getAbsolutePath());
			outputStream = new FileOutputStream(f);
			write = new OutputStreamWriter(outputStream, Constants.UTF8);
			writer = new BufferedWriter(write);
			writer.write(jsonStr);
			writer.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtil.closeWriter(writer);
			IOUtil.closeWriter(write);
			IOUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * Extract timeZoneNames.json
	 */
	public static void patternTimeZoneNameExtract() {
		logger.info("Start to extract timezonename pattern data ... ");
		Map<String, String> allLocales = getAllCldrLocales();
		logger.info("allLocales size is : " + allLocales.size());
		String coreZipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String metaZonefileName = CLDRConstants.CLDR_CORE_METAZONE;
		logger.info(metaZonefileName);
		String metaZonejson = CLDRUtils.readZip(metaZonefileName, coreZipPath);
		logger.info(metaZonejson);
		JSONObject metaZoneObj = JSONUtil.string2JSON(metaZonejson);
		for (String locale : allLocales.values()) {
			logger.info("locale is:" + locale);
			String zipPath = CLDRConstants.DATE_ZIP_FILE_PATH;
			String timeZoneNameFileName = MessageFormat.format(CLDRConstants.CLDR_DATES_FULL_DATE_TIMEZONENAME,
					CLDR_VERSION, locale);
			String timeZoneNameJson = CLDRUtils.readZip(timeZoneNameFileName, zipPath);
			logger.info(timeZoneNameJson);
			JSONObject timeZoneNameObj = JSONUtil.string2JSON(timeZoneNameJson);
			String jsonStr = CldrTimeZoneUtils.createTimeZoneNameJson(metaZoneObj, timeZoneNameObj, locale);
			String filePath = GEN_CLDR_PATTERN_TIMEZONE_DIR + locale + File.separator + Constants.DATE_TIMEZONENAME;
			writeJsonStr2File(filePath, jsonStr);
		}
	}

	public static void main(String[] args) {
		patternTimeZoneNameExtract();
	}

}

