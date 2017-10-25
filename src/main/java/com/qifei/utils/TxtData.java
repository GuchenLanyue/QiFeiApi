package com.qifei.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.testng.Assert;

public class TxtData {

	/**
	 * 写文件
	 *
	 * @param path
	 *            文件的路径
	 * @param content
	 *            写入文件的内容
	 */
	public void writerText(String fileName, String content) {
		File f = new File(fileName);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		
		if (f.exists()) {
			f.delete();
		}
		
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// new FileWriter(path + "t.txt", true) 这里加入true 可以不覆盖原有TXT文件内容 续写
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			BufferedWriter bw1 = new BufferedWriter(write);
			bw1.write(content);
			bw1.flush();
			bw1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
	 * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
	 * 
	 * @param filePath
	 */
	public String readTxtFile(String filePath) {

		String lineTxt = "";
		String str = "";
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);

			if (file.isFile() && file.exists()) { // 判断文件是否存在

				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式

				BufferedReader bufferedReader = new BufferedReader(read);

				while ((lineTxt = bufferedReader.readLine()) != null) {
					str += lineTxt + "\r\n";
				}

				read.close();
				bufferedReader.close();
			}
		} catch (Exception e) {
			Assert.fail("读取文件内容出错");
			e.printStackTrace();
		}
		
		return str.substring(0, str.length()-2);
	}
}
