package com.qifei.apis;

import java.util.Map;

import org.testng.annotations.Test;

import com.qifei.utils.test.BaseTest;

import io.qameta.allure.Description;

public class ProcessTest extends BaseTest {
	@Test(dataProvider = "CaseList", description= "官网接口冒烟测试_所有接口运行_正常测试")
	@Description("南昌人力资源项目PC端接口测试")
	public void process_Test(Map<String, Object> baseData) {
		String api = baseData.get("API").toString();
		String filePath = getSrcDir()+"/case/"+baseData.get("FilePath");
		String caseName = baseData.get("Case").toString();
		setRequest(api,filePath,caseName);
		checkResponse(getExpectedMap());
	}
}

