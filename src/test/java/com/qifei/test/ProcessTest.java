package com.qifei.test;

import java.util.Map;

import org.testng.annotations.Test;

import com.qifei.apis.Organization;
import com.qifei.utils.TxtData;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;

public class ProcessTest extends BaseTest {
	
	@Test(dataProvider = "SingleCase", description= "删除子部门，为整体流程测试做准备")
	public void DeleteOrganizationByName_Test(Map<String, Object> params){
		Organization organization = new Organization(getbasePath());
		organization.deleteOrganization(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
	}
	
	@Test(dataProvider = "CaseList", description= "整体流程冒烟测试")
	public void process_Test(Map<String, Object> baseData) {
		String api = baseData.get("API").toString();
		String filePath = getSrcDir()+"/case/"+baseData.get("FilePath");
		String caseName = baseData.get("Case").toString();
		setRequest(api,filePath,caseName);
		
		long time = 5000;
		while (checkResponse(getExpectedMap())&&time<15000) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setRequest(api,filePath,caseName);
			time += 5000;
		}
		
		if(api=="Auth"){
			JsonPath body = JsonPath.with(getBodyStr());
			String authorization = "Bearer " + body.getString("access_token");
			String tokenFile = System.getProperty("user.dir")+"\\sources\\config\\access_token.txt";
			TxtData txt = new TxtData();
			txt.writerText(tokenFile, authorization);
		}
		
		TxtData txt = new TxtData();
		String filename = getSrcDir()+"\\temp\\"+api+".txt";
		txt.writerText(filename, getBodyStr());
	}
}