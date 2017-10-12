package com.qifei.apis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qifei.utils.JsonUtils;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

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
	public Map<String,Object> formatParams(Map<String,Object> params){
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = params;
		JsonUtils jsonUtil = new JsonUtils();
		String apply_scope = paramMap.get("apply_scope").toString();
		List<Map<String, Object>> list = jsonUtil.getList(apply_scope);
		paramMap.put("apply_scope", list);
		
		double longitude = Double.parseDouble(paramMap.get("longitude").toString());
		double latitude = Double.parseDouble(paramMap.get("latitude").toString());
		double rangeF = Double.parseDouble(paramMap.get("range").toString());
		int range = (int) rangeF;
		paramMap.put("longitude", longitude);
		paramMap.put("latitude", latitude);
		paramMap.put("range", range);
		
		return paramMap;
	}
	
	@Step("addLocations() 新增打卡地点")
	public String addLocations(Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations");
		baseMap.put("contentType", ContentType.JSON);
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", params);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.post(map);
		
		return http.getBody(response);
	}
	
	@Step("getLocations() 获取打卡地点列表")
	public List<Object> getLocations(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations");
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
		baseMap.put("Path", "/attendance/v1/locations/" + uuid);
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
		baseMap.put("Path", "/attendance/v1/locations/"+uuid);
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
//		Map<String, Object> paramMap = new HashMap<>();
//
//		paramMap.put("longitude", 116.345369f);
//		paramMap.put("latitude", 40.056871f);
//		paramMap.put("range", 300);
//		paramMap = attendance.formatParams(paramMap);
//		Object[] apply_scope = new Object[1];
//		Map<String, String> scope = new HashMap<>();
//		scope.put("type", "all");
//		apply_scope[0] = scope;
//		paramMap.put("apply_scope", apply_scope);
//		attendance.addLocations(paramMap);
		for(Object uuid:attendance.getLocations()){
			attendance.deleteLocations(uuid.toString());
		}		
//		System.out.println(attendance.getLocations().size());
	}
}
