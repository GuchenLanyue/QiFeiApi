package com.qifei.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;

public class JsonUtils {
	public JsonUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public JsonPath jsonReader(String jsonFile){		
		File file = new File(jsonFile);
		if (file.exists()) {
			BufferedReader br = null;
			try {
				br=new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile),"UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return JsonPath.with(br);
			
		}else{
			Assert.fail("File not found:"+jsonFile);
			
			return null;
		}
	}	
	
	@Step
	public void equalsJson(String filePath, JsonPath responseJson){
		JsonPath expectedJson = jsonReader(filePath);
		List<JsonPath> expectedList = expectedJson.getList("expected");
		
		for(int i = 0; i < expectedList.size(); i++){
			Map<String, Object> map = expectedJson.get("expected["+i+"].values");
			String root = expectedJson.get("expected["+i+"].root");
			responseJson = responseJson.setRoot(root);
			for(Map.Entry<String,Object> mapEntry:map.entrySet()){
				String key = mapEntry.getKey();
				
				if (mapEntry.getKey().equals("root")) {
					continue;
				}

				String actual = null;
				String expected = null;
				if (responseJson.get(key)!=null)
					actual = responseJson.get(key).toString();
				
				if (mapEntry.getValue()!=null) 
					expected = mapEntry.getValue().toString();

				Assert.assertEquals(actual, expected,"文件\""+filePath+"\"["+key+"]:的预期值为："+expected+"，实际值为："+actual);
			}
		}
	}
	
	@Step
	public void equalsJson(Map<String, Object> params, String filePath, JsonPath responseJson){
		File f = new File(filePath);
		if (!f.exists()) {
			Assert.fail("没有找到文件："+filePath);
		}
		JsonPath expectedJson = jsonReader(filePath);
		List<JsonPath> expectedList = expectedJson.getList("expected");
		
		for(int i = 0; i < expectedList.size(); i++){
			Map<String, Object> map = expectedJson.get("expected["+i+"].values");
			String root = expectedJson.get("expected["+i+"].root");
			responseJson = responseJson.setRoot(root);
			for(Map.Entry<String,Object> mapEntry:map.entrySet()){
				String key = mapEntry.getKey();
				
				if (mapEntry.getKey().equals("root")) {
					continue;
				}

				String actual = null;
				String expected = null;
				if (responseJson.get(key)!=null)
					actual = responseJson.get(key).toString();
				
				if (params.containsKey(key)) {
					expected = params.get(key).toString();
				}else if (mapEntry.getValue()!=null) {
					expected = mapEntry.getValue().toString();
				}
					
				Assert.assertEquals(actual, expected,"文件\""+filePath+"\"["+key+"]:的预期值为："+expected+"，实际值为："+actual);
			}
		}
	}
	
	public boolean compareJSONArray(JSONArray arr1,JSONArray arr2){
		boolean isContinue = false;
		for(int i=0;i<arr2.length();i++){
			if(arr2.get(i) instanceof JSONObject){
				isContinue =  compareJSONObject(arr1.getJSONObject(i), arr2.getJSONObject(i));
				if (isContinue) {
					return isContinue;
				}
			}else if (arr2.get(i) instanceof JSONArray) {
				isContinue =  compareJSONArray(arr1.getJSONArray(i), arr2.getJSONArray(i));
				if (isContinue) {
					return isContinue;
				}
			}else{
				String str = arr2.get(i).toString();
				if(str.contains("?normal{")){
					str = str.substring(str.indexOf("?")+3, str.indexOf("}",str.indexOf("?")+3));
					isContinue = !arr1.get(i).toString().equals(str);
					if (isContinue) {
						return isContinue;
					}
				}
				Assert.assertEquals(arr1.get(i).toString(), str);
			}
		}
		
		return isContinue;
	}
	
	public boolean compareJSONObject(JSONObject response,JSONObject expections){
		boolean isContinue = false;
		for(String key:expections.keySet()){
			
			if(expections.get(key) instanceof JSONObject){
				if(response.get(key).toString().equals("{}")){
					Assert.assertEquals(response.get(key).toString(), expections.get(key).toString());
				}else{
					JSONObject obj3 = response.getJSONObject(key);
					JSONObject obj4 = expections.getJSONObject(key);
					isContinue = compareJSONObject(obj3, obj4);
				}
				
				if (isContinue) {
					return isContinue;
				}
			}else if (expections.get(key) instanceof JSONArray) {
				JSONArray tempArray = expections.getJSONArray(key);
				if(tempArray.length()>0){
					JSONArray arr1 = response.getJSONArray(key);
					JSONArray arr2 = expections.getJSONArray(key);
					isContinue = compareJSONArray(arr1, arr2);
				}
				if (isContinue) {
					return isContinue;
				}
			}else {
				String str1 = response.get(key).toString();
				String str2 = expections.get(key).toString();
				if(str2.contains("?normal{")){
					int startIndex = str2.indexOf("?normal{");
					int beginIndex = str2.indexOf("{",startIndex);
					int endIndex = str2.indexOf("}",beginIndex);
					str2 = str2.substring(beginIndex+1,endIndex);
					isContinue = !str1.equals(str2);
					if (isContinue) {
						return isContinue;
					}
				}else if(str2.startsWith("?item[0]")){
					int startIndex = str2.indexOf("?item[0]");
					int beginIndex = str2.indexOf("]",startIndex);
					int index0 = str2.indexOf("[",startIndex);
					int index1 = str2.indexOf("]",startIndex);
					
					String indexStr = str2.substring(index0+1,index1);
					str2 = str2.substring(beginIndex+1,str2.length());
					JSONArray array1 = new JSONArray(str1);

					int index = Integer.parseInt(indexStr);
					str1 = array1.get(index).toString();
				}
				Assert.assertEquals(str1,str2,key);
			}
		}
		
		return isContinue;
	}
		
	@Step
	public void equalsJson(Map<String, Object>expected,JsonPath response){
		for(String key:expected.keySet()){
			Assert.assertEquals(response.get(key), expected.get(key),key);
		}
	}
	
	public static void main(String[] args) {
		String file1 = "C:/Users/sam/Desktop/new1.txt";
		String file2 = "C:/Users/sam/Desktop/Approval.xlsx";
		TxtData txt = new TxtData();
		JSONObject obj1 = new JSONObject(txt.readTxtFile(file1));
		ExcelReader excel = new ExcelReader();
		Map<String, Object> map = new HashMap<>();
		map = excel.mapFromSheet(file2, "Expectations", "Approval_1");
		map.remove("CaseID");
		JSONObject obj2 = new JSONObject(map);
		JsonUtils jsonUtils = new JsonUtils();
		jsonUtils.compareJSONObject(obj1, obj2);
	}
}
