package com.qifei.utils.test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.qifei.utils.ExcelReader;
import com.qifei.utils.JsonUtils;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.File;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.AfterTest;

public class BaseTest {
	private String srcDir = null;
	private String method = null;
	private String responseStr = null;
	private String expectedJson = null;
	private String basePath = null;
	private String caseName = null;
	private String filePath = null;
	private Map<String, Object> expectedMap = new HashMap<>();
	
	public void setSrcDir(ITestContext context){
		srcDir = System.getProperty("user.dir")+context.getCurrentXmlTest().getParameter("sourcesDir");
	}
	
	public void setbasePath(ITestContext context){
		basePath = context.getCurrentXmlTest().getParameter("basePath");
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public String getSrcDir(){
		return srcDir;
	}
	
	public String getbasePath(){
		return basePath;
	}
	
	public String getBodyStr(){
		return responseStr;
	}
	
	public String getExpectedJson(){
		return expectedJson;
	}
	
	public Map<String, Object> getExpectedMap(){
		return expectedMap;
	}
	
	@BeforeTest
	public void BeforeTest(ITestContext context) {
		System.out.println(context.getName()+" Start!");
		setSrcDir(context);
		setbasePath(context);
	}
	
	@DataProvider(name = "CaseList")
	public Iterator<Object[]> caseData(ITestContext context,Method testMethod) {
		String methodName = testMethod.getName();
		String[] caseStr = methodName.split("_");
		method = caseStr[caseStr.length-2];
		filePath = getSrcDir()+"/case/"+method+".xlsx";
		
		String sheetName = "CaseList";
		ExcelReader excel = new ExcelReader();
		List<Map<String, Object>> caseList = excel.mapList(1,filePath, sheetName);
		List<Object[]> test_IDs = new ArrayList<Object[]>();

		for (Map<String, Object> baseData:caseList) {
			test_IDs.add(new Object[]{baseData});
		}
		
		return test_IDs.iterator();
	}
	
	@DataProvider(name = "SingleCase")
	public Iterator<Object[]> singleCase(Method testMethod) {
		String methodName = testMethod.getName();
		String[] caseStr = methodName.split("_");
		method = caseStr[caseStr.length-2];
		filePath = getSrcDir()+"/case/"+method+".xlsx";
		String sheetName = "Params";
		ExcelReader excel = new ExcelReader();
		List<Map<String, Object>> caseList = excel.mapList(1,filePath, sheetName);
		List<Object[]> test_IDs = new ArrayList<Object[]>();
		for (Map<String, Object> params:caseList) {
			test_IDs.add(new Object[]{params});
		}
		
		return test_IDs.iterator();
	}
	
	@Step
	public void setRequest(String api,Map<String, Object> paramsMap) {
		Parameter parameter = new Parameter();
		Map<String, Object> baseMap = parameter.setUrlData(api,filePath);
		baseMap.put("basePath", basePath);
		
		if (paramsMap.containsKey("CaseID")) {
			caseName = paramsMap.get("CaseID").toString();
			paramsMap.remove("CaseID");
		}
		
		expectedMap = parameter.setExpectedMap(filePath, caseName);
		if(expectedMap.containsKey("CaseID")){
			expectedMap.remove("CaseID");
		}
//		for(String key:expectedMap.keySet()){
//			if(paramsMap.containsKey(key)){
//				expectedMap.put(key, paramsMap.get(key));
//			}
//		}
		Map<String, Object> headerMap = new HashMap<>();
		Headers header = new Headers(basePath);
		headerMap.put("Authorization", header.getAuthorization());
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramsMap);
		map.put("headers", headerMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		saveResponseBody(response);
	}

	@Step
	public void setRequest(String api,String filePath, String caseName) {
		this.filePath = filePath;
		Parameter parameter = new Parameter();
		Map<String, Object> baseMap = parameter.setUrlData(filePath, api);
		baseMap.put("basePath", basePath);
		this.caseName = caseName;
		Map<String, Object> paramsMap = parameter.setParams(filePath, caseName);
		
		if (paramsMap.containsKey("Case")) {
			paramsMap.remove("Case");
		}
		
		Map<String, Object>expectedMap  = parameter.setExpectedMap(filePath, caseName);
		
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramsMap);
		
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		saveResponseBody(response);
		expectedJson = expectedMap.get("Path").toString();
	}
	
	@Step("checkResponse() 校验response")
	public void checkResponse() {
		String response = getBodyStr();
		
		//部分接口返回的response首字母不是“{”，不符合json格式，在这里处理一下
		
		response = response.substring(response.indexOf("{"), response.lastIndexOf("}"));
		
		JsonPath jsonPath = new JsonPath(response);

		String jsonFile = getSrcDir() + expectedJson;
		JsonUtils jsonUtil = new JsonUtils();
		
		jsonUtil.equalsJson(jsonFile, jsonPath);
	}
	
	@Step("checkResponse() 校验response")
	public void checkResponse(Map<String, Object> params) {
		String response = getBodyStr();
		
		//部分接口返回的response首字母不是“{”，不符合json格式，在这里处理一下
		while (response.charAt(0) != '{') {
			response = response.substring(1, response.length());
		};

		JsonPath jsonPath = new JsonPath(response);

		String jsonFile = getSrcDir() + expectedJson;
		JsonUtils jsonUtil = new JsonUtils();
		
		jsonUtil.equalsJson(params, jsonFile, jsonPath);
	}
	
	@Attachment(value = "Response.Body",type = "String")
	public String saveResponseBody(Response response) {
		String body = response.getBody().asString();
		Allure.addAttachment("Response.body", body);
		
		while(body.charAt(0)!='{'){
			body = body.substring(1, body.length());
		}
		
		responseStr = body;
		return responseStr;
	}
	
	@AfterTest
	public void AfterTest(ITestContext context) {
		File file = new File(getSrcDir()+"\\config\\access_token.txt");
		if(file.exists()&&file.isFile()){
			file.delete();
		}
		System.out.println(context.getName()+" End!");
	}
}