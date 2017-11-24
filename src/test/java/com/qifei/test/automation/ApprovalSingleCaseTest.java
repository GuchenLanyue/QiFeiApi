package com.qifei.test.automation;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.testng.annotations.Test;

import com.qifei.apis.Approval;
import com.qifei.apis.Employee;
import com.qifei.apis.Organization;
import com.qifei.utils.DateUtils;
import com.qifei.utils.ExcelWriter;
import com.qifei.utils.TxtData;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;
import junit.framework.Assert;

public class ApprovalSingleCaseTest extends BaseTest {
	
	@Test(dataProvider = "SingleCase", description= "获取所有审批类型")
	public void allTypes_Test(Map<String, Object> params){		
		setRequest("approvalTypes", params);
		//写入txt
		TxtData txt = new TxtData();
		String positionFile = srcDir+File.separator+"temp"+File.separator+"AllTypes.txt";
		txt.writerText(positionFile, bodyStr);
		JsonPath response = JsonPath.with(bodyStr);
		ExcelWriter excel = new ExcelWriter();
		File file = new File(srcDir+File.separator+"config"+File.separator+platform+File.separator+"data.xlsx");
		List<Object> form_names = response.getList("items.form_name");
		List<Object> names = response.getList("items.name");
		List<Object> ids = response.getList("items.uuid");
		for(int i=0;i<form_names.size();i++){
			excel.editExcel(file, "AllTypes", form_names.get(i).toString()+response.get("items["+i+"].sub_category"), "type_id", ids.get(i).toString());
			excel.editExcel(file, "AllTypes", form_names.get(i).toString()+response.get("items["+i+"].sub_category"), "name", names.get(i).toString());
			excel.editExcel(file, "AllTypes", form_names.get(i).toString()+response.get("items["+i+"].sub_category"), "uuid", response.get("items["+i+"].process_list[0].uuid"));
			excel.editExcel(file, "AllTypes", form_names.get(i).toString()+response.get("items["+i+"].sub_category"), "created_by", response.get("items["+i+"].created_by"));
		}
	}
	
	@Test(dataProvider="SingleCase",description="审批类型设置")
	public void ApprovalTypes_Test(Map<String, Object> params){
		setRequest("approvalTypes", params);
		checkResponse();
	}
	
