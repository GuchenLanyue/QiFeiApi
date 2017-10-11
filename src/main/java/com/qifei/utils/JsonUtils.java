package com.qifei.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;

public class JsonUtils {
	
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
}
