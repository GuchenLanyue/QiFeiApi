package com.qifei.apis;

public class Path {
	public Path() {
		// TODO Auto-generated constructor stub
	}
	
	public Path(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	
	enum PathParam{
		LocationID
	}
	
	public String analysisPath(String pathStr){
		PathParam pathEnum = null;
		pathEnum = Enum.valueOf(PathParam.class, pathStr);
		String path = null;
		switch (pathEnum) {
		case LocationID:
			Attendance attendance = new Attendance(basePath);
			path = attendance.getLocationID();
			
			break;
		default:
			break;
		}
		
		return path;
	}
}
