package com.qifei.apis;

import java.util.HashMap;
import java.util.Map;

import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Adjust {

	public Adjust() {
		// TODO Auto-generated constructor stub
	}
	
	public Adjust(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = "";
	
	public String reshuffle_logs(String target_id,String target_type){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/reshuffle_logs?target_id="+target_id+"&target_type="+target_type);
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
	
	public static void main(String[] args) {
		Adjust adjust = new Adjust("http://console.t.upvi.com/bapi");
		String body = adjust.reshuffle_logs("b8a23753-0f75-11e7-9aa4-00163e007053","employee");
		
		JsonPath json = JsonPath.with(body);
		json.getList("items");
	}
}
