package com.qifei.apis.attendance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;

import com.qifei.apis.Attendance;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;

public class LocationsTest extends BaseTest {
	
	@Test(dataProvider="SingleCase",description="新增打卡地点")
	public void add_Locations_Test(Map<String, Object> params){
//		Attendance attendance = new Attendance(getbasePath());
		setRequest("addLocations", params);
		Map<String, Object> expected = getExpectedMap();
		checkResponse(expected);
	}
	
	@Test(dataProvider="SingleCase",description="更新打卡地点")
	public void modify_Locations_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(getbasePath());
		List<Map<String, Object>> locations = attendance.getLocations();
		String uuid = null;
		if(locations.size()==0){
			String body = attendance.addLocations(params);
			JsonPath json = JsonPath.with(body);
			uuid = json.getString("uuid");
		}else{
			Random random = new Random();
			int index = random.nextInt(locations.size());
			Map<String, Object> locationMap = new HashMap<>();
			locationMap = locations.get(index);
			uuid = locationMap.get("uuid").toString();
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = attendance.formatParams(params);
		paramMap.put("uuid", uuid);
		setRequest("modifyLocations", paramMap);
		Map<String, Object> expected = getExpectedMap();
		checkResponse(attendance.formatParams(expected));
	}
	
	@Test(description="删除打卡地点")
	public void del_Locations_Test(){
		Attendance attendance = new Attendance(getbasePath());
		List<Map<String, Object>> locations = attendance.getLocations();
		String uuid = null;
		if(locations.size()==0){
			String body = attendance.addLocations();
			JsonPath json = JsonPath.with(body);
			uuid = json.getString("uuid");
		}else{
			Random random = new Random();
			int index = random.nextInt(locations.size());
			Map<String, Object> locationMap = new HashMap<>();
			locationMap = locations.get(index);
			uuid = locationMap.get("uuid").toString();
		}
		
		attendance.deleteLocations(uuid);
	}
}
