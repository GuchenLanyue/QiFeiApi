package com.qifei.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
	private HashMap<String, Object> map = new HashMap<>();
	private String src = "";
	private String platform = "";
	public ExcelReader() {
		// TODO Auto-generated constructor stub
	}

	public ExcelReader(String src,String platform) {
		// TODO Auto-generated constructor stub
		this.src = src;
		this.platform = platform;
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
			Assert.fail("File not found :" + fileName);
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
	public HashMap<String, Object> mapFromSheet(Workbook workbook, String sheetName, String caseName) {

//		Workbook workbook = null;
		Sheet sheet = null;
		int rowNum = 0;

		HashMap<String, Object> map = new HashMap<String, Object>();

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

		Assert.assertTrue(rowNum != 0, "Not found API " + caseName +" in sheet:[" + sheetName + "]!");

		for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
			if (sheet.getRow(0).getCell(i).toString() == null) {
				throw new IllegalArgumentException(sheetName + "第1行第" + i + "列的值为空!!!");
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
	
	public Object getCell(Cell cell) {
		// DecimalFormat df = new DecimalFormat("#");
		if(cell != null){
			switch (cell.getCellTypeEnum()) {
				case _NONE:
					return "";
				case NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
					}
					return cell.getNumericCellValue();
				case STRING:
					// System.out.println(cell.getStringCellValue());
					return replace(cell.getStringCellValue());
				case FORMULA:
					return cell.getCellFormula();
				case BLANK:
					return "";
				case BOOLEAN:
					return cell.getBooleanCellValue() + "";
				case ERROR:
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
			int endIndex = str1.indexOf("}",startIndex);
			
			String fileName = "";
			String paramter = "";
			String value = "";
			if(str1.contains(".")){
				int splitCharIndex = str1.indexOf(".",startIndex);
				fileName = str1.substring(beginIndex+1,splitCharIndex);
				paramter = str1.substring(splitCharIndex+1,endIndex);
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JsonPath json = JsonPath.with(body);
				value = json.get(paramter).toString();
			}else{
				fileName = str1.substring(beginIndex+1,endIndex);
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JSONObject object = new JSONObject(body);
				value = object.toString();
			}
			
			str1 = str1.substring(0,startIndex) + value + str1.substring(endIndex+1,str1.length());
			if(str1.equals("null")){
				str1="";
			}
		}
		
		while(str1.contains("$temp{")){
			int startIndex = str1.indexOf("$temp{");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			String formulaStr = str1.substring(beginIndex+1,endIndex);
			String[] formulaArr = formulaStr.split("\\?");
			String[] exceptArr = formulaArr[0].split("\\.");
			String[] formula = formulaArr[1].split("=");
			String fileName = "";
			String paramter = "";
			String value = "";
			
			if(exceptArr.length==2){
				fileName = exceptArr[0];
				paramter = exceptArr[1];
				
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JsonPath json = JsonPath.with(body);
				JSONObject jsonObject = new JSONObject(body);
				
				List<Object> list = new ArrayList<>();
				String exceptValue = formula[1].substring(1, formula[1].length()-1);
				list = json.getList(paramter+"."+formula[0]);
				int index = 0;
				for(index = 0; index < list.size(); index ++ ){
					if(list.get(index).equals(exceptValue)){
						break;
					}else{
						continue;
					}
				}
				JSONArray jsonArray = jsonObject.getJSONArray(paramter);
				value = jsonArray.getJSONObject(index).toString();
			}else{
				fileName = exceptArr[0];
				paramter = exceptArr[1];
				
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JsonPath json = JsonPath.with(body);
				List<String> list = new ArrayList<>();
				String exceptValue = formula[1].substring(1, formula[1].length()-1);
				list = json.getList(paramter+"."+formula[0]);
				int index = 0;
				for(index = 0; index < list.size(); index ++ ){
					if(list.get(index).equals(exceptValue)){
						break;
					}else{
						continue;
					}
				}
				paramter = exceptArr[1]+"["+index+"]."+formulaArr[0].substring(formulaArr[0].indexOf(exceptArr[2]), formulaArr[0].length());
				value = json.get(paramter).toString();
			}
			
			str1 = str1.substring(0,startIndex) + value + str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("$Array{")){
			int startIndex = str1.indexOf("$Array{");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);

			int apiEndIndex = str1.indexOf("[",beginIndex);
			int indexEnd = str1.indexOf("]",apiEndIndex);
			int index = Integer.parseInt(str1.substring(apiEndIndex+1,indexEnd));
			int splitCharIndex = str1.indexOf(".",indexEnd);
			
			String fileName = str1.substring(beginIndex+1,apiEndIndex);
			String paramter = "";
			String value = "";
			if(str1.contains(".")){
				paramter = str1.substring(splitCharIndex+1,endIndex);
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JSONArray array = new JSONArray(body);
				JsonPath json = JsonPath.with(array.get(index).toString());
				value = json.getString(paramter);
			}else{
				TxtData txt = new TxtData();
				String body = txt.readTxtFile(src + "/temp/"+fileName+".txt");
				JSONArray array = new JSONArray(body);
				value = array.get(index).toString();
			}
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
			fileName = src + "/config/" + platform + "/" +fileName+".xlsx";
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
			fileName = src + "/config/" + platform + "/" +fileName+".xlsx";
			Map<String, Object> map = mapFromSheet(fileName, sheetName, caseName);
			String value = map.get(key).toString();
			str1 = str1.substring(0,startIndex) +"?normal{"+ value + "}" + str1.substring(endIndex+1,str1.length());
		}
		
		/**
		 * $sum(${ApprovalOld.number_statistics[0].total_money},${purchase.total_price})
		 * */
		while(str1.contains("$sum(")){
			int startIndex = str1.indexOf("$sum(");
			int beginIndex = str1.indexOf("(",startIndex);
			int splitCharIndex = str1.indexOf(",",startIndex);
			int endIndex = str1.indexOf(")",startIndex);
			
			String valueStr1 = str1.substring(beginIndex+1,splitCharIndex);
			String valueStr2 = str1.substring(splitCharIndex+1,endIndex);
			double value1 = Double.parseDouble(valueStr1);
			double value2 = Double.parseDouble(valueStr2);
			double value = value1 + value2;
			BigDecimal b = new BigDecimal(value);  
			double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
			str1 = str1.substring(0,startIndex) + f1 + str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.today}")){
			int startIndex = str1.indexOf("{Date.today}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String today = date.getToday();
			str1 = str1.substring(0,beginIndex)+today+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.tomorrow}")){
			int startIndex = str1.indexOf("{Date.tomorrow}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String tomorrow = date.getTomorrow();
			str1 = str1.substring(0,beginIndex)+tomorrow+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.LastMonth}")){
			int startIndex = str1.indexOf("{Date.LastMonth}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getTheLastMonth();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.Month}")){
			int startIndex = str1.indexOf("{Date.Month}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getMonth();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.TheLastMonth}")){
			int startIndex = str1.indexOf("{Date.LastMonth}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getLastMonth();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.ThisMonth}")){
			int startIndex = str1.indexOf("{Date.ThisMonth}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getThisMonth();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.TheLastYear}")){
			int startIndex = str1.indexOf("{Date.LastMonth}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getTheLastYear();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
		}
		
		while(str1.contains("{Date.Year}")){
			int startIndex = str1.indexOf("{Date.Year}");
			int beginIndex = str1.indexOf("{",startIndex);
			int endIndex = str1.indexOf("}",startIndex);
			
			DateUtils date = new DateUtils();
			String month = date.getYear();
			str1 = str1.substring(0,beginIndex)+month+str1.substring(endIndex+1,str1.length());
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
			if(str1.contains(".")){
				str1 = str1.substring(5,str1.indexOf("."));
			}else{
				str1 = str1.substring(5,str1.length());
			}
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
//		String src = System.getProperty("user.dir")+"/sources";
//		ExcelReader excel = new ExcelReader(src,"release");
//		String fileName = "C:/Users/sam/Desktop/Approval.xlsx"; 
//		String sheetName = "Expectations";
//		String caseName = "Approval_1";
//		Map<String, Object> map = excel.mapFromSheet(fileName, sheetName, caseName);
//		JSONObject obj = new JSONObject(map);
//		for(String key:obj.keySet()){
//			System.out.println(key+":"+obj.get(key).toString());
//			if(key.equals("int")){
//				JsonUtils json = new JsonUtils();
//				TxtData txt = new TxtData();
//				json.compareJSONObject(new JSONObject(txt.readTxtFile("C:/Users/sam/Desktop/new 1.txt")),obj.getJSONObject(key));
//			}
//		}
		double f = 287863.00;  
		BigDecimal b = new BigDecimal(f);  
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
		System.out.println(f1);
	}
}
