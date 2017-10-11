package com.qifei.apis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qifei.utils.Headers;
import com.qifei.utils.HttpMethods;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Attendance {
	public Attendance() {
		// TODO Auto-generated constructor stub
	}
	
	public Attendance(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	
	@Step("setParams() 设置打卡地点参数")
	public Map<String,Object> setParams(Map<String,Object> params){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = params;
		if(!paramMap.containsKey("name")){
			paramMap.put("name", "兆维小区");
		}
		
		if(!paramMap.containsKey("province")){
			paramMap.put("province", "北京市");
		}
		
		if(!paramMap.containsKey("city")){
			paramMap.put("city", "北京市");
		}
		
		if(!paramMap.containsKey("address")){
			paramMap.put("address", "北京市朝阳区酒仙桥街道兆维小区");
		}
		
		if(!paramMap.containsKey("longitude")){
			paramMap.put("longitude", 116.503805f);
		}
		
		if(!paramMap.containsKey("latitude")){
			paramMap.put("latitude", 39.960939f);
		}
		
		if(!paramMap.containsKey("range")){
			paramMap.put("range", 300);
		}
		
		if(!paramMap.containsKey("apply_scope")){
			Object[] apply_scope = new Object[1];
			Map<String, String> scope = new HashMap<>();
			scope.put("type", "all");
			apply_scope[0] = scope;
			
			paramMap.put("apply_scope", apply_scope);
		}
		
		return paramMap;
	}
	
	@Step("addLocations() 新增打卡地点")
	public String addLocations(Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("path", "/attendance/v1/locations");
		baseMap.put("contentType", ContentType.JSON);
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", params);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.post(map);
		return http.getBody(response);
	}
	
	@Step("getLocations() 获取打卡地点列表")
	public List<Object> getLocations(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("path", "/attendance/v1/locations");
		baseMap.put("contentType", ContentType.JSON);
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.get(map);
		String body = http.getBody(response);
		JsonPath json = JsonPath.with(body);
		return json.getList("items.uuid");
	}
	
	@Step("modifyLocations() 更新打卡地点")
	public String modifyLocations(String uuid,Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("path", "/attendance/v1/locations/" + uuid);
		baseMap.put("contentType", ContentType.JSON);
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", params);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.put(map);
		return http.getBody(response);
	}
	
	@Step("deleteLocations() 删除打卡地点 ")
	public void deleteLocations(String uuid){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("path", "/attendance/v1/locations/"+uuid);
		baseMap.put("contentType", ContentType.JSON);
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		http.delete(map);
	}
	
	public static void main(String[] args) {
		Attendance attendance = new Attendance("http://console.t.upvi.com/bapi");
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("longitude", 116.345369f);
		paramMap.put("latitude", 40.056871f);
		paramMap.put("range", 300);
		paramMap = attendance.setParams(paramMap);
		Object[] apply_scope = new Object[1];
		Map<String, String> scope = new HashMap<>();
		scope.put("type", "all");
		apply_scope[0] = scope;
		paramMap.put("apply_scope", apply_scope);
		attendance.addLocations(paramMap);
//		for(Object uuid:attendance.getLocations()){
//			attendance.deleteLocations(uuid.toString());
//		}		
//		System.out.println(attendance.getLocations().size());
	}
}
