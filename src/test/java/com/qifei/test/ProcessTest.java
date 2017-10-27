package com.qifei.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.qifei.apis.Members;
import com.qifei.apis.Organization;
import com.qifei.utils.TxtData;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;

public class ProcessTest extends BaseTest {
	
	@Test(dataProvider = "SingleCase", description= "删除子部门，为整体流程测试做准备")
	public void DeleteOrganizationByName_Test(Map<String, Object> params){
		Organization organization = new Organization(getBasePath());
		organization.deleteOrganization(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
	}
	
	@Test(dataProvider="SingleCase",description="获取子部门id，否则连续新建子部门造成的垃圾数据太多")
	public void getOrganizationByName_Test(Map<String, Object> params){
		Organization organization = new Organization(getBasePath());
		//设置部门
		String organizationID = organization.getOrganizationID(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
		String organizationStr = null;
		if(organizationID.equals(null)){
			organizationStr = organization.addOrganization(params.get("organization_Name").toString());
			organizationID = JsonPath.with(organizationStr).getString("uuid");
		}
		//写入txt
		TxtData txt = new TxtData();
		String organizationFile = getSrcDir()+"\\temp\\"+params.get("organization_Name").toString()+".txt";
		organizationStr = organization.getOrganization(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
		
		txt.writerText(organizationFile, organizationStr);
		//设置岗位
		String positionID=null;
		List<String> positions = organization.getPositionsID(organizationID);
		if(positions.size()==0){
			positionID = organization.addPosition(organizationID, params.get("position_Name").toString());
		}else{
			String body = organization.getPositions(organizationID);
			JSONObject obj = new JSONObject(body);
			positionID = obj.getJSONArray("items").get(0).toString();
		}
		//写入txt
		String positionFile = getSrcDir()+"\\temp\\"+params.get("position_Name").toString()+".txt";
		txt.writerText(positionFile, positionID);
	}
	
	@Test(dataProvider = "CaseList", description= "调整审批流程冒烟测试")
	public void adjust_Smoke_Test(Map<String, Object> baseData) {
		if(baseData.get("API").toString().equals("")){
			return;
		}
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
		
		TxtData txt = new TxtData();
		String filename = getSrcDir()+"\\temp\\"+api+".txt";
		txt.writerText(filename, getBodyStr());
		
		if(api.equals("Auth")){
			JsonPath body = JsonPath.with(getBodyStr());
			String authorization = "Bearer " + body.getString("access_token");
			String tokenFile = System.getProperty("user.dir")+"\\sources\\config\\access_token.txt";
			txt.writerText(tokenFile, authorization);
		}
	}
	
	@Test(dataProvider="SingleCase",description="新增员工")
	public void addMember_Test(Map<String, Object> params){
		Map<String, Object> paramMap = params;
		String caseID = paramMap.get("CaseID").toString();
		paramMap.remove("CaseID");
		//新增员工
		Members member = new Members(getBasePath());
		String body = member.addMember(paramMap);
		
		TxtData txt = new TxtData();
		String filename = getSrcDir()+"\\temp\\"+caseID+".txt";
		txt.writerText(filename, body);
		//验证结果
		member.checkResponse(body);
		JsonPath response = new JsonPath(body);
		//到岗
		member.join(response.get("uuid").toString());
		member.checkResponse(body);
	}
	
	@Test(dataProvider="CaseList",description="转正审批测试")
	public void join_Formal_Smoke_Test(Map<String, Object> baseData){
		if(baseData.get("API").toString().equals("")){
			return;
		}
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
		
		TxtData txt = new TxtData();
		String filename = getSrcDir()+"\\temp\\"+api+".txt";
		txt.writerText(filename, getBodyStr());
	}
}