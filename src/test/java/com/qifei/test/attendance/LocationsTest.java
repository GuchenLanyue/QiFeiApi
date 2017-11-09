package com.qifei.test.attendance;

import java.util.List;
import java.util.Map;


import com.qifei.apis.Attendance;
import org.testng.annotations.Test;

import com.qifei.utils.test.BaseTest;

public class LocationsTest extends BaseTest {
	
	@Test(dataProvider="SingleCase",description="新增打卡地点")
	public void add_Locations_Test(Map<String, Object> params){
		setRequest("addLocations", params);
		Map<String, Object> expected = expectedMap;
		checkResponse(expected);
	}
	
	@Test(dataProvider="SingleCase",description="更新打卡地点")
	public void modify_Locations_Test(Map<String, Object> params){
		setRequest("modifyLocations", params);
		Map<String, Object> expected = expectedMap;
		checkResponse(expected);
	}
	
	@Test(dataProvider="SingleCase",description="删除打卡地点")
	public void del_Locations_Test(Map<String, Object> params){
//		Attendance attendance = new Attendance(getbasePath());
//		String uuid = attendance.getLocationID();
//		attendance.deleteLocations(uuid);
		setRequest("deleteLocations", params);
	}

	@Test
	public void testName() throws Exception {
	}

	@Test(description="删除打卡地点")
	public void clearData_Test(Map<String, Object> params) {
		Attendance attendance = new Attendance(basePath);
		List<String> schedules = attendance.getScheduleIDs();
		for(String id:schedules){
			attendance.deleteSchedules(id);
		}

//		String uuid = attendance.getLocationID();
//		attendance.deleteLocations(uuid);

	}

}
