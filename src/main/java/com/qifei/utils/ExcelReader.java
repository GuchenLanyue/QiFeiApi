package com.qifei.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

import io.qameta.allure.Allure;

public class ExcelReader {
	private HashMap<String, Object> map = null;

	public ExcelReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param path
	 *            文件路径
	 * @param sheetName
	 *            sheet表名
	 * @param caseName
	 *            用例名
	 */
	public ExcelReader(String path, String sheetName, String caseName) {
		// TODO Auto-generated constructor stub
		map = mapFromSheet(path, sheetName, caseName);
	}

	public HashMap<String, Object> getCaseMap() {
		return map;
	}

	/**
	 * 创建工作簿对象
	 * 
	 * @param filePath
	 *            Excel文件路径
	 * @return
	 * @throws IOException
	 */
	public static final Workbook setWorkbook(String filePath) throws IOException {
		Workbook wb = null;
		if (StringUtils.isBlank(filePath)) {
			throw new IllegalArgumentException("参数错误!!!");
		}
		if (filePath.trim().toLowerCase().endsWith("xls")) {
			FileInputStream xlsxFile = null;
			try {
				xlsxFile = new FileInputStream(filePath);
			} catch (Exception e) {
				// TODO: handle exception
				Allure.addAttachment("创建excel对象发生错误：", e.toString());
				e.printStackTrace();
			}
			wb = new HSSFWorkbook(xlsxFile);
			return wb;
		} else if (filePath.trim().toLowerCase().endsWith("xlsx")) {
			FileInputStream xlsxFile = null;
			try {
				xlsxFile = new FileInputStream(filePath);
			} catch (Exception e) {
				// TODO: handle exception
				Allure.addAttachment("创建excel对象发生错误：", e.toString());
				e.printStackTrace();
			}
			wb = new XSSFWorkbook(xlsxFile);
			return wb;
		} else {
			throw new IllegalArgumentException("不支持除：xls/xlsx以外的文件格式!!! " + filePath);
		}
	}

	/**
	 * 获取指定Case的数据
	 * 
	 * @param fileName
	 *            文件名(全路径)
	 * @param sheetName
	 *            sheet表名
	 * @param caseName
	 *            用例名
	 * @return HashMap<key,value> key:首行cell的值，value:指定行cell的值
	 */
	public HashMap<String, Object> mapFromSheet(String fileName, String sheetName, String caseName) {

		Workbook workbook = null;
		Sheet sheet = null;
		int rowNum = 0;

		HashMap<String, Object> map = new HashMap<String, Object>();
		File f = new File(fileName);
		if (!f.exists()) {
			Assert.fail("文件不存在：" + fileName);
		}

		try {
			workbook = setWorkbook(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sheet = workbook.getSheet(sheetName);

		for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum() + 1; i++) {

			Row row = sheet.getRow(i);
			if (row.getLastCellNum() == 0) {
				continue;
			}

			if (row.getCell(0).toString().equals(caseName)) {
				rowNum = i;
				break;
			}
		}

		Assert.assertTrue(rowNum != 0, "Not found API " + caseName +" in "+fileName + " sheet:[" + sheetName + "]!");

		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			if (sheet.getRow(0).getCell(i).toString() == null) {
				throw new IllegalArgumentException("参数错误：" + fileName + "表：" + sheetName + "第1行第" + i + "列的值为空!!!");
			}

			if (sheet.getRow(rowNum).getCell(i) == null) {
				map.put(sheet.getRow(0).getCell(i).toString(), "");
			} else {
				Cell cell = sheet.getRow(rowNum).getCell(i);
				map.put(sheet.getRow(0).getCell(i).toString(), getCell(cell));
			}

		}

		return map;
	}

	@SuppressWarnings("deprecation")
	public Object getCell(Cell cell) {
		// DecimalFormat df = new DecimalFormat("#");
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
			}
			// return df.format(cell.getNumericCellValue());
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			// System.out.println(cell.getStringCellValue());
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		case Cell.CELL_TYPE_BLANK:
			return "";
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case Cell.CELL_TYPE_ERROR:
			return cell.getErrorCellValue() + "";
		}
		return "";
	}

	/**
	 * 获取指定行的数据
	 * 
	 * @param fileName
	 *            文件名(全路径)
	 * @param sheetName
	 *            sheet表名
	 * @param caseName
	 *            用例名
	 * @return HashMap<key,value> key:首行cell的值，value:指定行cell的值
	 */
	public HashMap<String, Object> rowMap(String fileName, String sheetName, int rowNum) {
		if(rowNum==11){
			System.out.println(rowNum);
		}
		Workbook workbook = null;
		Sheet sheet = null;

		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			workbook = setWorkbook(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sheet = workbook.getSheet(sheetName);

		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			if (sheet.getRow(0).getCell(i).toString() == null) {
				throw new IllegalArgumentException("参数错误：" + fileName + "表：" + sheetName + "第1行第" + i + "列的值为空!!!");
			}

			if (sheet.getRow(rowNum).getCell(i) == null) {
				map.put(sheet.getRow(0).getCell(i).toString(), "");
			} else {
				Cell cell = sheet.getRow(rowNum).getCell(i);
				map.put(sheet.getRow(0).getCell(i).toString(), getCell(cell));
			}

		}

		return map;
	}

	public Object[][] sheetArray() {
		String[][] caseData = null;

		return caseData;
	}

	public List<Map<String, Object>> mapList(int firstRow, String filePath, String sheetName) {

		File f = new File(filePath);
		if (!f.exists()) {
			Assert.fail("文件：" + filePath + "不存在！");
		}

		Workbook wb = null;
		Sheet sheet = null;
		int rows = 0;
		List<Map<String, Object>> caseList = new ArrayList<Map<String, Object>>();

		try {
			wb = setWorkbook(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sheet = wb.getSheet(sheetName);
		rows = sheet.getLastRowNum();
		for (int i = firstRow; i < rows + 1; i++) {
			caseList.add(rowMap(filePath, sheetName, i));
		}

		return caseList;
	}
}
