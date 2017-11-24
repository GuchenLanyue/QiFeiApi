package com.qifei.apis;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.mozilla.javascript.ast.NewExpression;
import org.testng.Assert;

import com.qifei.utils.DateUtils;
import com.qifei.utils.RandomValue;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Employee {
	public Employee() {
		// TODO Auto-generated constructor stub
	}

	public Employee(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}

	private String basePath = null;
	Map<String, Object> params = new HashMap<>();
	
	// 新增员工
	@SuppressWarnings("static-access")
	@Step("addEmployee() 新增成员")
	public String addEmployee(String organization_ID,String position_ID) {
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		// 设置参数
		RandomValue random = new RandomValue();
		params.put("name", random.getChineseName());
		params.put("mobile", random.getTel());
		Date date = new Date();
		params.put("employee_no", "ZZ_"+date.getTime());
		params.put("organization_ID", organization_ID);
		params.put("position_ID", position_ID);
		
		//设置入职日期
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date)+"T10:00:00+08:00";
		params.put("join_date", dateString);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);

		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);

		return body;
	}

	// 新增员工
	@Step("addEmployee() 新增成员")
	public String addEmployee() {
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		// 设置参数
//		Map<String, Object> params = new HashMap<>();
		RandomValue random = new RandomValue();
		params.put("name", random.getChineseName());
		params.put("mobile", random.getTel());
		Date date = new Date();
		params.put("employee_no", "ZZ_"+date.getTime());
		
		//设置入职日期
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date)+"T10:00:00+08:00";
		params.put("join_date", dateString);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);

		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);

		return body;
	}
		
	// 新增员工
	@SuppressWarnings("static-access")
	@Step("addEmployee() 新增成员")
	public String addEmployee(Map<String, Object> params) {
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		this.params = params;
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date)+"T10:00:00+08:00";
		this.params.put("join_date", dateString);
		this.params.put("employee_no", "ZZ_"+date.getTime());
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", this.params);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);

		return body;
	}

	//到岗
	@Step("formal() 到岗")
	public String join(String uuid){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords/{uuid}?form=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "PUT");

		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> paramMap = new HashMap<>();
		//设置请求参数
		paramMap.put("status", "probation");
		paramMap.put("uuid", uuid);
		
		//设置路径参数
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("uuid", uuid);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", paramMap);
		map.put("pathParams", pathParamMap);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);

		return body;
	}
	
	/**
	 * @description 转正*/
	@Step("formal() 转正")
	public String formal(Map<String,Object> paramMap){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee_probation");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");

		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		DateUtils dateUtils = new DateUtils();	
		Map<String,Object> params = paramMap;
		params.put("need_notify_employee", "n");
		params.put("effective_date", dateUtils.getToday()+"T11:10:20+08:00");
		params.put("notify_content", "");
		params.put("comment", "转正测试");
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		String body = http.getBody(response);

		return body;
	}
		
	//验证返回值
	@Step("checkResponse() 验证审批结果")
	public void checkResponse(String body){
		JSONObject obj = new JSONObject(body);
		for(String key:obj.keySet()){
			if(this.params.containsKey(key)){
				Assert.assertEquals(this.params.get(key).toString(), obj.get(key).toString());
			}
		}
	}
	
	/**
	 * @description 获取所有员工 */
	@Step("getEmployees() 获取所有员工")
	public Response getEmployees(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/formrecords?form=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");

		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		// 发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return response;
	}
	
	/**
	 * @description 获取发展轨迹*/
	@Step("reshuffle_logs() 获取发展轨迹")
	public Response reshuffle_logs(String employee_id){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", basePath);
		baseMap.put("Path", "/hr/v1/reshuffle_logs?target_id={employee_id}&target_type=employee");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");

		// 设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Object> pathParamMap = new HashMap<>();
		pathParamMap.put("employee_id", employee_id);
		
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
	 * 辞职所有指定状态的员工*/
	@Step("clean() 辞职所有指定状态的员工")
	public void clean(String statu){
		Response response = getEmployees();
		JsonPath json = JsonPath.with(response.getBody().asString());
		List<String> status = json.getList("items.status");
		Approval approval = new Approval(basePath);
		Map<String,Object> params = new HashMap<>();
				
		approval.setApprovalType("employee_offboard", "resign");
		
		for(int i=0; i<status.size(); i++){
			if(status.get(i).equals(statu)){
				String employeeID = json.getString("items["+i+"].uuid");
				String organization = json.getString("items["+i+"].organization");
				String organization_ID = json.getString("items["+i+"].organization_ID");
				String position = json.getString("items["+i+"].position");
				String position_ID = json.getString("items["+i+"].position_ID");
				if(organization==null){
					organization = "";
					organization_ID = "";
				}
				
				if(position==null){
					position = "";
					position_ID = "";
				}
				
				params.put("employee_ID", employeeID);
				params.put("organization", organization);
				params.put("organization_ID", organization_ID);
				params.put("position", position);
				params.put("position_ID", position_ID);
				params.put("employee", json.getString("items["+i+"].name"));
				//离职
				Response offboard = approval.offboard(params);
				JsonPath json1 = JsonPath.with(offboard.getBody().asString());
				if(json1.getString("uuid")==null){
					continue;
				}
				//获取我提交的审批列表
				String uuid = approval.getInstances(json1.getString("uuid"));
				//获取审批详情
				String body = approval.getInstanceInfo(uuid);
				JsonPath json2 = JsonPath.with(body);
				//审批通过
				String task_id = json2.getString("tasks[0].uuid");
				body = approval.approval(uuid,task_id);
				json2 = JsonPath.with(body);
				//验证审批状态
				Assert.assertEquals(json2.getString("status"), "approved");
				Response log = reshuffle_logs(employeeID);
				//获取发展轨迹
				JsonPath reshuffle_log = JsonPath.with(log.asString());
				List<String> record_ids = reshuffle_log.getList("items.related_form_record_id");
				long time = 0;
				while(time<15000||record_ids.size()==0||!record_ids.get(record_ids.size()-1).equals(json1.getString("uuid"))){
					try {
						Thread.sleep(5000);
						time += 5000;
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					log = reshuffle_logs(employeeID);
					reshuffle_log = JsonPath.with(log.asString());
					record_ids = reshuffle_log.getList("items.related_form_record_id");
				}
				
				//验证最后一项发展轨迹是否为离职
				Assert.assertEquals(record_ids.get(record_ids.size()-1), json1.getString("uuid"));
			}
		}
	}

	public static void main(String[] args) {
		Employee employee = new Employee("http://console.t.upvi.com/bapi");
		employee.clean("probation");
//		employee.addMember();
	}
}
