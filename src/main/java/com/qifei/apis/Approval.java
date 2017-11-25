package com.qifei.apis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.qifei.utils.DateUtils;
import com.qifei.utils.ExcelReader;
import com.qifei.utils.TxtData;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Step;
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
	@Step("getAllTypes() 获取所有审批类型")
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
	@Step("setApprovalType() 设置审批流程")
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
	@Step("setApprovalType() 设置审批流程")
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
	 * @description 审批统计*/
	@Step("approval_summary() 审批统计")
	public String approval_summary(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/statistic/summary?year={year}&month={month}");
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
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return http.getBody(response);
	}
	
	/**
	 * @description 审批统计*/
	@Step("approval_summary() 审批统计")
	public Response approval_summary(String year, String month){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/statistic/summary?year="+year+"&month="+month);
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
	 * @description 搜索审批明细*/
	@Step("search_approval() 搜索审批明细")
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
	@Step("cancel() 撤回审批")
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
	@Step("approval() 审批通过")
	public String approval(String uuid){
		String instance_id = getInstances(uuid);
		String info = getInstanceInfo(instance_id);
		String task_id = JsonPath.with(info).getString("tasks[0].uuid");
		
		String body = approval(instance_id, task_id);
		
		return body;
	}
	
	/**
	 * @description 审批通过 */
	@Step("approval() 审批通过")
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
	@Step("getInstances() 获取我发起的审批")
	public String getInstances(String uuid){
		
		String body = getInstances(10);
		JsonPath json = JsonPath.with(body);
		List<String> form_record_ids = json.getList("items.form_record_id");
		
		long time = 0;
		while (!form_record_ids.contains(uuid)||time < 15000) {
			try {
				Thread.sleep(5000);
				time += 5000;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			body = getInstances(10);
			json = JsonPath.with(body);
			form_record_ids = json.getList("items.form_record_id");
		}
		
		TxtData txtData = new TxtData();
		String filename = System.getProperty("user.dir")+File.separator+"sources"+File.separator+"temp"+File.separator+"GetInstances.txt";
		txtData.writerText(filename, body);
		
		Assert.assertTrue("not found approval:\""+uuid+"\"",form_record_ids.contains(uuid));
		int index = form_record_ids.indexOf(uuid);
		
		return json.get("items["+index+"].uuid").toString();
	}
	
	/**
	 * @description 获取审批列表 */
	@Step("getInstances() 获取我发起的审批")
	public String getInstances(int limit){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/automation/v1/approval/instances?approvalRole=submitter&limit="+limit+"&offset=0");
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
	
	/**
	 * @description 获取审批详情*/
	@Step("getInstanceInfo() 获取审批详情")
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
	@Step("offboard() 辞职")
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
	
	public static void main(String[] args) {
		Approval approval = new Approval("http://console.t.upvi.com/bapi");
		String body = approval.getInstances(10000);
		JsonPath instances = JsonPath.with(body);
		List<String> items = instances.getList("items");

		for(int i=0;i<items.size();i++){
			String status = instances.getString("items["+i+"].status");
			if(status.equals("approving")){
				approval.cancel(instances.getString("items["+i+"].uuid"));
			}
		}
//		approval.approval_summary();
	}
}
