package com.qifei.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.qifei.utils.DateUtils;
import com.qifei.utils.ExcelReader;
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
	
	/**
	 * @description 获取所有审批类型
	 * */
	public String getAllTypes(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/alltypes?all=true");
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
		
		return http.getBody(response);
	}
	
	/**
	 * @description 设置审批流程*/
	public Response setApprovalType(String approval,String sub_category){
		String types = getAllTypes();
		JsonPath type = JsonPath.with(types);
		List<String> form_names = type.getList("items.form_name");
		String typeID = "";
		String processID = "";
		for(int i=0; i<form_names.size(); i++){
			if(form_names.get(i).equals(approval)){
				typeID = type.getString("items["+i+"].uuid");
				processID = type.getString("items["+i+"].process_list[0].uuid");
				break;
			}
		}
		
		Response response = setApprovalType(typeID, processID, sub_category);
		
		return response;
	}
	
	/**
	 * @description 设置审批流程*/
	public Response setApprovalType(String typeID,String processID,String sub_category){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/types/{type_id}/op/all");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "PUT");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("type_id", typeID);
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("notice_type", "approval_start");
		paramMap.put("duplicate_type", "duplicate");
		paramMap.put("sub_category", sub_category);
		
		Auth auth = new Auth(basePath);
		auth.tokens();

		JsonPath user = JsonPath.with(auth.getResponse().getBody().asString());
		
		Map<String, Object> approvalMap = new HashMap<>();
		approvalMap.put("uuid", "");
		approvalMap.put("type", "employee");
		approvalMap.put("value", user.get("employee_id"));
		
		List<Object> approvers = new ArrayList<>();
		approvers.add(approvalMap);
		
		Map<String, Object> processMap = new HashMap<>();
		processMap.put("uuid", processID);
		processMap.put("approvers", approvers);
		processMap.put("target_type", "");
		processMap.put("target_value", "\"{}\"");
		
		List<Object> process_list = new ArrayList<>();
		process_list.add(processMap);
		
		paramMap.put("process_list", process_list);
		
		List<Object> cc_list = new ArrayList<>();
		paramMap.put("cc_list", cc_list);
		
		paramMap.put("notice_type", "");
		paramMap.put("notice_type", "");
		JSONObject obj = new JSONObject(paramMap);
		ExcelReader excel = new ExcelReader();
		paramMap = excel.getMap(obj.toString());
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		map.put("params", paramMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return response;
	}
	
	/**
	 * @description 搜索审批明细*/
	public Response search_approval(String title){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/statistic/details?year={year}&month={month}&limit={limit}&offset={offset}&title={title}");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");

		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		DateUtils dateUtils = new DateUtils();
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("year", dateUtils.getYear());
		pathParamMap.put("month", dateUtils.getThisMonth());
		pathParamMap.put("limit", 10);
		pathParamMap.put("offset", 0);
		pathParamMap.put("title", title);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return response;
	}
	
	/**
	 * @description 撤回审批
	 * */
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
	
	/**
	 * @description 审批通过 */
	public String approval(String instance_id,String task_id){
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
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("task_id", task_id);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		map.put("params", paramMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
	
	/**
	 * @description 获取审批列表 */
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
		String form_record_id = json.get("items[0].form_record_id").toString();
		long time = 0;
		do{
			if(form_record_id.equals(uuid)){
				break;
			}else{
				response = http.request(map);
				json = JsonPath.with(http.getBody(response));
				form_record_id = json.get("items[0].form_record_id").toString();
			}
			
			try {
				Thread.sleep(5000);
				time += 5000;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(time < 15000);
		Assert.assertEquals("not found approval:"+uuid+".", uuid, form_record_id);
		
		return json.get("items[0].uuid").toString();
	}
	
	/**
	 * @description 获取审批详情*/
	public String getInstanceInfo(String uuid){
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
		map.put("pathParams", pathParamMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);
		
		return body;
	}
	
	/**
	 * @description 辞职
	 * */
	public Response offboard(Map<String, Object> params){
		Map<String, Object> paramMap = params;
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee_offboard");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		DateUtils dateUtils = new DateUtils();
		paramMap.put("offboard_date", dateUtils.getToday()+"T18:27:36+08:00");
		paramMap.put("comment", "辞职");
		paramMap.put("type", "辞职");
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", paramMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return response;
	}
}
