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
	
	public List<Object> getList(String jsonStr){
		JSONArray jsonArray = new JSONArray(jsonStr);
		List<Object> list = new ArrayList<>();
		
		for(int i=0;i<jsonArray.length();i++){
			if(jsonArray.get(i) instanceof JSONObject|jsonArray.get(i).toString().startsWith("{")){
				list.add(getMap(jsonArray.get(i).toString()));
			}else if(jsonArray.get(i) instanceof JSONArray|jsonArray.get(i).toString().startsWith("[")){
				list.add(getList(jsonArray.get(i).toString()));
			}else if(jsonArray.get(i) instanceof Double){
				String str = jsonArray.getString(i);
				str = str.substring(str.indexOf(".")+1,str.length());
				if(Integer.parseInt(str)==0){
					list.add(Integer.parseInt(jsonArray.get(i).toString()));
				}else{
					list.add(Double.parseDouble(jsonArray.get(i).toString()));
				}
			}else if(jsonArray.get(i) instanceof Boolean){
				list.add(Boolean.parseBoolean(jsonArray.get(i).toString()));
			}else{
				String valueStr = jsonArray.get(i).toString();
				if(valueStr.contains("?${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/sources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String str = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(str);
					valueStr = "?${"+jsonPath.getString(paramPath)+"}";
				}else if(valueStr.contains("${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/sources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String str = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(str);
					valueStr = jsonPath.getString(paramPath);
				}else if(valueStr.contains("?{")){
					valueStr = valueStr.replaceAll("?{", "{");
				}
				list.add(valueStr);
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
			if(value instanceof JSONArray|valueStr.startsWith("[")){
				map.put(key.toString(), getList(valueStr));
			}else if(value instanceof JSONObject|valueStr.startsWith("{")){
				map.put(key.toString(), getMap(valueStr));
			}else if(value instanceof Double){
				String str = valueStr;
				str = str.substring(str.indexOf(".")+1, str.length());
				if(Integer.parseInt(str)==0){
					map.put(key.toString(), Integer.parseInt(valueStr));
				}else{
					map.put(key.toString(), Double.parseDouble(valueStr));
				}
			}else{
				if(valueStr.startsWith("s{")){
					valueStr = valueStr.substring(1, valueStr.length());
					JSONObject obj = new JSONObject(getMap(valueStr));
					valueStr = obj.toString();
				}else if(valueStr.contains("?${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/sources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String str = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(str);
					valueStr = "?${"+jsonPath.getString(paramPath)+"}";
				}else if(valueStr.contains("${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/sources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String str = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(str);
					valueStr = jsonPath.getString(paramPath);
				}
				map.put(key.toString(), valueStr);
			}
		}
		
		return map;
	}
	
	enum Type{
		String,Int,Double,Float,Boolean
	}
	
	public List<Object> formatList(JSONArray baseArr,JSONArray array){
		List<Object> list = new ArrayList<>();
		for(int i=0;i<baseArr.length();i++){
			System.out.println(baseArr.get(i).toString());
			System.out.println(array.get(i).toString());
			if(baseArr.get(i) instanceof JSONObject){
				
				for(int j=0;j<array.length();i++){
					System.out.println(baseArr.get(j).toString());
					System.out.println(array.get(j).toString());
					if(array.get(j) instanceof JSONObject){
						if(array.get(j).toString().equals("{}")){
							list.add(array.get(j).toString());
						}else if(baseArr.getJSONObject(i).length()==array.getJSONObject(j).length()){
							list.add(formatMap(baseArr.getJSONObject(i), array.getJSONObject(j).toMap()));
						}else{
							Type type = Enum.valueOf(Type.class, baseArr.get(j).toString());
							switch (type) {
							case Int:
								list.add(Integer.parseInt(array.get(i).toString()));
								break;
							case Double:
								list.add(Double.parseDouble(array.get(i).toString()));
								break;
							case String:
								list.add(array.get(i).toString());
								break;
							case Boolean:
								list.add(Boolean.parseBoolean(array.getBigInteger(i).toString()));
								break;
							default:
								break;
							}
						}
					}else{
						list.add(array.get(j));
					}
				}
			}else if(baseArr.get(i) instanceof JSONArray){
				
				for(int k=0;k<array.length();k++){
					System.out.println(baseArr.get(k).toString());
					System.out.println(array.get(k).toString());
					if(array.get(k) instanceof JSONArray){
						list.add(formatList(baseArr.getJSONArray(i), array.getJSONArray(k)));
					}else{
						list.add(array.get(k));
					}
				}
			}else{
				Type type = Enum.valueOf(Type.class, baseArr.get(i).toString());
				switch (type) {
				case Int:
					list.add(Integer.parseInt(array.get(i).toString()));
					break;
				case Double:
					list.add(Double.parseDouble(array.get(i).toString()));
					break;
				case String:
					list.add(array.get(i).toString());
					break;
				case Boolean:
					list.add(Boolean.parseBoolean(array.getBigInteger(i).toString()));
					break;
				default:
					break;
				}
			}
		}
		
		return list;
	}
	
	public Map<String, Object> formatMap(JSONObject baseObj,Map<String, Object> map){
		Map<String, Object> formatMap = new HashMap<>();
		formatMap = formatMap(map);
		
		JSONObject obj = new JSONObject(formatMap);
		for(String key:baseObj.keySet()){
			System.out.println(baseObj.get(key).toString());
			System.out.println(obj.get(key).toString());
			if(obj.get(key) instanceof JSONObject){
				if(key.toString().equals("target_value")|key.toString().equals("form_key_field")){
					System.out.println(key.toString());
					System.out.println(obj.get(key).toString());
				}
				if(obj.get(key).toString().equals("{}")){
					formatMap.put(key.toString(), obj.get(key).toString());
				}else if(baseObj.getJSONObject(key).length()==obj.getJSONObject(key).length()){
					formatMap.put(key.toString(), formatMap(baseObj.getJSONObject(key),obj.getJSONObject(key).toMap()));
				}else{
					Type type = Enum.valueOf(Type.class, baseObj.get(key).toString());
					switch (type) {
					case Int:
						formatMap.put(key.toString(),Integer.parseInt(obj.get(key).toString()));
						break;
					case Double:
						formatMap.put(key.toString(),Double.parseDouble(obj.get(key).toString()));
						break;
					case String:
						formatMap.put(key.toString(), obj.get(key).toString());
						break;
					default:
						break;
					}
				}
			}else if(obj.get(key) instanceof JSONArray){
				formatMap.put(key.toString(), formatList(baseObj.getJSONArray(key), obj.getJSONArray(key)));
			}else{
				Type type = Enum.valueOf(Type.class, baseObj.get(key).toString());
				switch (type) {
				case Int:
					formatMap.put(key.toString(),Integer.parseInt(obj.get(key).toString()));
					break;
				case Double:
					formatMap.put(key.toString(),Double.parseDouble(obj.get(key).toString()));
					break;
				case String:
					formatMap.put(key.toString(), obj.get(key).toString());
					break;
				default:
					break;
				}
			}
		}
		
		return formatMap;
	}
	
	public Map<String, Object> formatMap(String jsonFile,Map<String, Object> map){
		JsonPath json = jsonReader(jsonFile);
		Map<String, Object> formatMap = new HashMap<>();
		formatMap = formatMap(map);
//		formatMap = map;
		
		for(String key:formatMap.keySet()){
			
			Type type = Enum.valueOf(Type.class, json.getString(key));
			String value = formatMap.get(key).toString();

			switch (type) {
			case String:
				formatMap.put(key, value);
				break;
			case Int:
				formatMap.put(key, Integer.parseInt(value));
				break;
			case Double:
				formatMap.put(key, Double.parseDouble(value));
				break;
			case Float:
				formatMap.put(key, Float.parseFloat(value));
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
			if(value instanceof JSONArray|valueStr.startsWith("[")){
				formatMap.put(key.toString(), getList(valueStr));
			}else if(value instanceof JSONObject|valueStr.startsWith("{")){
				formatMap.put(key.toString(), getMap(valueStr));
			}else if(value instanceof Double){
				valueStr = valueStr.substring(valueStr.indexOf(".")+1,valueStr.length());
				if(Integer.parseInt(valueStr)==0){
					String str = value.toString().substring(0, value.toString().indexOf("."));
					formatMap.put(key.toString(), Integer.parseInt(str));
				}else {
					formatMap.put(key.toString(), Double.parseDouble(value.toString()));
				}
			}else if(value instanceof Boolean){
				formatMap.put(key.toString(), Boolean.parseBoolean(valueStr));
			}else{
				if(valueStr.contains("?${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/ources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String jsonStr = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(jsonStr);
					valueStr = "?${"+jsonPath.getString(paramPath)+"}";
				}else if(valueStr.contains("${")){
					String fileName = valueStr.substring(valueStr.indexOf("{")+1, valueStr.indexOf("."));
					String paramPath = valueStr.substring(valueStr.indexOf(".")+1, valueStr.indexOf("}"));
					fileName = System.getProperty("user.dir")+"/sources/temp/"+fileName+".txt";
					TxtData txt = new TxtData();
					String jsonStr = txt.readTxtFile(fileName);
					JsonPath jsonPath = JsonPath.with(jsonStr);
					valueStr = valueStr.substring(0,valueStr.indexOf("$"))+jsonPath.getString(paramPath) + valueStr.substring(valueStr.indexOf("}")+1, valueStr.length());
				}else if(valueStr.startsWith("s{")){
					valueStr = valueStr.substring(1,valueStr.length());
					JSONObject obj = new JSONObject(getMap(valueStr));
					valueStr = obj.toString();
				}
				formatMap.put(key.toString(), valueStr);
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
				if(str.contains("?${")){
					str = str.substring(str.indexOf("{")+1, str.lastIndexOf("}"));
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
	
	public boolean compareJSONObject(JSONObject obj1,JSONObject obj2){
		boolean isContinue = false;
		for(String key:obj2.keySet()){
			if(obj2.get(key) instanceof JSONObject){
				if(obj1.get(key).toString().equals("{}")){
					Assert.assertEquals(obj1.get(key).toString(), obj2.get(key).toString());
				}else{
					JSONObject obj3 = obj1.getJSONObject(key);
					JSONObject obj4 = obj2.getJSONObject(key);
					isContinue = compareJSONObject(obj3, obj4);
				}
				
				if (isContinue) {
					return isContinue;
				}
			}else if (obj2.get(key) instanceof JSONArray) {
				JSONArray tempArray = obj2.getJSONArray(key);
				if(tempArray.length()>0){
					JSONArray arr1 = obj1.getJSONArray(key);
					JSONArray arr2 = obj2.getJSONArray(key);
					isContinue = compareJSONArray(arr1, arr2);
				}
				if (isContinue) {
					return isContinue;
				}
			}else {
				String str = obj2.get(key).toString();
				if(str.contains("?${")){
					str = str.substring(str.indexOf("{")+1, str.lastIndexOf("}"));
					isContinue = !obj1.get(key).toString().equals(str);
					if (isContinue) {
						return isContinue;
					}
				}
				Assert.assertEquals(obj1.get(key).toString(), str,key);
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
		JsonUtils jsonUtils = new JsonUtils();
		TxtData txt = new TxtData();
		String str1 = txt.readTxtFile("C:\\Users\\sam\\Desktop\\1.txt");
		String str2 = txt.readTxtFile("C:\\Users\\sam\\Desktop\\2.txt");
		
		JSONObject obj1 = new JSONObject(str1);
		JSONObject obj2 = new JSONObject(str2);
		
		System.out.println(jsonUtils.compareJSONObject(obj1, obj2));
	}
}