	@Test(dataProvider="SingleCase",description="获取子部门id，否则连续新建子部门造成的垃圾数据太多")
	public void getOrganizationByName_Test(Map<String, Object> params){
		Organization organization = new Organization(basePath);
		//设置部门
		String organizationID = organization.getOrganizationID(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
		String organizationStr = "";
		if(organizationID.equals("")){
			organizationStr = organization.addOrganization(params.get("organization_Name").toString());
			organizationID = JsonPath.with(organizationStr).getString("uuid");
		}
		//写入txt
		TxtData txt = new TxtData();
		String organizationFile = srcDir+File.separator+"temp"+File.separator+params.get("organization_Name").toString()+".txt";
		organizationStr = organization.getOrganization(params.get("parent_organization_ID").toString(), params.get("organization_Name").toString());
		txt.writerText(organizationFile, organizationStr);
		//写入excel
		JsonPath response = JsonPath.with(organizationStr);
		ExcelWriter excel = new ExcelWriter();
		File file = new File(srcDir+File.separator+"config"+File.separator+platform+File.separator+"data.xlsx");
		excel.editExcel(file, "organization", response.get("name").toString(), "name", response.get("name").toString());
		excel.editExcel(file, "organization", response.get("name").toString(), "uuid", response.get("uuid").toString());
		excel.editExcel(file, "organization", response.get("name").toString(), "leader_ID", response.get("leader_ID").toString());
		excel.editExcel(file, "organization", response.get("name").toString(), "user_id", response.get("user_id").toString());

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
		String positionFile = srcDir+File.separator+"temp"+File.separator+params.get("position_Name").toString()+".txt";
		txt.writerText(positionFile, positionID);
		response = JsonPath.with(positionID);
		//写入excel
		excel.editExcel(file, "position", response.get("name").toString(), "name", response.get("name").toString());
		excel.editExcel(file, "position", response.get("name").toString(), "uuid", response.get("uuid").toString());
		excel.editExcel(file, "position", response.get("name").toString(), "organization", response.get("organization").toString());
		excel.editExcel(file, "position", response.get("name").toString(), "organization_ID", response.get("organization_ID").toString());
	}
	
	@Test(dataProvider="SingleCase",description="新增员工")
	public void addMember_Test(Map<String, Object> params){
		Map<String, Object> paramMap = params;
		String caseID = paramMap.get("CaseID").toString();
		paramMap.remove("CaseID");
		//新增员工
		Employee member = new Employee(basePath);
		String organization_ID = paramMap.get("organization_ID").toString();
		String position_ID = paramMap.get("position_ID").toString();
		String body = member.addEmployee(organization_ID,position_ID);
		
		TxtData txt = new TxtData();
		String filename = srcDir+File.separator+"temp"+File.separator+caseID+".txt";
		txt.writerText(filename, body);

		//验证结果
		member.checkResponse(body);
		JsonPath response = new JsonPath(body);
		//到岗
		member.join(response.get("uuid").toString());
		member.checkResponse(body);
	}
	
	@Test(dataProvider="SingleCase",description="转正")
	public void Formal_Test(Map<String, Object> params){
		Employee employee = new Employee(basePath);
		String body = employee.addEmployee();
		JsonPath user = JsonPath.with(body);
		employee.join(user.getString("uuid"));
		employee.checkResponse(body);
		
		Map<String,Object> paramMap = params;
		paramMap.put("organization", "");
		paramMap.put("organization_ID", "");
		paramMap.put("position", "");
		paramMap.put("position_ID", "");
		paramMap.put("employee_ID", user.getString("uuid"));
		paramMap.put("employee_no", "microfastup-"+user.getString("employee_no"));
		paramMap.put("employee", user.getString("name"));
		
		setRequest("Formal", paramMap);
		Map<String,Object> expectionMap = expectedMap;
		expectionMap.put("organization", "");
		expectionMap.put("organization_ID", "");
		expectionMap.put("position", "");
		expectionMap.put("position_ID", "");
		expectionMap.put("employee_ID", user.getString("uuid"));
		expectionMap.put("employee_no", "microfastup-"+user.getString("employee_no"));
		expectionMap.put("employee", user.getString("name"));
		
		checkResponse(expectionMap);
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description= "调整")
	public void Adjust_Test(Map<String, Object> params) {
		Employee employee = new Employee(basePath);
		//新增员工
		String body = employee.addEmployee();
		JsonPath user = JsonPath.with(body);
		//到岗
		employee.join(user.getString("uuid"));
		employee.checkResponse(body);
		//设置转正请求参数
		Map<String,Object> paramMap = params;
		paramMap.put("organization", "");
		paramMap.put("organization_ID", "");
		paramMap.put("position", "");
		paramMap.put("position_ID", "");
		paramMap.put("employee_ID", user.getString("uuid"));
		paramMap.put("employee_no", "microfastup-"+user.getString("employee_no"));
		paramMap.put("employee", user.getString("name"));
		Approval approval = new Approval(basePath);
		//设置转正流程
		approval.setApprovalType("employee_probation","");
		//发起转正审批
		String formal = employee.formal(paramMap);
		JsonPath formalJson = JsonPath.with(formal);
		//审批通过
		String instance_id = approval.getInstances(formalJson.getString("uuid"));
		JsonPath jsonPath = JsonPath.with(approval.getInstanceInfo(instance_id));
		approval.approval(instance_id, jsonPath.getString("tasks[0].uuid"));
		//发起调整审批
		setRequest("Adjust", paramMap);
		
		Map<String,Object> expectionMap = expectedMap;
		expectionMap.put("organization", "");
		expectionMap.put("organization_ID", "");
		expectionMap.put("position", "");
		expectionMap.put("position_ID", "");
		expectionMap.put("employee_ID", user.getString("uuid"));
		expectionMap.put("employee_no", formalJson.getString("employee_no"));
		expectionMap.put("employee", formalJson.getString("name"));
		expectionMap.put("comment", "调整审批测试");
		checkResponse(expectionMap);
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		//获取审批id
		uuid = approval.getInstances(uuid);
		//撤销审批
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="辞职")
	public void Offboard_Test(Map<String, Object> params){
		Employee employee = new Employee(basePath);
		String body = employee.addEmployee();
		JsonPath user = JsonPath.with(body);
		employee.join(user.getString("uuid"));
		employee.checkResponse(body);
		
		Map<String,Object> paramMap = params;
		paramMap.put("organization", "");
		paramMap.put("organization_ID", "");
		paramMap.put("position", "");
		paramMap.put("position_ID", "");
		paramMap.put("employee_ID", user.getString("uuid"));
		paramMap.put("employee_no", "microfastup-"+user.getString("employee_no"));
		paramMap.put("employee", user.getString("name"));
		
		setRequest("Offboard", paramMap);
		Map<String,Object> expectionMap = expectedMap;
		expectionMap.put("organization", "");
		expectionMap.put("organization_ID", "");
		expectionMap.put("position", "");
		expectionMap.put("position_ID", "");
		expectionMap.put("employee_ID", user.getString("uuid"));
		expectionMap.put("employee_no", "microfastup-"+user.getString("employee_no"));
		expectionMap.put("employee", user.getString("name"));
		
		checkResponse(expectionMap);
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="加班")
	public void OvertimeRequest_Test(Map<String, Object> params){
		setRequest("OvertimeRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="请假")
	public void LeaveRequest_Test(Map<String, Object> params){
		setRequest("LeaveRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="外出")
	public void OutRequest_Test(Map<String, Object> params){
		setRequest("OutRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="补卡")
	public void ResignRequest_Test(Map<String, Object> params){
		setRequest("ResignRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider="SingleCase",description="出差")
	public void TripRequest_Test(Map<String, Object> params){
		setRequest("TripRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider = "SingleCase", description= "项目")
	public void ProjectRequest_Test(Map<String, Object> params) {
		setRequest("ProjectRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}

	@Test(dataProvider = "SingleCase", description= "通用")
	public void GeneralRequest_Test(Map<String, Object> params) {
		setRequest("GeneralRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}

	@Test(dataProvider = "SingleCase", description= "物品领用")
	public void MaterialgetRequest_Test(Map<String, Object> params) {
		setRequest("MaterialgetRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}

	@Test(dataProvider = "SingleCase", description= "合同")
	public void ContractRequest_Test(Map<String, Object> params) {
		setRequest("ContractRequest", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider = "SingleCase", description= "采购")
	public void purchase_Test(Map<String, Object> params) {
		setRequest("purchase", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider = "SingleCase", description= "报销")
	public void reimbursement_Test(Map<String, Object> params) {
		setRequest("reimbursement", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider = "SingleCase", description= "付款")
	public void payment_Test(Map<String, Object> params) {
		setRequest("payment", params);
		checkResponse();
		JsonPath response = JsonPath.with(bodyStr);
		String uuid = response.get("uuid").toString();
		Approval approval = new Approval(basePath);
		uuid = approval.getInstances(uuid);
		approval.cancel(uuid);
	}
	
	@Test(dataProvider = "SingleCase", description= "审批统计-审批明细")
	public void approval_ApprovalDetails_Test(Map<String,Object> params){
//		setRequest("ApprovalDetailsDefault",params);
		setRequest("ApprovalDetailsCustomer",params);
		JsonPath json = JsonPath.with(bodyStr);
		List<String> list = json.getList("items");
		for(int i = 0 ;i < list.size() ; i++){
			String dateStr = json.getString("items["+i+"].created_at");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+08:00");  
		    Date actualDate = null;
			try {
				actualDate = sdf.parse(dateStr.replace("T", " "));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sdf = new SimpleDateFormat("yyyy-MM-dd");  
			DateUtils dateUtils = new DateUtils();
			String firstDate = dateUtils.getMonth() + "-01";
			Date expectedDate = null;
			try {
				expectedDate = sdf.parse(firstDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Assert.assertTrue("\""+json.getString("items["+i+"].uuid")+"\""+" Approval time early of "+dateUtils.getMonth()+"-01", actualDate.getTime()>expectedDate.getTime());
		}
	}
	
	@Test(description="离职所有待转正员工")
	public void cleanEmploee_Test(){
		Employee employee = new Employee(basePath);
		employee.clean("probation");
	}
}