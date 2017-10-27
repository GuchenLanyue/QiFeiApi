package com.qifei.utils.http;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.response.Response;

public class HttpMethods {
	private Map<String,Object> headerMap = new HashMap<>();
	private Map<String,Object> cookieMap = new HashMap<>();
	private Map<String,Object> queryMap = new HashMap<>();
	private Map<String,Object> paramMap = new HashMap<>();
	private Map<String,Object> pathParamMap = new HashMap<>();
	private String requestURL = null;
	private String basePath = null;
	
	public HttpMethods() {
		// TODO Auto-generated constructor stub
	}
	
	public HttpMethods(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	public Response request(Map<String, Map<String,Object>> map){
		String method = map.get("base").get("Method").toString();
		String base = null;
		if(map.get("base").containsKey("basePath")){
			base = map.get("base").get("basePath").toString();
		}
		
		if(basePath!=null&&base==null){
			requestURL = basePath + map.get("base").get("Path").toString();
		}else{
			requestURL = map.get("base").get("BasePath").toString()
					+ map.get("base").get("Path").toString();
		}
		
		if(map.containsKey("headers")){
			headerMap = map.get("headers");
		}
		if(map.containsKey("cookies")){
			cookieMap = map.get("cookies");
		}
		if(map.containsKey("querys")){
			queryMap = map.get("querys");
		}
		if(map.containsKey("pathParams")){
			pathParamMap = map.get("pathParams");
		}
		
		if(method.equals("POST")){
			if (map.containsKey("params")) {
				paramMap = map.get("params");
				return post(map);
			}else{
				return postNobody(map);
			}
		}else if (method.equals("GET")) {
			if (map.containsKey("params")) {
				paramMap = map.get("params");
				return get(map);
			}else{
				return getNobody(map);
			}
		}else if (method.equals("DELETE")) {
			return delete(map);
		}else if (method.equals("PUT")) {
			if (map.containsKey("params")) {
				paramMap = map.get("params");
				return put(map);
			}else{
				return putNobody(map);
			}
		}else{
			Assert.fail("目前不支持"+method+"请求！");
			return null;
		}
	}
	
	@Step("post() 发起请求")
	public Response post(Map<String, Map<String,Object>> map){
		Response response = given()
				.log().method()
				.proxy("127.0.0.1", 8888)
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
				.body(paramMap)
			.when()
				.post(requestURL)
			.then()
//				.log().body()
//				.statusCode(200)
				.log().status()
			.extract()
				.response();

		responseLog(response);
		
		return response;
	}
	
	@Step("get() 发起请求")
	public Response get(Map<String, Map<String,Object>> map){
		Response response = given()
				.proxy("127.0.0.1", 8888)
				.log().method()
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
				.body(paramMap)
			.when()
				.get(requestURL)
			.then()
//				.log().body()
				.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		
		return response;
	}
	
	@Step("put() 发起请求")
	public Response put(Map<String, Map<String,Object>> map){
		Response response = given()
				.proxy("127.0.0.1", 8888)
				.log().method()
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
				.body(paramMap)
			.when()
				.put(requestURL)
			.then()
//				.log().body()
				.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		
		return response;
	}
	
	@Step("delete() 发起请求")
	public Response delete(Map<String, Map<String,Object>> map){
		Response response = given()
				.proxy("127.0.0.1", 8888)
				.log().method()
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
			.when()
				.delete(requestURL)
			.then()
//				.log().body()
				.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		
		return response;
	}
	
	@Step("post() 发起请求")
	public Response postNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				.log().method()
				.proxy("127.0.0.1", 8888)
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
//				.body(paramMap)
			.when()
				.post(requestURL)
			.then()
//				.log().body()
//				.statusCode(200)
				.log().status()
			.extract()
				.response();

		responseLog(response);
		
		return response;
	}
	
	@Step("get() 发起请求")
	public Response getNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				.proxy("127.0.0.1", 8888)
				.log().method()
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
//				.body(paramMap)
			.when()
				.get(requestURL)
			.then()
//				.log().body()
				.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		
		return response;
	}
	
	@Step("put() 发起请求")
	public Response putNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				.proxy("127.0.0.1", 8888)
				.log().method()
//				.log().all()
				.log().uri()
//				.log().params()
				.headers(headerMap)
				.cookies(cookieMap)
				.config(RestAssured.config()
						  .encoderConfig(EncoderConfig.encoderConfig()
								    .defaultContentCharset("UTF-8")
								    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.contentType(map.get("base").get("contentType").toString())
				.pathParams(pathParamMap)
				.queryParams(queryMap)
//				.body(paramMap)
			.when()
				.put(requestURL)
			.then()
//				.log().body()
				.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		
		return response;
	}
	
	@Description("获取响应数据")
	public String getBody(Response response){
		String body = response.asString();
		return body.substring(body.indexOf("{"), body.lastIndexOf("}")+1);
	}
	
	@Description("将请求数据添加到测试报告中")
	public void requestLog(){
		String requestBody = " ";
		if(paramMap==null){
			return;
		}
		
		for(String key:paramMap.keySet()){
			String str = key+"="+paramMap.get(key);
			requestBody=requestBody+"&"+str;
		}
		
		Allure.addAttachment("RequestBody:", requestBody.substring(requestBody.indexOf('&')+1, requestBody.length()));
	}
	
	@Description("将响应数据添加到测试报告中")
	public void responseLog(Response response){		
		Allure.addAttachment("Response Body:", response.getBody().asString());
	}
	
	public static void main(String[] args) {
		Map<String, Map<String,Object>> map = new HashMap<>();
		
		String path = "/enterprise/settledapply/index/ajax/settled-apply-grid/SettledApply_sort/id.desc";
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Method", "GET");
		baseMap.put("basePath", "http://nchr.release.microfastup.com");
		baseMap.put("path", path);
		map.put("base", baseMap);
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("ajax", "settled-apply-grid");
		map.put("querys", queryMap);
		
		HttpMethods http = new HttpMethods();
		Response response = http.post(map);
		
		String id = response.andReturn().htmlPath().getString("//*[@id=\"settled-apply-grid\"]/table/tbody/tr[1]/td[1]");
		System.out.println(id);
	}
}
