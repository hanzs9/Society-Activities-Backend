package com.yiibai.springmvc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class SortingHandler {
	
    //the threshold to clear the preferences 
	final double THRESHOLD = 0.1;
	
	private SQLHandler sqlHandler;
    
    public SortingHandler(SQLHandler sqlHandler) {
    	this.sqlHandler = sqlHandler;
    }
    
    public boolean setPreference(String preferences, String studentID) {
        return (sqlHandler.update("Student", "studentID", studentID, "preference", preferences));        
    }
    
    
    public void updatePreferenceByActivity(String updateType, String studentID, String activityID) {        
        try {        	
        	ResultSet resultSet = sqlHandler.query("Student", "preference", "studentID","=" ,studentID);        	
        	String result = "";
        	if(resultSet.next()) {
        	   result = resultSet.getString("preference");
        	}
	    	double[] preferences = stringConvertDouble(result);
			
	    	resultSet = sqlHandler.query("Activity", "type", "activityID","=", activityID);
	    	String result2 = "";
	    	if(resultSet.next()) {
	    	    result2 = resultSet.getString("type");
	    	}	    	
			int[] type = stringConvertInt(result2);
			
	    	switch(updateType) {
	          case "view":
	              update(preferences,type,1.1,0.9);
	          break;
	          case "apply":
	              update(preferences,type,1.5,0.7);        	  
	          break;
	          case "quit":
	        	  update(preferences,type,0.8,1);
	          break;
	          case "favorite":
	        	  update(preferences,type,1.2,0.9);
	          break;
	          case "cancelFavorite":
	        	  update(preferences,type,0.8,1);
	          break;  
	          default:
	            System.out.println("Wrong type for updates");
	        }
	    	
	    	String newPreferences = doubleConvertString(preferences);
	    	sqlHandler.update("Student", "studentID", studentID, "preference", newPreferences);
        } catch (SQLException e) {
			e.printStackTrace();
		}

    }
    
    public void updatePreferenceBySociety(String updateType, String studentID, String societyID) {
    	try {
    	ResultSet resultSet = sqlHandler.query("Student", "preference", "studentID", "=",studentID);        			
	    String result = "";
    	if(resultSet.next()) {
	    	result = resultSet.getString("preference");
	    }
    	double[] preferences = stringConvertDouble(result);
		
    	resultSet = sqlHandler.query("Society", "type", "societyID", "=",societyID);
    	String result2 = "";
    	if(resultSet.next()) {
    	    result2 = resultSet.getString("type");
    	}
    	int[] type = stringConvertInt(result2);
            
    	switch(updateType) {
    	    case "quit":
    	    	update(preferences,type,0.7,1);
    	    break;	
    	    case "join":	
    	    	update(preferences,type,1.3,0.8);
    	    break;
    	    default:
    	    System.out.println("Wrong input for updates");	
    	}
    	String newPreferences = doubleConvertString(preferences);
    	sqlHandler.update("Student", "studentID", studentID, "preference", newPreferences);
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public String[] getList(String studentID) {
    	String date = getDate();
    	
        String y1 = date.substring(0, 4);
    	String m1 = date.substring(5, 7);
    	String d1 = date.substring(8, 10);
    	int dayNumber = Integer.parseInt(y1)*365 + Integer.parseInt(m1)*30 + Integer.parseInt(d1);
    	
    	HashMap<String,Double> activityScore = new HashMap<String,Double>();
    	try {
    		ResultSet rs;
    		String result = "";
    		rs = sqlHandler.query("Student", "preference", "studentID", "=", studentID);        			
    	    if(rs.next()) {
    		     result = rs.getString("preference");
    	    }
    	    double[] preferences = stringConvertDouble(result);
        	
    		rs= sqlHandler.query("Activity","*", "time", ">",date);    	
			while(rs.next()) {
				String activityID = rs.getString("activityID");
				
				String date2 = rs.getString("time");
		        String y2 = date2.substring(0, 4);
		    	String m2 = date2.substring(5, 7);
		    	String d2 = date2.substring(8, 10);
		    	int dayNumber2 = Integer.parseInt(y2)*365 + Integer.parseInt(m2)*30 + Integer.parseInt(d2);			
				
				int dayDistance = dayNumber2 - dayNumber;
		    					
				String type = rs.getString("type");
				int[] types = stringConvertInt(type);
				double mark = getMark(preferences,types,dayDistance);
				activityScore.put(activityID,mark);
			}
			
			String[] keys = new String[activityScore.size()];
			Object[] temp = activityScore.keySet().toArray();
			
			for(int i = 0; i < activityScore.size();i++) {
				keys[i]  = (String)temp[i];
			}
				
			for(int i = 0; i< activityScore.size()-1;i++) {
				for(int j = 0; j<activityScore.size()-i-1;j++) {
					if(activityScore.get(keys[j])<activityScore.get(keys[j+1])) {
						exchange(keys,j,j+1);
					}
				}
			}
			keys = hybrid(keys);
			return keys;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    
    private double getMark(double[] preferences, int[] type,int dayDistance) {
    	double mark = 0;
    	for(int i = 0;i<preferences.length;i++) {
    		if(type[i] == 1) {
    			mark += preferences[i];
    		}
    	}
    	mark = mark * (3.5/(dayDistance+1));
    	return mark;
    }
    
    
    //update the preferences when applying for activities, 
    //applying for societies and quitting society
    private void update(double[] preferences, int[] type, double parameter1, double parameter2) {    	
    	for(int i = 0;  i<preferences.length; i++) {
    		if(type[i] == 1) {
    			preferences[i] = modifyResult((preferences[i])*parameter1 + (parameter1-1)*2);    			
    		    if(preferences[i] < 0) {
    		    	preferences[i] = 0;
    		    }
    		    if(preferences[i]>1000) {
    		    	preferences[i] = 1000;
    		    }
    		}
    		if(type[i] == 0) {
    			preferences[i] = modifyResult((preferences[i])*parameter2);
    		}
    	}
    	clearPreference(preferences);
    }
    

    
    //set the elements in the preferences to zero if it is less than the threshold
    private void clearPreference(double[] preferences) {
    	for(int i = 0; i <preferences.length; i++) {
    		if(preferences[i] <= THRESHOLD) {
    			preferences[i] = 0;
    		}
    	}
    }
    
    private int[] stringConvertInt(String string) {
        int[] intArr;
    	if(string.equals("")) {
    		return null;
    	}else {
    		String[] stringArr = string.split(",");
    	    intArr = new int[stringArr.length];
    	    for(int i = 0;i<stringArr.length;i++) {
    	    	intArr[i] = Integer.parseInt(stringArr[i]);
    	    }
    	  return intArr;
        }
    }
    
    private double[] stringConvertDouble(String string) {
        double[] doubleArr;
    	if(string.equals("")) {
    		return null;
    	}else {
    		String[] stringArr = string.split(",");
    	    doubleArr = new double[stringArr.length];
    	    for(int i = 0;i<stringArr.length;i++) {
    	    	doubleArr[i] = Double.parseDouble(stringArr[i]);
    	    }
    	  return doubleArr;
        }
    }
    
    private String doubleConvertString(double[] doubleArr) {
    	StringBuffer stringBuffer = new StringBuffer();
    	for(int i = 0;i<doubleArr.length;i++) {
    		stringBuffer.append(doubleArr[i]);
    		if(i != doubleArr.length-1) {
    			stringBuffer.append(",");
    		}
    	}
    	return stringBuffer.toString();
    }
    
    private double modifyResult(double number) {
    	BigDecimal bg = new BigDecimal(number);
    	double newNumber = bg.setScale(3,BigDecimal.ROUND_HALF_DOWN).doubleValue();
    	return newNumber;
    }
    
    private String getDate() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = new Date();
    	String result = format.format(date);
    	return result;
    }
    
    private void exchange(String[] list,int i,int j) {
		String temp = list[i];
		list[i] = list[j];
		list[j] = temp;
    }
    
    private String[] hybrid(String[] list) {
    	int length = list.length;
    	if(list.length <= 4) {
    		return list;
    	}
    	String[] newList = Arrays.copyOf(list, list.length);
    	for(int i=0; i< length/4 ; i++) {
    		for(int j = 0; j < 4; j++) {
    			if(j == 3) {
    				exchange(newList,j+4*i,length-1-i);
    			}
    		}
    	}
    	return newList;
    }
}