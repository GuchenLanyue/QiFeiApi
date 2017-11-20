package com.qifei.apis;

import java.util.HashMap;
import java.util.Map;

import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import junit.framework.Assert;

public class Approval {
	public Approval() {
		// TODO Auto-generated constructor stub
	}
	
	public Approval(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = "";
	
	public String cancel(String process_id){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/instances/{process_id}/op/cancel");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("process_id", process_id);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
	
	public String approval(String instance_id){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/instances/{instance_id}/op/approve");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("instance_id", instance_id);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
	
	public String getInstances(String uuid){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/instances?approvalRole=submitter&limit=1&offset=0");
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
		
		JsonPath json = JsonPath.with(body);
		String form_record_id = json.get("form_record_id").toString();
		long time = 0;
		do{
			if(form_record_id.equals(uuid)){
				break;
			}else{
				response = http.request(map);
				json = JsonPath.with(body);
				form_record_id = json.get("form_record_id").toString();
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(time < 15000);
		Assert.assertEquals("not found approval:"+uuid+".", uuid, form_record_id);
		
		return json.get("items[0].uuid").toString();
	}
	
	public String gtInstanceInfo(String uuid){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/instances/{instance_id}");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("instance_id", uuid);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
}
