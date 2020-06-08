package com.yiibai.springmvc;

public class UpdateInfo {
    private String StudentID;
    private String ActivityID;
	private String SocietyID;
    
    public String getStudentID() {
		return StudentID;
	}
	
    public void setStudentID(String studentID) {
		StudentID = studentID;
	}
	
    public String getActivityID() {
		return ActivityID;
	}
	
    public void setActivityID(String activityID) {
		ActivityID = activityID;
	}
    
    public String getSocietyID() {
    	return SocietyID;
    }
    
    public void setSocietyID(String societyID) {
    	SocietyID = societyID;
    }
}
