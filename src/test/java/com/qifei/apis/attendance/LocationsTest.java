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
	public void Add_Locations_Test(Map<String, Object> params){
		Attendance attendance = new Attendance(getbasePath());
		setRequest("locations", attendance.formatParams(params));
		JsonPath response = JsonPath.with(getBodyStr());
		JsonUtils jsonUtil = new JsonUtils();
		jsonUtil.equalsJson(getExpectedMap(), response);
	}
	
	@Test(dataProvider="SingleCase",description="更新打卡地点")
	public void Modify_Locations_Test(Map<String, Object> params){
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
		Map<String, Object> paramMap = new HashMap<>();
		paramMap = attendance.formatParams(params);
		paramMap.put("uuid", uuid);
		attendance.modifyLocations(uuid, paramMap);
		setRequest("locations", paramMap);
		JsonPath response = JsonPath.with(getBodyStr());
		JsonUtils jsonUtil = new JsonUtils();
		jsonUtil.equalsJson(getExpectedMap(), response);
	}
}
