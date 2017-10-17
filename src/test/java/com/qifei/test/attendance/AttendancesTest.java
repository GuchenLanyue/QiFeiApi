package com.qifei.apis.attendance;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.qifei.apis.Attendance;
import com.qifei.utils.test.BaseTest;

public class AttendancesTest extends BaseTest {
	
	@Test(dataProvider="SingleCase",description="内勤打卡")
	public void attendance_In_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(getbasePath());
		String uuid = attendance.getUUID();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("location_id", uuid);
		paramMap.put("type", Integer.parseInt(params.get("type").toString()));
		
		setRequest("in", paramMap);
		Map<String, Object> location = attendance.getLocation(uuid);
		checkResponse(location);
	}
	
	public void attendance_Out_Test(){
		
	}
}
