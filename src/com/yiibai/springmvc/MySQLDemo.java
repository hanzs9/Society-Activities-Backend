package com.yiibai.springmvc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLDemo {

	public static void main(String[] args) {
		SQLHandler sqlHandler = new SQLHandler();
		ArrayList<String> newActivity = new ArrayList<>();
		
		String activityID = sqlHandler.getMaxId("Activity", "activityID");
		int actID = Integer.parseInt(activityID);
		actID = actID + 1;
		String newActivityID = actID + "";
		
		newActivity.add(newActivityID);
		newActivity.add("aid outfit introduction");
		newActivity.add("0,1,1,0,0,0,0,0,0,0,0,0,0,0,1");
		newActivity.add("Almost everyone will need to use a first aid kit at some time. Make time to prepare home and travel kits for your family’s safety.");
		newActivity.add("Guild of student");
		newActivity.add("2019-05-26 19:00:00");
		newActivity.add("100");
		newActivity.add("11120");
		newActivity.add("http://60.205.225.26:8080/pic/medicineintroduction.jpg");
		sqlHandler.insert("Activity", newActivity);

//		String societyID = sqlHandler.getMaxId("Society", "societyID");
//		int actID = Integer.parseInt(societyID);
//		actID = actID + 1;
//		String newsocietyID = actID + "";
//
//		newActivity.add(newsocietyID);
//		newActivity.add("24 FESTIVAL DRUMS");
//		newActivity.add("0,0,0,0,0,0,1,1,0,0,0,0,0,0,1");
//		newActivity.add("We are a group of drummers who perform a unique drumming style from Malaysia. The society was founded by a Malaysian drummer and has been going strong as the only group in Europe for 4 years from March 2011.");
//		newActivity.add("http://60.205.225.26:8080/pic/24festivaldrumssociety.jpg");
//		sqlHandler.insert("Society", newActivity);

	}


}

//String activityID = sqlHandler.getMaxId("Activity", "activityID");
//int actID = Integer.parseInt(activityID);
//actID = actID + 1;
//String newActivityID = actID + "";
//
//newActivity.add(newActivityID);
//newActivity.add("handball Match");
//newActivity.add("1,0,0,1,0,0,1,0,0,0,0,1,0,0,1");
//newActivity.add("handball match for year 1 and year 2 students ");
//newActivity.add("Univerty Sports Centre");
//newActivity.add("2019-05-10 14:10:00");
//newActivity.add("100");
//newActivity.add("11111");
//newActivity.add("http://60.205.225.26:8080/pic/handball.jpg");
//sqlHandler.insert("Activity", newActivity);

