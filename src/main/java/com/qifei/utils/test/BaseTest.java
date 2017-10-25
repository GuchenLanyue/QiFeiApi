package com.qifei.utils.test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.qifei.apis.Path;
import com.qifei.utils.ExcelReader;
import com.qifei.utils.JsonUtils;
import com.qifei.utils.TxtData;
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

import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;

public class BaseTest {
	private String srcDir = null;
	private String method = null;
	private String bodyStr = null;
	private Response response = null;
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
		return bodyStr;
	}
	
	public Response getResponse(){
		return response;
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
	public void setRequest(String api,Map<String, Object> paramMap) {
		Parameter parameter = new Parameter();
		//设置请求类型，请求路径等基本数据
		Map<String, Object> baseMap = parameter.setUrlData(api,filePath);
		baseMap.put("basePath", basePath);
		//设置路径参数
		Map<String, Object> pathParamMap = new HashMap<>();
		if(baseMap.get("Path").toString().contains("{")){
			String path = baseMap.get("Path").toString();
			String pathParam = path.substring(path.indexOf("{")+1, path.lastIndexOf("}"));
			if(paramMap.containsKey(pathParam)){
				pathParamMap.put(pathParam, paramMap.get(pathParam));
			}else{
				Path pathStr = new Path(basePath);
				String pathParamStr = pathStr.analysisPath(pathParam);
				pathParamMap.put(pathParam, pathParamStr);
			}
		}
		
		//为caseName赋值，并将CaseID从参数值Map中删除。
		if (paramMap.containsKey("CaseID")) {
			caseName = paramMap.get("CaseID").toString();
			paramMap.remove("CaseID");
		}
		
		//获取预期结果
		expectedMap = parameter.setExpectedMap(filePath, caseName);
		if(expectedMap.containsKey("CaseID")){
			expectedMap.remove("CaseID");
		}
		
		//Excel读取的所有数据都是double类型，服务器端会对数据类型经行验证，需要做下处理。
		String jsonFile = getSrcDir()+"\\case\\"+api+".json";
		JsonUtils jsonUtil = new JsonUtils();
		if(new File(jsonFile).exists()){
			paramMap = jsonUtil.formatMap(jsonFile,paramMap);
		}else{
			paramMap = jsonUtil.formatMap(paramMap);
		}
		
		expectedMap = jsonUtil.formatMap(expectedMap);
		
		//设置header
		Map<String, Object> headerMap = new HashMap<>();
		Headers header = new Headers(basePath);
		headerMap.put("Authorization", header.getAuthorization());
		
		//设置请求数据
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		HttpMethods http = new HttpMethods(basePath);
		response = http.request(map);
		
		saveResponseBody(response);
	}

	@Step
	public void setRequest(String api,String filePath, String caseName) {
		this.filePath = filePath;
		Parameter parameter = new Parameter();
		//获取请求路径，请求类型等基本信息
		Map<String, Object> baseMap = parameter.setUrlData(api, filePath);
		baseMap.put("basePath", baseMap.get("BasePath").toString());
		this.caseName = caseName;
		
		//获取参数值
		Map<String, Object> paramMap = parameter.setParams(filePath, caseName);
		//设置路径参数
		Map<String, Object> pathParamMap = new HashMap<>();
		if(baseMap.get("Path").toString().contains("{")){
			String path = baseMap.get("Path").toString();
			String pathParam = path.substring(path.indexOf("{")+1, path.lastIndexOf("}"));
			String pathParamStr = paramMap.get(pathParam).toString();
			if(pathParamStr.contains("${")){
				String jsonFile = pathParamStr.substring(pathParamStr.indexOf("{")+1, pathParamStr.indexOf("."));
				String paramPath = pathParamStr.substring(pathParamStr.indexOf(".")+1, pathParamStr.indexOf("}"));
				jsonFile = getSrcDir()+"\\temp\\"+jsonFile+".txt";
				TxtData txt = new TxtData();
				String jsonStr = txt.readTxtFile(jsonFile);
				JsonPath jsonPath = JsonPath.with(jsonStr);
				pathParamStr = jsonPath.getString(paramPath);
			}
			
			pathParamMap.put(pathParam, pathParamStr);
			paramMap.remove(pathParam);
		}
		//为caseName赋值，并将CaseID从参数值Map中删除。
		if (paramMap.containsKey("CaseID")) {
			caseName = paramMap.get("CaseID").toString();
			paramMap.remove("CaseID");
		}		
		
		//获取预期结果
		expectedMap = parameter.setExpectedMap(filePath, caseName);
		if(expectedMap.containsKey("CaseID")){
			expectedMap.remove("CaseID");
		}
		
		//Excel读取的所有数据都是double类型，服务器端会对数据类型经行验证，需要做下处理。
		JsonUtils jsonUtil = new JsonUtils();
		String jsonFile = getSrcDir()+"\\case\\"+api+".json";
		File jFile = new File(jsonFile);
		if(jFile.exists()){
			paramMap = jsonUtil.formatMap(jsonFile,paramMap);
		}else{
			paramMap = jsonUtil.formatMap(paramMap);
		}
		
		expectedMap = jsonUtil.formatMap(expectedMap);
		//设置header
		Map<String, Object> headerMap = new HashMap<>();
		Headers header = new Headers(basePath);
		headerMap.put("Authorization", header.getAuthorization());
		//设置请求数据
		Map<String, Map<String, Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("params", paramMap);
		map.put("headers", headerMap);
		map.put("pathParams", pathParamMap);
		HttpMethods http = new HttpMethods(basePath);
		response = http.request(map);
		//保存response
		saveResponseBody(response);
	}
	
	@Step("checkResponse() 校验response")
	public boolean checkResponse(Map<String, Object> expected) {
		boolean isContinue = false;
		String response = getBodyStr();
		JSONObject responseObj = new JSONObject();
		JSONObject expections = new JSONObject(expected);
		if(response.startsWith("{")){
			responseObj = new JSONObject(response);
		}
		
		JsonUtils jsonUtil = new JsonUtils();
		isContinue = jsonUtil.compareJSONObject(responseObj, expections);
		
		return isContinue;
	}
	
	@Attachment(value = "Response.Body",type = "String")
	public String saveResponseBody(Response response) {
		String body = response.getBody().asString();
		Allure.addAttachment("Response.body", body);
		
		if(body.length()>0&&body.contains("{")){
			body = body.substring(body.indexOf("{"), body.lastIndexOf("}")+1);
		}
		
		bodyStr = body;
		return bodyStr;
	}
	
	@AfterTest
	public void AfterTest(ITestContext context) {
//		File file = new File(getSrcDir()+"\\config\\access_token.txt");
//		if(file.exists()&&file.isFile()){
//			file.delete();
//		}
		System.out.println(context.getName()+" End!");
	}
}
