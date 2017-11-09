package com.qifei.test.attendance;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.qifei.apis.Attendance;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;

public class AttendancesTest extends BaseTest {
	
	@Test(dataProvider="SingleCase",description="内勤打卡")
	public void attendance_In_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(basePath);
		String uuid = attendance.getLocationID();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = params;
		paramMap.put("location_id", uuid);
//		paramMap.put("type", Integer.parseInt(params.get("type").toString()));
		
		setRequest("AttendanceIn", paramMap);
		Map<String, Object> location = attendance.getLocation(uuid);
		Map<String, Object> expected = expectedMap;
		for(String key:expected.keySet()){
			if(location.containsKey(key)){
				expected.put(key, location.get(key));
			}
		}
		String body = bodyStr;
		JsonPath jsonPath = JsonPath.with(body);
		
		String time = jsonPath.getString("updated_at");
		time = time.substring(time.indexOf("T")+1,time.lastIndexOf("+"));
		String update_time = time.substring(0,time.lastIndexOf(":"));
		
		checkResponse(expected);
		attendance.daily_statistic();
	}


	
	@Test(dataProvider="SingleCase",description="外勤打卡")
	public void attendance_Out_Test(Map<String, Object> params){
		setRequest("AttendanceOut", params);
		checkResponse(expectedMap);
		Attendance attendance = new Attendance(basePath);
		attendance.daily_statistic();
	}

	@Test(dataProvider = "SingleCase",description ="假期类型")
	public void LeaveType_Test(Map<String, Object> params){
		setRequest("LeaveType", params);
		checkResponse(expectedMap);

	}

	@Test(dataProvider = "SingleCase", description = "请假审批")
	public void LeaveRequest_Test(Map<String, Object> params){
		setRequest("LeaveRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "请假审批异常测试")
	public void LeaveRequestError_Test(Map<String, Object> params){
		setRequest("LeaveRequestError", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "加班审批")
	public void OvertimeRequest_Test(Map<String, Object> params){
		setRequest("OvertimeRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "补卡审批")
	public void ResignRequest_Test(Map<String, Object> params){
		setRequest("ResignRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "外出审批")
	public void OutRequest_Test(Map<String, Object> params){
		setRequest("OutRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "出差审批")
	public void TripRequest_Test(Map<String, Object> params){
		setRequest("TripRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "项目审批")
	public void ProjectRequest_Test(Map<String, Object> params){
		setRequest("ProjectRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "通用审批")
	public void GeneralRequest_Test(Map<String, Object> params){
		setRequest("GeneralRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "采购审批")
	public void PurchaseRequest_Test(Map<String, Object> params){
		setRequest("PurchaseRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "物品领用审批")
	public void MaterialgetRequest_Test(Map<String, Object> params){
		setRequest("MaterialgetRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "报销审批")
	public void ReimbursementRequest_Test(Map<String, Object> params){
		setRequest("ReimbursementRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "合同审批")
	public void ContractRequest_Test(Map<String, Object> params){
		setRequest("ContractRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "付款审批")
	public void PaymentRequest_Test(Map<String, Object> params){
		setRequest("PaymentRequest", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "设置班次")
	public void SetSchedule_Test(Map<String, Object> params){
		setRequest("SetSchedule", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "PC端考勤统计")
	public void AttendanceStatisticsPC_Test(Map<String, Object> params){
		setRequest("AttendanceStatisticsPC", params);
		checkResponse(expectedMap);
	}

	@Test(dataProvider = "SingleCase", description = "APP端考勤统计")
	public void AttendanceStatisticsAPP_Test(Map<String, Object> params){
		setRequest("AttendanceStatisticsAPP", params);
		checkResponse(expectedMap);
	}


	@Test(dataProvider = "SingleCase", description = "APP端考勤统计")
	public void AttendanceIn_Test(Map<String, Object> params){
		setRequest("AttendanceIn", params);
		checkResponse(expectedMap);
	}






}
