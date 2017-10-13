package com.qifei.apis.attendance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;

import com.qifei.apis.Attendance;
import com.qifei.utils.JsonUtils;
import com.qifei.utils.test.BaseTest;

import io.restassured.path.json.JsonPath;

public class LocationsTest extends BaseTest {
	
	@Test(dataProvider="SingleCase",description="新增打卡地点")
	public void add_Locations_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(getbasePath());
		attendance.addLocations(attendance.formatParams(params));
//		setRequest("addLocations", attendance.formatParams(params));
//		Map<String, Object> expected = getExpectedMap();
//		checkResponse(attendance.formatParams(expected));
	}
	
	@Test(dataProvider="SingleCase",description="更新打卡地点")
	public void modify_Locations_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(getbasePath());
		String uuid = null;
		List<Object> locations = attendance.getLocations();
		if(locations.size()==0){
			String body = attendance.addLocations(attendance.formatParams(params));
			JsonPath json = JsonPath.with(body);
			uuid = json.getString("uuid");
		}else{
			Random random = new Random();
			int index = random.nextInt(locations.size());
			uuid = locations.get(index).toString();
		}
		attendance.modifyLocations(uuid, attendance.formatParams(params));
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap = attendance.formatParams(params);
//		paramMap.put("uuid", uuid);
//		setRequest("modifyLocations", paramMap);
//		JsonPath response = JsonPath.with(getBodyStr());
//		JsonUtils jsonUtil = new JsonUtils();
//		jsonUtil.equalsJson(getExpectedMap(), response);
	}
}
