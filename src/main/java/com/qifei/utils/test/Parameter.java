package com.qifei.utils.test;

import java.util.Map;

import com.qifei.utils.ExcelReader;

import io.qameta.allure.Description;
import io.qameta.allure.Step;

public class Parameter {
	private String src = "";
	private String platform = "";
	public Parameter(String src,String platform) {
		// TODO Auto-generated constructor stub
		this.src = src;
		this.platform = platform;
	}
	
	@Step
	@Description("从excel中读取url数据")
	public Map<String, Object> setUrlData(String api,String filePath){
		ExcelReader baseExcel = new ExcelReader(src,platform);
		Map<String, Object> baseMap = baseExcel.mapFromSheet(filePath, "Base", api);
		
		return baseMap;
	}
	
	@Step
	@Description("从excel中读取params数据")
	public Map<String, Object> setParams(String filePath,String caseName){
		ExcelReader paramsExcel = new ExcelReader(src,platform);
		Map<String, Object> paramsMap = paramsExcel.mapFromSheet(filePath, "Params", caseName);
		paramsMap.remove("Case");
		
		return paramsMap;
	}
	
	@Step
	@Description("从excel中读取expected数据")
	public Map<String, Object> setExpectedMap(String filePath,String caseName){
		ExcelReader expectedExcel = new ExcelReader(src,platform);
		Map<String, Object> expectedMap = expectedExcel.mapFromSheet(filePath, "Expectations", caseName);
		
		return expectedMap;
	}
}
