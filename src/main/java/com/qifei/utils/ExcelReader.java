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
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import io.qameta.allure.Allure;
import io.restassured.path.json.JsonPath;

public class ExcelReader {
	private HashMap<String, Object> map = null;
	private String src = "";
//	public ExcelReader() {
//		// TODO Auto-generated constructor stub
//	}

	public ExcelReader(String src) {
		// TODO Auto-generated constructor stub
		this.src = src;
	}
	
	/**
	 * @param path
	 *            文件路径
	 * @param sheetName
	 *            sheet表名
	 * @param caseName
	 *            用例名
	 */
//	public ExcelReader(String path, String sheetName, String caseName) {
//		// TODO Auto-generated constructor stub
//		map = mapFromSheet(path, sheetName, caseName);
//	}

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
		if (cell != null){	
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
				return replace(cell.getStringCellValue());
			case Cell.CELL_TYPE_FORMULA:
				return cell.getCellFormula();
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue() + "";
			case Cell.CELL_TYPE_ERROR:
				return cell.getErrorCellValue() + "";
			default:
				return "";
			}
		}else{
			return "";
		}
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
			
			Cell cell = sheet.getRow(rowNum).getCell(i);
			map.put(sheet.getRow(0).getCell(i).toString(), getCell(cell));
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
	
	public Object replace(String str){
		String str1 = str;
		while(str1.contains("${")){
			int startIndex = str1.indexOf("${");
			int beginIndex = str1.indexOf("{",startIndex);
			int splitCharIndex = str1.indexOf(".",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			String fileName = str1.substring(beginIndex+1,splitCharIndex);
			String paramter = str1.substring(splitCharIndex+1,endIndex);
			
			TxtData txt = new TxtData();
			String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
			JsonPath json = JsonPath.with(body);
			
			String value = json.getString(paramter);
			str1 = str1.substring(0,startIndex) + value + str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("$csv{")){
			int startIndex = str1.indexOf("$csv{");
			int beginIndex = str1.indexOf("{",startIndex);
			int splitCharIndex1 = str1.indexOf(".",startIndex);
			int splitCharIndex2 = str1.indexOf(".",splitCharIndex1+1);
			int splitCharIndex3 = str1.indexOf(".",splitCharIndex2+1);
			int endIndex = str1.indexOf("}",startIndex);
			
			String fileName = str1.substring(beginIndex+1,splitCharIndex1);
			String sheetName = str1.substring(splitCharIndex1+1,splitCharIndex2);
			String caseName = str1.substring(splitCharIndex2+1,splitCharIndex3);
			String key = str1.substring(splitCharIndex3+1,endIndex);
			fileName = src + "/temp/"+fileName+".xlsx";
			Map<String, Object> map = mapFromSheet(fileName, sheetName, caseName);
			String value = map.get(key).toString();
			str1 = str1.substring(0,startIndex) + value + str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("?temp{")){
			int startIndex = str1.indexOf("?temp{");
			int beginIndex = str1.indexOf("{",startIndex);
			int splitCharIndex = str1.indexOf(".",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			String fileName = str1.substring(beginIndex+1,splitCharIndex);
			String paramter = str1.substring(splitCharIndex+1,endIndex);
			
			TxtData txt = new TxtData();
			String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
			JsonPath json = JsonPath.with(body);
			
			String value = json.getString(paramter);
			str1 = str1.substring(0,startIndex) +"?normal{" + value + "}" + str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("?csv{")){
			int startIndex = str1.indexOf("$csv{");
			int beginIndex = str1.indexOf("{",startIndex);
			int splitCharIndex1 = str1.indexOf(".",startIndex);
			int splitCharIndex2 = str1.indexOf(".",splitCharIndex1+1);
			int splitCharIndex3 = str1.indexOf(".",splitCharIndex2+1);
			int endIndex = str1.indexOf("}",startIndex);
			
			String fileName = str1.substring(beginIndex+1,splitCharIndex1);
			String sheetName = str1.substring(splitCharIndex1+1,splitCharIndex2);
			String caseName = str1.substring(splitCharIndex2+1,splitCharIndex3);
			String key = str1.substring(splitCharIndex3+1,endIndex);
			fileName = src + "/temp/"+fileName+".xlsx";
			Map<String, Object> map = mapFromSheet(fileName, sheetName, caseName);
			String value = map.get(key).toString();
			str1 = str1.substring(0,startIndex) +"?normal{"+ value + "}" + str1.substring(endIndex+1,str1.length());
		}
		
		if(str1.startsWith("{")){
			return getMap(str1);
		}else if(str1.startsWith("!!String{")){
			int beginIndex = str1.indexOf("{");
			return str1.substring(beginIndex,str1.length());
		}else if(str1.startsWith("[")){
			return getList(str1);
		}else if(str1.startsWith("!!String[")){
			int beginIndex = str1.indexOf("[");
			return str1.substring(beginIndex,str1.length());
		}else if(str1.startsWith("!!Int[")){
			int beginIndex = str1.indexOf("[");
			str1 = str1.substring(beginIndex,str1.length());
			List<Object> list = new ArrayList<>();
			for(Object obj:getList(str1)){
				list.add(Integer.parseInt(obj.toString()));
			}
			return list;
		}else if(str1.startsWith("!!Int")){
			str1 = str1.substring(5,str1.length());
			return Integer.parseInt(str1);
		}else if(str1.startsWith("!!Double")){
			str1 = str1.substring(8,str1.length());
			return Double.parseDouble(str1);
		}else if(str1.startsWith("!!Boolean")){
			str1 = str1.substring(9,str1.length());
			return Boolean.parseBoolean(str1);
		}else{
			return str1;
		}
	}
	
	public List<Object> getList(String jsonStr){
		JSONArray jsonArray = new JSONArray(jsonStr);
		List<Object> list = new ArrayList<>();
		
		for(int i=0;i<jsonArray.length();i++){
			String valueStr = jsonArray.get(i).toString();
			if(jsonArray.get(i) instanceof JSONObject){
				list.add(getMap(valueStr));
			}else if(jsonArray.get(i) instanceof JSONArray){
				list.add(getList(valueStr));
			}else if(jsonArray.get(i) instanceof Integer){
				list.add(Integer.parseInt(valueStr));
			}else if(jsonArray.get(i) instanceof Double){
				list.add(Double.parseDouble(valueStr));
			}else if(jsonArray.get(i) instanceof Boolean){
				list.add(Boolean.parseBoolean(valueStr));
			}else{
				list.add(replace(valueStr));
			}
		}
		
		return list;
	}
	
	public Map<String, Object> getMap(String jsonStr){
		JSONObject jsonObj = new JSONObject();
		
		if(jsonStr==null|jsonStr==""){
			return null;
		}else{
			try {
				jsonObj = new JSONObject(jsonStr);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(jsonStr);
				e.printStackTrace();
			}
		}
		
		Map<String, Object> map = new HashMap<>();
		for(Object key:jsonObj.keySet()){
			Object value = jsonObj.get(key.toString());
			String valueStr = value.toString();
			if(value instanceof JSONArray){
				map.put(key.toString(), getList(valueStr));
			}else if(value instanceof JSONObject){
				map.put(key.toString(), getMap(valueStr));
			}else if(value instanceof Double){
				map.put(key.toString(), Double.parseDouble(valueStr));
			}else if(value instanceof Integer){
				map.put(key.toString(), Integer.parseInt(valueStr));
			}else if(value instanceof Boolean){
				map.put(key.toString(), Boolean.parseBoolean(valueStr));
			}else{
				map.put(key.toString(), replace(valueStr));
			}
		}
		
		return map;
	}
	
	public static void main(String[] args) {
		String src = System.getProperty("user.dir")+"/sources";
		ExcelReader excel = new ExcelReader(src);
		String fileName = src + "/case/ApprovalTypes.xlsx"; 
		String sheetName = "Params";
		String caseName = "trip_2";
		Map<String, Object> map = excel.mapFromSheet(fileName, sheetName, caseName);
		for(String key:map.keySet()){
			System.out.println(key);
			System.out.println(map.get(key).toString());
		}
	}
}
