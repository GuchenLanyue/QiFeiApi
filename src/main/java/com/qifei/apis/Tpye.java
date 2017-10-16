package com.qifei.apis;

import java.util.HashMap;
import java.util.Map;

import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class Tpye {
	public Tpye() {
		// TODO Auto-generated constructor stub
	}
	
	public Tpye(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	
	@Step("allTypes() 获取审批类型列表")
	public String allTypes(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/automation/v1/approval/alltypes");
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
		String body =  http.getBody(response);
		
		return body;
	}
	
	@Step("modifyType() 修改审批类型及模板信息")
	public String modifyType(Map<String, Object> paramMap){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/automation/v1/approval/types/{type_id}/op/all");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "PUT");

		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("type_id", paramMap.get("type_id"));
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", paramMap);
		map.put("pathParams", pathParamMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body =  http.getBody(response);
		
		return body;
	}
}
