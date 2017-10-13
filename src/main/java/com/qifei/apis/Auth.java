package com.qifei.apis;

import java.util.HashMap;
import java.util.Map;

import com.qifei.utils.JsonUtils;
import com.qifei.utils.TxtData;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Auth {
	
	public Auth() {
		// TODO Auto-generated constructor stub
	}
	
	public Auth(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	private String file = System.getProperty("user.dir")+"\\sources\\config\\user.json";
	private String tokenFile = System.getProperty("user.dir")+"\\sources\\config\\access_token.txt";
	
	@Step("tokens() 登录-获取Token")
	public String tokens(){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Path", "/auth/v1/auth/tokens");
		baseMap.put("contentType", ContentType.JSON);
		baseMap.put("Method", "POST");

		//通过user.json文件读取登录信息
		JsonUtils jsonUtil = new JsonUtils();
		JsonPath user = jsonUtil.jsonReader(file);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = user.getMap("user");
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		JsonPath body = JsonPath.with(http.getBody(response));
		
		String authorization = "Bearer " + body.getString("access_token");
		
		TxtData txt = new TxtData();
		txt.writerText(tokenFile, authorization);
		
		return authorization;
	}
	
	public static void main(String[] args) {
		Auth auth = new Auth("http://console.t.upvi.com/bapi");
		System.out.println(auth.tokens());
	}
}
