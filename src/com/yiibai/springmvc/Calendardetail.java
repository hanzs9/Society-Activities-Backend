package com.yiibai.springmvc;

public class Calendardetail {

	private String studentID;
	private String password;
	
	private String date;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStudentID() {
		return studentID;
	}

	public String getPassword() {
		return password;
	}

	public void setStudentID(String account) {
		this.studentID = account;
	} 

	public void setIPassword(String password) {
		this.password = password;
	}    
}