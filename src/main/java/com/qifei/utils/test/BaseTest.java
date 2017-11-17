package com.qifei.utils.test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.qifei.utils.DateUtils;
import com.qifei.utils.ExcelReader;
import com.qifei.utils.JsonUtils;
import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.io.File;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;

public class BaseTest {
	protected String srcDir = null;
	protected String method = null;
	protected String bodyStr = null;
	protected Response response = null;
	protected String expectedJson = null;
	protected String basePath = null;
	protected String platform = null;
	protected String caseName = null;
	protected String filePath = null;
	protected Map<String, Object> expectedMap = new HashMap<>();
	
	public void setSrcDir(ITestContext context){
		srcDir = System.getProperty("user.dir")+context.getCurrentXmlTest().getParameter("sourcesDir");
	}
	
	public void setbasePath(ITestContext context){
		basePath = context.getCurrentXmlTest().getParameter("basePath");
	}
	
	public void setPlatform(ITestContext context){
		platform = context.getCurrentXmlTest().getParameter("platform");
	}
	
	@BeforeTest
	public void BeforeTest(ITestContext context) {
		System.out.println(context.getName()+" Start!");
		setSrcDir(context);
		setbasePath(context);
		setPlatform(context);
	}
	
	@DataProvider(name = "CaseList")
	public Iterator<Object[]> caseData(ITestContext context,Method testMethod) {
		String methodName = testMethod.getName();
		String[] caseStr = methodName.split("_");
		method = caseStr[caseStr.length-2];
		filePath = srcDir+"/case/"+method+".xlsx";
		
		String sheetName = caseStr[caseStr.length-3];
		ExcelReader excel = new ExcelReader(srcDir,platform);
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
		filePath = srcDir+"/case/"+method+".xlsx";
		String sheetName = "Params";
		ExcelReader excel = new ExcelReader(srcDir,platform);
		List<Map<String, Object>> caseList = excel.mapList(1,filePath, sheetName);
		List<Object[]> test_IDs = new ArrayList<Object[]>();
		for (Map<String, Object> params:caseList) {
			test_IDs.add(new Object[]{params});
		}
		
		return test_IDs.iterator();
	}
	
	@Step
	public void setRequest(String api,Map<String, Object> paramMap) {
		Parameter parameter = new Parameter(srcDir , platform);
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
		Parameter parameter = new Parameter(srcDir,platform);
		//获取请求路径，请求类型等基本信息
		Map<String, Object> baseMap = parameter.setUrlData(api, filePath);
//		baseMap.put("basePath", baseMap.get("BasePath").toString());
		this.caseName = caseName;
		
		//获取参数值
		Map<String, Object> paramMap = parameter.setParams(filePath, caseName);
		//设置路径参数
		Map<String, Object> pathParamMap = new HashMap<>();

		if(baseMap.get("Path").toString().contains("{Date_today}")){
			String path = baseMap.get("Path").toString();
			while (path.contains("{Date_today}")) {
				DateUtils dateUtils = new DateUtils();
				String date = dateUtils.getToday();
				int beginIndex = path.indexOf("{Date_today}");
				int endIndex = path.indexOf("}",beginIndex);
				path = path.substring(0, beginIndex)+date+path.substring(endIndex+1, path.length());
				baseMap.put("Path", path);
			}
		}else if(baseMap.get("Path").toString().contains("{Date_tomorrow}")){
			String path = baseMap.get("Path").toString();
			while (path.contains("{Date_tomorrow}")) {
				DateUtils dateUtils = new DateUtils();
				String date = dateUtils.getToday();
				int beginIndex = path.indexOf("{Date_tomorrow}");
				int endIndex = path.indexOf("}",beginIndex);
				path = path.substring(0, beginIndex)+date+path.substring(endIndex+1, path.length());
				baseMap.put("Path", path);
			}
		}if(baseMap.get("Path").toString().contains("{")){			
			pathParamMap = setPathParamters(baseMap, paramMap);
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
		String response = bodyStr;
		JSONObject responseObj = new JSONObject();
		JSONArray responseArr = new JSONArray();
		JSONObject expections = new JSONObject(expected);
		JsonUtils jsonUtil = new JsonUtils();
		if(response.startsWith("{")){
			responseObj = new JSONObject(response);
		}else if(response.startsWith("[")){
			responseArr = new JSONArray(response);
		}
		isContinue = jsonUtil.compareJSONObject(responseObj, expections);
		
		return isContinue;
	}
	
	public Map<String, Object> setPathParamters(Map<String, Object> baseMap,Map<String, Object> paramMap){
		String path = baseMap.get("Path").toString();
		Map<String, Object> pathParamMap = new HashMap<>();

		int beginIndex = path.indexOf("{");
		int endIndex = path.indexOf("}",beginIndex);
		while (beginIndex < path.lastIndexOf("{")) {
			String pathParam = path.substring(beginIndex+1, endIndex);
			String pathParamStr = paramMap.get(pathParam).toString();
			pathParamMap.put(pathParam, pathParamStr);
			beginIndex = path.indexOf("{",beginIndex+1);
			endIndex = path.indexOf("}",beginIndex);
		}
		beginIndex = path.lastIndexOf("{");
		endIndex = path.indexOf("}",beginIndex);
		String pathParam = path.substring(beginIndex+1, endIndex);
		String pathParamStr = paramMap.get(pathParam).toString();
		pathParamMap.put(pathParam, pathParamStr);
		
		return pathParamMap;
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
		File file = new File(bodyStr+"/temp/access_token.txt");
		if(file.exists()&&file.isFile()){
			file.delete();
		}
		System.out.println(context.getName()+" End!");
	}
}
