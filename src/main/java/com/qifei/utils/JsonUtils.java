package com.qifei.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
			Assert.fail("文件"+jsonFile+"不存在！");
			
			return null;
		}
	}
	
	public List<Map<String, Object>> getList(String jsonStr){
		JSONArray jsonArr = new JSONArray(jsonStr);
		List<Map<String, Object>> list = new ArrayList<>();
//		System.out.println(jsonStr);
		for(Object json:jsonArr){
			list.add(getMap(json.toString()));
		}
		
		return list;
	}
	
	public Map<String, Object> getMap(String jsonStr){
		JSONObject jsonObj = null;
		
		try {
			jsonObj = new JSONObject(jsonStr);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(jsonStr);
			e.printStackTrace();
		}
		Map<String, Object> map = new HashMap<>();
		for(Object key:jsonObj.keySet()){
			Object value = jsonObj.get(key.toString());
			
			if(value instanceof JSONArray){
				String str = value.toString();
				if(str.startsWith("{")|str.startsWith("[")){
					if(str.contains(":")){
						map.put(key.toString(), getList(value.toString()));
					}else if(str.equals("[]")){
						map.put(key.toString(), new JSONArray());
					}else{
						map.put(key.toString(), str);
					}
					
				}else{
					map.put(key.toString(), value.toString());
				}
			}else{
				map.put(key.toString(), value);
			}
		}
		
		return map;
	}
	
	enum Type{
		String,Int,Double,Float
	}
	
	public Map<String, Object> formatMap(String jsonFile,Map<String, Object> map){
		JsonPath json = jsonReader(jsonFile);
		Map<String, Object> formatMap = new HashMap<>();
		formatMap = formatMap(map);
		for(String key:formatMap.keySet()){
			Type type = Enum.valueOf(Type.class, json.getString(key));
			switch (type) {
			case String:
				formatMap.put(key, formatMap.get(key).toString());
				break;
			case Int:
				formatMap.put(key, Integer.parseInt(formatMap.get(key).toString()));
				break;
			case Double:
				formatMap.put(key, Double.parseDouble(formatMap.get(key).toString()));
				break;
			case Float:
				formatMap.put(key, Float.parseFloat(formatMap.get(key).toString()));
				break;
			default:
				break;
			}
		}
		
		return formatMap;
	}
	
	public Map<String, Object> formatMap(Map<String, Object> map){
		JSONObject jsonObj = new JSONObject(map);
		Map<String, Object> formatMap = new HashMap<>();
		for(Object key:jsonObj.keySet()){
			Object value = jsonObj.get(key.toString());
			String valueStr = value.toString();
			if(valueStr.startsWith("[")){
				if(valueStr.contains(":")){
					formatMap.put(key.toString(), getList(value.toString()));
				}else if(valueStr.equals("[]")){
					formatMap.put(key.toString(), new JSONArray());
				}else{
					formatMap.put(key.toString(), valueStr);
				}
			}else if(valueStr.startsWith("{")){
				formatMap.put(key.toString(), getMap(value.toString()));
			}else if(value instanceof Double){

				String str = valueStr.substring(valueStr.indexOf(".")+1, valueStr.length());

				if(Integer.parseInt(str)==0){
					int intValue = ((Double) value).intValue();
					formatMap.put(key.toString(), intValue);
				}else{
					formatMap.put(key.toString(), value);
				}
			}else{
				formatMap.put(key.toString(), value);
			}
		}
		
		return formatMap;
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
	
	@Step
	public void equalsJson(Map<String, Object>expected,JsonPath response){
		for(String key:expected.keySet()){
			Assert.assertEquals(response.get(key), expected.get(key),key);
		}
	}
}
