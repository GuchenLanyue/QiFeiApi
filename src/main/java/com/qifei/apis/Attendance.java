package com.qifei.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.qifei.utils.ExcelReader;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Step;
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
		ExcelReader excel = new ExcelReader();
		String apply_scope = paramMap.get("apply_scope").toString();
		List<Object> list = excel.getList(apply_scope);
		
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
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", formatParams(params));
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return http.getBody(response);
	}
	
	@Step("addLocations() 新增打卡地点")
	public String addLocations(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", "兆维小区");
		paramMap.put("province", "北京市");
		paramMap.put("city", "");
		paramMap.put("address", "北京市朝阳区酒仙桥街道兆维小区");
		paramMap.put("longitude", 116.503805f);
		paramMap.put("latitude", 39.960939f);
		paramMap.put("range", 300);
		Object[] apply_scope = new Object[1];
		Map<String, String> scope = new HashMap<>();
		scope.put("type", "all");
		apply_scope[0] = scope;
		paramMap.put("apply_scope", apply_scope);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramMap);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return http.getBody(response);
	}
	
	@Step("modifyLocations() 更新打卡地点")
	public String modifyLocations(String uuid,Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations/" + uuid);
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "PUT");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", formatParams(params));
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		return http.getBody(response);
	}
	
	@Step("deleteLocations() 删除打卡地点 ")
	public void deleteLocations(String uuid){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations/"+uuid);
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "DELETE");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}

	@Step("deleteLocations() 删除打卡地点 ")
	public void deleteSchedules(String id){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/attendance/settings/"+id);
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "DELETE");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);

		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);

		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}

	@Step("deleteLocations() 删除打卡地点 ")
	public void deleteAllSchedules(){
		List<String> schedules = new ArrayList<>();
		schedules = getScheduleIDs();
		if(schedules!=null){
			for(String id:schedules){
				deleteSchedules(id);
			}
		}
	}

	

	@Step("getLocations() 获取打卡地点列表")
	public List<Map<String, Object>> getLocations(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		//处理response
		ExcelReader excel = new ExcelReader();
		JSONObject jsonObj = new JSONObject(body);
		JSONArray jsonArr = jsonObj.getJSONArray("items");
		List<Map<String,Object>> list = new ArrayList<>();
		List<Object> items = jsonArr.toList();
		for(int i=0;i<items.size();i++){
			Map<String,Object> item = excel.getMap(items.get(i).toString());
			items.add(item);
		}
		
		return list;
	}
	
	@Step("getLocations() 获取打卡地点列表")
	public List<String> getLocationIDs(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/locations");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		JsonPath json = JsonPath.with(body);
		
		return json.getList("items.uuid");
	}

	@Step("getSchedules() 获取所有班次列表")
	public List<String> getScheduleIDs(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path","/attendance/v1/attendance/settings");
	//	baseMap.put("basepath","http://console.t.upvi.com/bapi");
		baseMap.put("contentType","application/json");
		baseMap.put("Method","GET");

		String authorization = new Headers(basePath).getAuthorization();
		Map<String,Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);

		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);

		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		JsonPath json = JsonPath.with(body);
		
		return json.getList("items.id");
	}

	@Step("getLocation() 获取打卡地点信息")
	public Map<String, Object> getLocation(String uuid){
		List<Map<String, Object>> locations = getLocations();
		if (locations.size()==0) {
			return null;
		}
		
		for(Map<String, Object> map:locations){
			if (map.get("uuid").toString().equals(uuid)) {
				return map;
			}else{
				continue;
			}
		}
		
		return null;
	}
	
	@Step("getLocation() 随机获取一个打卡地点的id")
	public String getLocationID(){
		List<Map<String, Object>> locations = getLocations();
		int size = locations.size();
		String locationID = null;
		if(size==0){
			String body = addLocations();
			JsonPath json = JsonPath.with(body);
			locationID = json.getString("uuid");
		}else{
			Random random = new Random();
			int index = random.nextInt(locations.size());
			Map<String, Object> locationMap = new HashMap<>();
			locationMap = locations.get(index);
			locationID = locationMap.get("uuid").toString();
		}
		
		return locationID;
	}
	
	@Step("in() 内勤打卡")
	public void in(Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/attendances/in");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);
		
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}
	
	@Step("out() 外勤打卡")
	public void out(Map<String,Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/attendances/out");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);
		
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}

	
	@Step("daily_statistic()")
	public void daily_statistic(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/attendance/v1/attendances/daily-statistic");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}
	
	public static void main(String[] args) {
		Attendance attendance = new Attendance("http://console.t.upvi.com/bapi");
		//新增打卡地点
//		attendance.addLocations();
		//获取打卡地点列表
		List<String> locations = attendance.getLocationIDs();
//		int size = locations.size();
//		Random random = new Random();
//		int index = random.nextInt(size);
//		String uuid = locations.get(index).get("uuid").toString();
//		System.out.println(uuid);
//		//获取打卡地点详情
//		Map<String, Object> location = attendance.getLocation(uuid);
//		for(String key:location.keySet()){
//			System.out.println(key+":"+location.get(key));
//		}
		
		//删除打卡地点
		for(String uuid:locations){
			attendance.deleteLocations(uuid);
		}
//		attendance.addLocations();

		List<String> schedules = attendance.getScheduleIDs();
		for(String id:schedules){
			attendance.deleteSchedules(id);
		}
	}
}
