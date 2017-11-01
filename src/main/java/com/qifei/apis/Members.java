package com.qifei.apis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import com.qifei.utils.RandomValue;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.restassured.response.Response;
import junit.framework.Assert;

public class Members {
	public Members() {
		// TODO Auto-generated constructor stub
	}

	public Members(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}

	private String basePath = null;
	Map<String, Object> params = new HashMap<>();
	
	// 新增员工
	@SuppressWarnings("static-access")
	public String addMember() {
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
		params.put("organization_ID", "696434ed-b965-11e7-b707-5254001aba5d");
		params.put("position_ID", "69b25559-b965-11e7-b707-5254001aba5d");
		params.put("join_date", "");
		params.put("field_6", "21");
		params.put("field_8", "510723199007195415");
		
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
	public String addMember(Map<String, Object> params) {
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
//		this.params.put("organization_ID", "696434ed-b965-11e7-b707-5254001aba5d");
//		this.params.put("position_ID", "69b25559-b965-11e7-b707-5254001aba5d");
		RandomValue random = new RandomValue();
		this.params.put("mobile", random.getTel());
		this.params.put("name", random.getChineseName());
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
	
	//验证返回值
	public void checkResponse(String body){
		JSONObject obj = new JSONObject(body);
		for(String key:obj.keySet()){
			if(this.params.containsKey(key)){
				Assert.assertEquals(this.params.get(key).toString(), obj.get(key).toString());
			}
		}
	}
	public static void main(String[] args) {
		Members members = new Members("http://console.t.upvi.com/bapi");
		members.addMember();
	}
}
