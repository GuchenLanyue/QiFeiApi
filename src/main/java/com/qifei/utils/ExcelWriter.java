package com.qifei.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.groovy.util.ListHashMap;

import com.qifei.utils.http.HttpMethods;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ExcelWriter {
	public static void main(String[] args) {
		Map<String, Map<String,Object>> map = new HashMap<>();
		Map<String,Object> baseMap = new HashMap<>();
		baseMap.put("Method", "GET");
		baseMap.put("basePath", "http://nchr.release.microfastup.com");
		baseMap.put("path", "/api_doc/main.json");
		
		map.put("base", baseMap);
		
		Map<String,Object> cookieMap = new HashMap<>();
		cookieMap.put("UM_distinctid", "15b71f24cc2101-020ee61b4f9c14-8373f6a-100200-15b71f24cc312d");
		cookieMap.put("PHPSESSID", "67cth9jsol7k74kddrccld3pt0");
		
		map.put("cookies", cookieMap);
		HttpMethods http = new HttpMethods("http://nchr.release.microfastup.com");
		Response response = http.post(map);
		JsonPath json = JsonPath.with(http.getBody(response));
		List<Map<String, Object>> dirs = json.getList("apis");
		for(int i = 0 ; i < dirs.size(); i++){
			String jpath = dirs.get(i).get("path").toString();
			jpath = jpath.substring(jpath.indexOf("/")+1, jpath.lastIndexOf("."));

			baseMap.put("Method", "GET");
			baseMap.put("basePath", "http://nchr.release.microfastup.com");
			baseMap.put("path", "/api_doc/"+jpath+".json");
			
			map.put("base", baseMap);

			cookieMap.put("UM_distinctid", "15b71f24cc2101-020ee61b4f9c14-8373f6a-100200-15b71f24cc312d");
			cookieMap.put("PHPSESSID", "67cth9jsol7k74kddrccld3pt0");
			
			map.put("cookies", cookieMap);
			http = new HttpMethods("http://nchr.release.microfastup.com");
			response = http.post(map);
			json = JsonPath.with(http.getBody(response));
			
			String basePath = json.getString("basePath");
			String dir = json.getString("resourcePath");
			List<Map<String, Object>> apis = new ArrayList<>();
			apis = json.getList("apis");
	
			for(int j = 0 ; j < apis.size() ; j++){
				Map<String,Object> api = apis.get(j);
				String fileName = json.getString("apis["+j+"].operations[0].nickname");
				String path = api.get("path").toString();
				String description = api.get("description").toString();
				String method = json.getString("apis["+j+"].operations[0].method");
				List<Map<String,Object>> params = new ArrayList<>();
				params = json.getList("apis["+j+"].operations[0].parameters");
				String[] paramArray = new String[params.size()];
				
				for(int k = 0; k <params.size() ; k++){
					Map<String,Object> param = params.get(k);
					String paramStr = param.get("name").toString();
					paramArray[k] = paramStr;
				}
				Map<String, String> base = new ListHashMap<>();
				
				base.put("description", description);
				base.put("basePath", basePath);
				base.put("path", path);
				base.put("method", method);
				base.put("fileName", fileName);
				base.put("dir", dir);
				createExcel(base,paramArray);
			}
		}
	}
	
	public static void createExcel(Map<String,String> base,String[] params){
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		if(wb!=null){
			Sheet baseSheet = wb.createSheet("Base");
			Sheet paramSheet = wb.createSheet("Params");
			
			Row row = baseSheet.createRow(0);
			String[] baseTitle = {"Description","Method","BasePath","Path"};
			
			for(int i = 0 ; i < baseTitle.length; i++){
				String title = baseTitle[i];
				Cell cell = row.createCell(i, CellType.BLANK);
				cell.setCellValue(title);
				baseSheet.autoSizeColumn(i);
			}
			
			Row row1 = baseSheet.createRow(1);
			String[] baseValue = {base.get("description"),base.get("method"),base.get("basePath"),base.get("path")};
			
			for(int i = 0 ; i < baseValue.length; i++){
				String value = baseValue[i];
				Cell cell = row1.createCell(i, CellType.BLANK);
				cell.setCellValue(value);
				baseSheet.autoSizeColumn(i);
			}
			
//			String[] paramTitle = {"toke","id"};
			Row pRow = paramSheet.createRow(0);
			if(params!=null){
				for(int j = 0 ; j < params.length; j++){
					String title = params[j];
					Cell cell = pRow.createCell(j);
					cell.setCellValue(title);
					paramSheet.autoSizeColumn(j);
				}
			}
			
			try {
				File file = new File("C:\\Users\\sam\\Desktop\\apis\\"+base.get("dir")+"\\"+base.get("fileName")+".xlsx");
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				FileOutputStream outputStream = new FileOutputStream(file);
				wb.write(outputStream);
				outputStream.flush();
				outputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
