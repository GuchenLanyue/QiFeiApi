package com.qifei.utils.http;

import java.io.File;

import com.qifei.apis.Auth;
import com.qifei.utils.TxtData;

public class Headers {
	
	public Headers() {
		// TODO Auto-generated constructor stub
	}
	
	public Headers(String basePath) {
		// TODO Auto-generated constructor stub
		this.basePath = basePath;
	}
	
	private String basePath = null;
	private String tokenFile = System.getProperty("user.dir")+"\\sources\\config\\access_token.txt";

	public String getAuthorization(){
		File file = new File(tokenFile);
		if(!file.exists()){
			Auth auth = new Auth(basePath);
			return auth.tokens();
		}else{
			TxtData txt = new TxtData();
			return txt.readTxtFile(tokenFile);
		}
	}
}
