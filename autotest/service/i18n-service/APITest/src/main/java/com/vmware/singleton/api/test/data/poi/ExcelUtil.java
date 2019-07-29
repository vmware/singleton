package com.vmware.singleton.api.test.data.poi;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import com.vmware.g11n.log.GLogger;

public class ExcelUtil {
	private static String excelFilePath;
	private static List<XSSFSheet> excelWSheetList;
//	private static XSSFSheet excelWSheet;
	private static XSSFWorkbook excelWBook;
	private static XSSFCell cell;
	private static XSSFRow row;
//	public static GLogger logger = GLogger.getInstance(ExcelUtil.class.getSimpleName());
	//This method is to set the File path and to open the Excel file, Pass Excel Path and Sheetname as Arguments to this method

	public static void setExcelFile(String path, String[] sheetList) throws Exception {
		try {
			// Open the Excel file
			excelFilePath = path;
			FileInputStream ExcelFile = new FileInputStream(excelFilePath);
			// Access the required test data sheet
			excelWBook = new XSSFWorkbook(ExcelFile);
			excelWSheetList = new ArrayList<XSSFSheet>();
			for (String sheet : sheetList) {
				excelWSheetList.add(excelWBook.getSheet(sheet));
			}
		} catch (Exception e){
			System.out.println("loading test data failed.");
//			logger.error("loading test data failed.");
			System.exit(1);
		}
	}

	//This method is to read the test data from the Excel cell, in this we are passing parameters as Row num and Col num

	public static String getCellData(XSSFSheet excelWSheet, int rowNum, int columnNum) throws Exception{
		try{
			cell = excelWSheet.getRow(rowNum).getCell(columnNum);
			String CellData;
			switch (cell.getCellTypeEnum()) {
            case NUMERIC:
            	CellData = Double.toString(cell.getNumericCellValue());
				break;
			default:
				CellData = cell.getStringCellValue();
				break;
			};
			return CellData;
		}catch (Exception e){
			System.out.println(String.format("get test data cell failed, sheet='%s', row=%s, column=%s",
					excelWSheet.getSheetName(), rowNum, columnNum));
//			logger.warn(String.format("get test data cell failed, sheet='%s', row=%s, column=%s",
//					excelWSheet.getSheetName(), rowNum, columnNum));
			return"";
		}
	}

	//This method is to write in the Excel cell, Row num and Col num are the parameters

	public static void setCellData(XSSFSheet excelWSheet, String Result,  int rowNum, int columnNum) throws Exception	{
		try{
			row  = excelWSheet.getRow(rowNum);
			cell = row.getCell(columnNum, MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if (cell == null) {
				cell = row.createCell(columnNum);
				cell.setCellValue(Result);
			} else {
				cell.setCellValue(Result);
			}

			// Constant variables Test Data path and Test Data file name

			FileOutputStream fileOut = new FileOutputStream(excelFilePath);
			excelWBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		}catch(Exception e){
			System.out.println(String.format("write test result cell failed, sheet='%s', row=%s, column=%s",
					excelWSheet.getSheetName(), rowNum, columnNum));
//			logger.warn(String.format("write test result cell failed, sheet='%s', row=%s, column=%s",
//					excelWSheet.getSheetName(), rowNum, columnNum));
			throw (e);
		}
	}

	public static List<XSSFSheet> getSheetList() {
		return excelWSheetList;
	}
}
