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
		Attendance attendance = new Attendance(getbasePath());
		String uuid = attendance.getLocationID();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = params;
		paramMap.put("location_id", uuid);
//		paramMap.put("type", Integer.parseInt(params.get("type").toString()));
		
		setRequest("AttendanceIn", paramMap);
		Map<String, Object> location = attendance.getLocation(uuid);
		Map<String, Object> expected = getExpectedMap();
		for(String key:expected.keySet()){
			if(location.containsKey(key)){
				expected.put(key, location.get(key));
			}
		}
		String body = getBodyStr();
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
		checkResponse(getExpectedMap());
		Attendance attendance = new Attendance(getbasePath());
		attendance.daily_statistic();
	}
}
