package com.qifei.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qifei.utils.http.Headers;
import com.qifei.utils.http.HttpMethods;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Organization {
	
	public Organization() {
		// TODO Auto-generated constructor stub
	}
	
	public Organization(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	
	//获取组织结构
	public String getOrganizations(String parent_organization_ID){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/formrecords?form=organization&status=created&parent_organization_ID="+parent_organization_ID+"");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return http.getBody(response);
	}
	
	//获取子部门详情
	public String getChildOrganization(String organization_ID){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/simple-employees?status=active&organization_ID="+organization_ID+"&limit=100&offset=0");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		return http.getBody(response);
	}
	
	//获取岗位列表
	public List<String> getPositions(String organization_ID){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/formrecords?form=position");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "GET");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		Response response = http.request(map);
		
		String body = http.getBody(response);
		JsonPath json = JsonPath.with(body);
		List<String> list = json.getList("items.organization_ID");
		List<String> positions = new ArrayList<>();
		
		String uuid = null;
		for(int i=0;i<list.size();i++){
			if(list.get(i).equals(organization_ID)){
				uuid = json.getString("items["+i+"].uuid");
				positions.add(uuid);
			}
		}
		
		return positions;
	}
	
	//删除岗位
	public void deletePosition(String position_ID){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/formrecords/"+position_ID+"?form=position");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "DELETE");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}
	
	//调入调出
	public void transfer_out(Map<String, Object> params){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/employees/op/transfer");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "POST");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		map.put("params", params);
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}
	
	//删除部门
	public void deleteOrganization(String organization_ID){
		Map<String, Object> baseMap = new HashMap<>();
		baseMap.put("BasePath", baseMap);
		baseMap.put("Path", "/hr/v1/formrecords/"+organization_ID+"?form=organization");
		baseMap.put("contentType", "application/json");
		baseMap.put("Method", "DELETE");
		//设置Authorization
		String authorization = new Headers(basePath).getAuthorization();
		Map<String, Object> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		
		Map<String, Map<String,Object>> map = new HashMap<>();
		map.put("base", baseMap);
		map.put("headers", headerMap);
		
		//发起请求
		HttpMethods http = new HttpMethods(basePath);
		http.request(map);
	}
	
	//删除部门
	public void deleteOrganization(String parent_organization_ID,String organization_Name) {
		//获取所有子部门
		String body = getOrganizations(parent_organization_ID);
		JsonPath json = JsonPath.with(body);
		//获取所有子部门名字
		List<Object> organizations = json.getList("items.name");
		boolean isCreated = false;
		int index = 0;
		for(int i=0;i<organizations.size();i++){
			String name = organizations.get(i).toString();
			if(name.equals(organization_Name)){
				isCreated = true;
				index = i;
				break;
			}
		}
		//判断部门是否已经被创建
		if(!isCreated){
			return;
		}
		//获取id
		String uuid = json.getString("items["+index+"].uuid");
		body = getChildOrganization(uuid);
		json = JsonPath.with(body);
		//获取所有成员
		List<String> tenants = json.getList("items.uuid");
		int[] keys = new int[tenants.size()];
		for(int i=0;i<keys.length;i++){
			keys[i] = i;
		}
		//调出所有成员
		if(keys.length!=0){
			Map<String, Object> params = new HashMap<>();
			params.put("keys", keys);
			params.put("opt", "transfer_out");
			params.put("organization_ID", uuid);
			params.put("to_org_id", "b89d7d04-0f75-11e7-9aa4-00163e007053");
			params.put("employee_ids", tenants);
			
			transfer_out(params);
		}
		
		//获取所有岗位
		List<String> positions = getPositions(uuid);
		//删除所有岗位
		for(String position_ID:positions){
			deletePosition(position_ID);
		}
		
		deleteOrganization(uuid);
	}
	
	public static void main(String[] args) {
		Organization organization = new Organization("http://console.t.upvi.com/bapi");
		organization.deleteOrganization("b89d7d04-0f75-11e7-9aa4-00163e007053", "流程1");
		organization.deleteOrganization("b89d7d04-0f75-11e7-9aa4-00163e007053", "流程2");
	}
}
