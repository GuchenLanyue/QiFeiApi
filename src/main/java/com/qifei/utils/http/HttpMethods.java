package com.qifei.utils.http;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
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
				//.log().method()
				//.proxy("127.0.0.1", 8888)
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
//				.statusCode(200)
				//.log().status()
			.extract()
				.response();

		requestLog(map);
		responseLog(response);
		return response;
	}
	
	@Step("get() 发起请求")
	public Response get(Map<String, Map<String,Object>> map){
		Response response = given()
				//.proxy("127.0.0.1", 8888)
				//.log().method()
//				//.log().all()
				//.log().uri()
				//.log().body()
//				//.log().params()
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
//				//.log().body()
				//.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		requestLog(map);
		responseLog(response);
		return response;
	}
	
	@Step("put() 发起请求")
	public Response put(Map<String, Map<String,Object>> map){
		Response response = given()
				//.proxy("127.0.0.1", 8888)
				//.log().method()
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
				//.log().status()
//				.statusCode(200)
			.extract()
				.response();
		requestLog(map);
		responseLog(response);
		return response;
	}
	
	@Step("delete() 发起请求")
	public Response delete(Map<String, Map<String,Object>> map){
		Response response = given()
				//.proxy("127.0.0.1", 8888)
				//.log().method()
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
				//.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		return response;
	}
	
	@Step("post() 发起请求")
	public Response postNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				//.log().method()
				//.proxy("127.0.0.1", 8888)
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
//				.statusCode(200)
				//.log().status()
			.extract()
				.response();

		responseLog(response);
		return response;
	}
	
	@Step("get() 发起请求")
	public Response getNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				//.proxy("127.0.0.1", 8888)
				//.log().method()
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
				//.log().status()
