package com.yiibai.springmvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler {
	public Date convertStringtoDate(String str) {
		Date date = null;
		String timePattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Convert failed!");
			e.printStackTrace();
		}
		return date;
	}
}
