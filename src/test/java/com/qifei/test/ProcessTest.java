package com.qifei.test;

import java.util.Map;

import org.testng.annotations.Test;

import com.qifei.utils.TxtData;
import com.qifei.utils.test.BaseTest;

public class ProcessTest extends BaseTest {
	@Test(dataProvider = "CaseList", description= "整体流程冒烟测试")
	public void process_Test(Map<String, Object> baseData) {
		String api = baseData.get("API").toString();
		String filePath = getSrcDir()+"/case/"+baseData.get("FilePath");
		String caseName = baseData.get("Case").toString();
		setRequest(api,filePath,caseName);
//		checkResponse(getExpectedMap());
		TxtData txt = new TxtData();
		String filename = getSrcDir()+"\\temp\\"+api+".txt";
		txt.writerText(filename, getBodyStr());
	}
}