//				.statusCode(200)
			.extract()
				.response();
		
		responseLog(response);
		return response;
	}
	
	@Step("put() 发起请求")
	public Response putNobody(Map<String, Map<String,Object>> map){
		Response response = given()
				//.proxy("127.0.0.1", 8888)
				//.log().method()
//				//.log().all()
				//.log().uri()
//				//.log().params()
				//.log().body()
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
//				//.log().body()
				//.log().status()
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
	public void requestLog(Map<String, Map<String,Object>> params){
		String requestBody = " ";
		if(paramMap==null){
			return;
		}
		
		for(String key:paramMap.keySet()){
			String str = key+"="+paramMap.get(key);
			requestBody=requestBody+"&"+str;
		}
		
		Allure.addAttachment("Request:", new JSONObject(params).toString());
	}
	
	@Description("将响应数据添加到测试报告中")
	public void responseLog(Response response){		
		Allure.addAttachment("Response Body:", response.getBody().asString());
	}
	
	public static void main(String[] args) {
		Map<String, Map<String,Object>> map = new HashMap<>();
		
		File file = new File("C:\\Users\\sam\\git\\QiFeiApi\\sources\\temp\\htl.jpg");
		String path = "/00b19169-fc38-4e51-a99f-98f31ccaa46d/form/5ceb080b-0e25-11e7-b0e5-00163e007053/2f4158cc-c393-11e7-9c79-5254001aba5d/IMG_20171029_124310R.jpg";
		
		Map<String, Object> headerMap = new HashMap<>();
		
		headerMap.put("x-oss-security-token","CAISyAJ1q6Ft5B2yfSjIprTNEc3/mo9l1bG7Q3L5pjc6Rth0mKjG2jz2IHBLfHhrAu0Wsf4xnWxZ6PwZlrh+W4NIX0rNaY5t9ZlN9wqkbtJyYyxyfP9W5qe+EE2/VjQVta27OpcRJbGwU/OpbE++yE0X6LDmdDKkckW4OJmS8/BOZcgWWQ/KG1gjA8xNdCRvtOgQN3baKZTINQXx0FLNEG1iuAd3lRkoi8KFz9ab9wDVgXDj1+YRvP6RGJW/aNR2N5oNGLX81edtJK3ay3wSuUEWrPko0fMfom6Y5Y7FXgMD4A+GKa2W0KU2cFcnOPVlQPUd86OjzaAigIGJydSrkSQqFPpOTiHSSLqnxMb5A+6zPr47D+2rZCyciYvSZsav7Vp4MCxBbxk5ct4gO2J2Dgc3VjbZJ6mo9VbHeA6/TLKf16U7wS7+d8kOfg3gGoABiYD5nF+0MjJ6vsp3XsRgSEP/j9zHIFUqMqXuWcvE+HeQ894sUb++HOXAAD4IgXRMcFdsuueQE+QJAiOUc3OuGk11130wfWfGlUjBLqB2/ifQfei/5o/eFtSVuHbOdOv91I6DOiWPaWX6WvsJaTaJq9eO6MuLc6C9r9WG0J7CSiw=");
		headerMap.put("x-oss-callback-var","eyJ4OmFjY2Vzc190b2tlbiI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUpoWTJOdmRXNTBJam9pYldsamNtOW1ZWE4wZFhBaUxDSmpiMjF3WVc1NVgyNWhiV1VpT2lMbGpKZmt1cXpsdnE3b3Y0VWlMQ0pqYjIxd1lXNTVYM05vYjNKMFgyNWhiV1VpT2lJaUxDSmxiWEJzYjNsbFpWOXBaQ0k2SW1JNFlUSXpOelV6TFRCbU56VXRNVEZsTnkwNVlXRTBMVEF3TVRZelpUQXdOekExTXlJc0ltVnRjR3h2ZVdWbFgzTjBZWFIxY3lJNkltRmpkR2wyWlNJc0ltVjRjQ0k2TVRVeE1ERXdPVEF3TXl3aVptbHljM1JmZFhObGNsOXBaQ0k2SWpWalpXSXdPREJpTFRCbE1qVXRNVEZsTnkxaU1HVTFMVEF3TVRZelpUQXdOekExTXlJc0ltWnBjbk4wWDNWelpYSmZiVzlpYVd4bElqb2lNVGcyTVRJNE1UazBNamNpTENKb1lYTmZjSGRrSWpwMGNuVmxMQ0pzWVhOMFgzUmxibUZ1ZEY5cFpDSTZkSEoxWlN3aWNtOXNaWE1pT2xzaWFISmZiMlptYVdObGNpSXNJbTFoYm1GblpYSWlMQ0p0WlcxaVpYSWlMQ0poWkcxcGJpSXNJbUYwZEdWdVpHRnVZMlZmYjJabWFXTmxjaUlzSW5CaGVYSnZiR3hmYjJabWFXTmxjaUpkTENKMFpXNWhiblJmWVdOamIzVnVkQ0k2SWlJc0luUmxibUZ1ZEY5cFpDSTZJakF3WWpFNU1UWTVMV1pqTXpndE5HVTFNUzFoT1RsbUxUazRaak14WTJOaFlUUTJaQ0lzSW5WelpYSmZhV1FpT2lJMVkyVmlNRGd3WWkwd1pUSTFMVEV4WlRjdFlqQmxOUzB3TURFMk0yVXdNRGN3TlRNaUxDSjFjMlZ5WDIxdlltbHNaU0k2SWpFNE5qRXlPREU1TkRJM0lpd2lkWE5sY2w5dVlXMWxJam9pNUw2djZiNlpJaXdpZFhObGNsOXpaWGdpT2lJaUxDSjFjMlZ5WDNOMFlYUjFjeUk2SW1GamRHbDJaU0o5LkdlSUZhQ3ZCOXpQc2NRYlhVQnRHQ0RpX1Rha1NZcXQ0UDh3a3VMYXlDNkEifQ==");
		headerMap.put("x-oss-callback","eyJjYWxsYmFja0JvZHlUeXBlIjoiYXBwbGljYXRpb25cL3gtd3d3LWZvcm0tdXJsZW5jb2RlZCIsImNhbGxiYWNrSG9zdCI6ImNvbnNvbGUudC51cHZpLmNvbSIsImNhbGxiYWNrVXJsIjoiaHR0cDpcL1wvY29uc29sZS50LnVwdmkuY29tXC9iYXBpXC9vc3NfY2FsbGJhY2tcL3YxXC9cL29iamVjdHNcLzgxNmMwMDdjLWMzOTgtMTFlNy05Yzc5LTUyNTQwMDFhYmE1ZCIsImNhbGxiYWNrQm9keSI6ImJ1Y2tldD0ke2J1Y2tldH0mb2JqZWN0PSR7b2JqZWN0fSZldGFnPSR7ZXRhZ30mc2l6ZT0ke3NpemV9Jm1pbWVUeXBlPSR7bWltZVR5cGV9JmFjY2Vzc190b2tlbj0ke3g6YWNjZXNzX3Rva2VufSJ9");
		
		headerMap.put("Authorization", "OSS STS.GwxZwKwPDbsPATHB7oJWXwmm8:d33iXg22ypox783NTrG/LVv9FDg=");
		headerMap.put("Accept-Encoding", "gzip");
		headerMap.put("Date", "Tue, 07 Nov 2017 08:49:06 GMT");
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("Method", "PUT");
		baseMap.put("basePath", "http://krhr.oss-cn-beijing.aliyuncs.com");
		baseMap.put("path", path);
		map.put("base", baseMap);
		
		given()
		//.proxy("127.0.0.1", 8888)
		//.log().method()
		//.log().uri()
		.headers(headerMap)
		.config(RestAssured.config()
				  .encoderConfig(EncoderConfig.encoderConfig()
						    .defaultContentCharset("UTF-8")
						    .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
		.contentType("image/jpeg")
		.body(file)
	.when()
		.put("http://krhr.oss-cn-beijing.aliyuncs.com/00b19169-fc38-4e51-a99f-98f31ccaa46d/form/5ceb080b-0e25-11e7-b0e5-00163e007053/8166c5f6-c398-11e7-9c79-5254001aba5d/IMG_20171029_124310R.jpg")
	.then()
		//.log().status()
	.extract()
		.response();
	}
}
