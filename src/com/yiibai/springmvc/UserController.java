package com.yiibai.springmvc;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sun.org.apache.regexp.internal.recompile;



import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Controller
public class UserController {
	@RequestMapping("/upload")
	public String upload() {
		return "upload";
	}
	
	SQLHandler sqlHandler = new SQLHandler();
	SortingHandler sortingHandler = new SortingHandler(sqlHandler);
	
	@RequestMapping("/login")
	public @ResponseBody Student login(@RequestBody Login login) {
		
		Student stu = new Student();
		try {
			ResultSet rs = sqlHandler.query("Student", "*", "studentID","=", login.getStudentID());
			if(rs.next()) {
				if(login.getPassword().equals(rs.getString("password"))){					
					stu.setStudentID(rs.getString("studentID"));
					stu.setGender(rs.getString("gender"));
					stu.setFirstname(rs.getString("firstname"));
					stu.setSurname(rs.getString("surname"));
					stu.setProgramme(rs.getString("programme"));
					stu.setEmail(rs.getString("email"));
					stu.setPreference(rs.getString("preference"));
					stu.setAccount(rs.getString("account"));
					stu.setPassword(rs.getString("password"));
					return stu;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stu;
	}

	@RequestMapping("/loginAndAct")
	public @ResponseBody LoginInformation loginAndAct(@RequestBody Login login) {
		
		LoginInformation stu = new LoginInformation();
		try {
			ResultSet rs = sqlHandler.query("Student", "*", "studentID","=", login.getStudentID());
			if(rs.next()) {
				if(login.getPassword().equals(rs.getString("password"))){
					Date date = new Date();
					DateHandler dh = new DateHandler();
					Date Nearest = new Date();
					Calendar c = Calendar.getInstance();
			        c.setTime(date);
			        c.add(Calendar.DAY_OF_MONTH, 1);// 浠婂ぉ+1澶�
			        String actName="";
			        String time="";
			        String location="";
			        
			        Date tomorrow = c.getTime();
					ResultSet rs1 = sqlHandler.query("Participator", "*", "studentID","=", login.getStudentID());
					while(rs1.next()) {
						ResultSet rs2 = sqlHandler.query("Activity", "*", "activityID","=", rs1.getString("activityID"));
						if(rs2.next()) {
							Date activitytime = dh.convertStringtoDate(rs2.getString("time"));
							
							if(activitytime.after(date) && activitytime.before(tomorrow)) {
								tomorrow = activitytime;
								actName = rs2.getString("name");
								time = rs2.getString("time");
								location = rs2.getString("location");
							}
						}
					}
			
					stu.setActivityName(actName);
					stu.setTime(time);
					stu.setLocation(location);
			         
					stu.setStudentID(rs.getString("studentID"));
					stu.setGender(rs.getString("gender"));
					stu.setFirstname(rs.getString("firstname"));
					stu.setSurname(rs.getString("surname"));
					stu.setProgramme(rs.getString("programme"));
					stu.setEmail(rs.getString("email"));
					stu.setPreference(rs.getString("preference"));
					stu.setAccount(rs.getString("account"));
					stu.setPassword(rs.getString("password"));
					return stu;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stu;
	}
	
//	+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	@RequestMapping("/ApplyActivity")
	public @ResponseBody SuccessMessage ApplyActivity(@RequestBody UpdateInfo updateInfo) {
		SuccessMessage message = new SuccessMessage();
		ArrayList<String> values = new ArrayList<String>(); 
		String activityID = updateInfo.getActivityID();
		String studentID = updateInfo.getStudentID();
		String parID = studentID + activityID;
		values.add(parID);
		values.add(activityID);
		values.add(studentID);
		boolean result = sqlHandler.insert("Participator",values);
		if(result) {
			message.setMessage("success");
			sortingHandler.updatePreferenceByActivity("apply", studentID, activityID);
		}else {
			message.setMessage("fail");
		}

		return message;
	} @RequestMapping("/QuitActivity")
	public @ResponseBody SuccessMessage QuitActivity(@RequestBody UpdateInfo updateInfo) {
		SuccessMessage message = new SuccessMessage();
		String activityID = updateInfo.getActivityID();
		String studentID = updateInfo.getStudentID();
		String parID = studentID+activityID;
		boolean result = sqlHandler.delete("Participator", "participatorID",parID);
		if(result) {
			message.setMessage("success");
			sortingHandler.updatePreferenceByActivity("quit", studentID, activityID);
		}else {
			message.setMessage("fail");
		}
		return message;
	}
	@RequestMapping("/RequestSociety")
	public @ResponseBody SocietyInformation RequestSociety(){
		SocietyInformation societyInfo = new SocietyInformation();
		ResultSet rs = sqlHandler.query("Society", "*", "societyID", "!=", "0");
		ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
		try {
			while(rs.next()) {
				HashMap<String,String> map2 = new HashMap<String,String>();
				String societyID = rs.getString("societyID");
				map2.put("societyID",societyID);
				String name = rs.getString("name");
				map2.put("name",name);
				String type = rs.getString("type");
				map2.put("type",type);
				String description = rs.getString("description");
				map2.put("description",description);
				String poster = rs.getString("poster");
				map2.put("poster",poster);
				map.add(map2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		societyInfo.setMap(map);
		return societyInfo;
	}
	
	@RequestMapping("/SocietyActivityList")
	public @ResponseBody ActivityInformation SocietyActivityList(@RequestBody UpdateInfo updateInfo) {
	    ActivityInformation activityInfo = new ActivityInformation();
	    String studentID = updateInfo.getStudentID();
	    String societyID = updateInfo.getSocietyID();
	    ResultSet rs = sqlHandler.query("Activity", "*", "societyID", "=", societyID);
	    ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	    try {
	  while(rs.next()) {
	   HashMap<String,String> map2 = new HashMap<String, String>();  
	   String activityID = rs.getString("activityID");
	   map2.put("activityID",activityID);
	   String name = rs.getString("name");
	   map2.put("name",name);
	   String type = rs.getString("type");
	   map2.put("type",type);
	   String description  = rs.getString("description");
	   map2.put("description",description);
	   String location = rs.getString("location");
	   map2.put("location",location);
	   String time = rs.getString("time").substring(0, 19);
	   map2.put("time",time);
	   String participatorNum = rs.getString("participatorNum");
	   map2.put("participatorNum",participatorNum);
	   map2.put("societyID",societyID);
	   String poster = rs.getString("poster");
	   map2.put("poster",poster);
	   
	   ResultSet rs2 = sqlHandler.query("ActivityFavorites", "*", "studentID", "=", studentID);
	   boolean flag = false;
	   while(rs2.next()) {
	    if(rs2.getString("activityID").equals(activityID)) {
	     flag = true;
	    }
	   }
	   if(flag) {
	    map2.put("favorite","1");
	   }else {
	    map2.put("favorite","0");
	   }
	   
	   map.add(map2);
	  }
	 } catch (SQLException e) {
	  e.printStackTrace();
	 }
	    activityInfo.setMap(map);    
	    return activityInfo;
	}
	
	@RequestMapping("/ApplySociety")
	public @ResponseBody SuccessMessage ApplySociety(@RequestBody UpdateInfo updateInfo) {
		SuccessMessage message = new SuccessMessage();
		String studentID = updateInfo.getStudentID();
		String societyID = updateInfo.getSocietyID();
		String memberID = studentID + societyID;
		ArrayList<String> values = new ArrayList<String>(); 
		values.add(memberID);
		values.add(societyID);
		values.add("0");
		values.add(studentID);	
		boolean result = sqlHandler.insert("ApplicationForSociety", values);
		if(result) {
			message.setMessage("success");
			sortingHandler.updatePreferenceBySociety("join", studentID, societyID);
		}else {
			message.setMessage("fail");  
		}
		return  message;
	}
	@RequestMapping("/QuitSociety")
	public @ResponseBody SuccessMessage QuitSociety(@RequestBody UpdateInfo updateInfo) {
		SuccessMessage message = new SuccessMessage();
		String studentID = updateInfo.getStudentID();
		String societyID = updateInfo.getSocietyID();
		String memberID = studentID + societyID;
		boolean result  = sqlHandler.delete("SocietyMember", "memberID", memberID);
		if(result) {
			message.setMessage("success");
			sortingHandler.updatePreferenceBySociety("quit", studentID, societyID); 
			sqlHandler.delete("ApplicationForSociety", "applicationID", (studentID+societyID));
		}else {
			message.setMessage("fail");
		} 
		return  message;
	}
	
	@RequestMapping("/RequestSocietyApplication")
	public @ResponseBody SocietyInformation RequestSocietyApplication(@RequestBody UpdateInfo updateInfo){
	 String id = updateInfo.getStudentID();
	 ResultSet rs = sqlHandler.query("ApplicationForSociety", "*", "studentID", "=", id);
	 ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	 try {
	  while(rs.next()) {
	         String state = rs.getString("state");
	         HashMap<String,String> map2 = new HashMap<String,String>();
	         map2.put("state",state);
	         ResultSet rs2 = sqlHandler.query("Society","*","societyID","=",rs.getString("societyID"));
	      if(rs2.next()) {
	       String name = rs2.getString("name");
	       map2.put("name",name);
	       String poster = rs2.getString("poster");
	       map2.put("poster",poster);      
	      }
	      map.add(map2);
	  }
	 }catch(SQLException e) {
	  e.printStackTrace();
	 }
	 SocietyInformation sInfo = new SocietyInformation();
	 sInfo.setMap(map);
	 return sInfo;
	}

	@RequestMapping("/FavoritesList")
	public @ResponseBody ActivityInformation FavoritesList(@RequestBody Student student){
	 String studentID = student.getStudentID();
	 ResultSet rs = sqlHandler.query("ActivityFavorites", "activityID", "studentID", "=", studentID);
	 ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	 try {
	  while(rs.next()) {
	   String activityID = rs.getString("activityID");
	   HashMap<String,String> map2 = new HashMap<String,String>();
	   ResultSet rs2 = sqlHandler.query("Activity", "*", "activityID", "=", activityID);
	   if(rs2.next()) {
	    map2.put("activityID",activityID);
	    String name = rs2.getString("name");
	    map2.put("name",name);
	    String type = rs2.getString("type");
	    map2.put("type",type);
	    String description  = rs2.getString("description");
	    map2.put("description",description);
	    String location = rs2.getString("location");
	    map2.put("location",location);
	    String time = rs2.getString("time").substring(0, 19);
	    map2.put("time",time);
	    String participatorNum = rs2.getString("participatorNum");
	    map2.put("participatorNum",participatorNum);
	    String  societyID = rs2.getString("societyID");
	    map2.put("societyID",societyID);
	    String poster = rs2.getString("poster");
	    map2.put("poster",poster); 
	    map.add(map2);
	   }
	  }
	 } catch (SQLException e) {
	  e.printStackTrace();
	 }
	 ActivityInformation activityInfo = new ActivityInformation();
	 activityInfo.setMap(map);
	 return activityInfo;
	}
	
@RequestMapping("/viewActivity")
public @ResponseBody SuccessMessage viewActivity(@RequestBody UpdateInfo updateInfo){
	SuccessMessage message = new SuccessMessage();
	sortingHandler.updatePreferenceByActivity("view", updateInfo.getStudentID(), updateInfo.getActivityID());
    message.setMessage("success");
	return message; 
}

@RequestMapping("/RequestList")
public @ResponseBody ActivityInformation RequestList(@RequestBody Student student){
    String id = student.getStudentID();
    String[] resultList = sortingHandler.getList(id);
    ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
    for(int i = 0; i < resultList.length; i++) {
     ResultSet rs = sqlHandler.query("Activity", "*", "activityID", "=", resultList[i]);
     HashMap<String,String> map2 = new HashMap<String,String>();
     try {
      map2.put("activityID",resultList[i]);
   while(rs.next()) {
    String name = rs.getString("name");
    map2.put("name",name);
    String type = rs.getString("type");
    map2.put("type",type);
    String description  = rs.getString("description");
    map2.put("description",description);
    String location = rs.getString("location");
    map2.put("location",location);
    String time = rs.getString("time").substring(0, 19);
    map2.put("time",time);
    String participatorNum = rs.getString("participatorNum");
    map2.put("participatorNum",participatorNum);
    String  societyID = rs.getString("societyID");
    map2.put("societyID",societyID);
    String poster = rs.getString("poster");
    map2.put("poster",poster);       
   }
   ResultSet rs2 = sqlHandler.query("ActivityFavorites", "*", "studentID", "=", id);
   boolean flag = false;
   while(rs2.next()) {
    if(rs2.getString("activityID").equals(resultList[i])) {
     flag = true;
    }
   }
   if(flag) {
    map2.put("favorite","1");
   }else {
    map2.put("favorite","0");
   }
      map.add(map2);
  } catch (SQLException e) {
   e.printStackTrace();
  }
    }
    ActivityInformation activityInfo = new ActivityInformation();
    activityInfo.setMap(map);
    return activityInfo;    
}

@RequestMapping("/SetFavorites")
public @ResponseBody SuccessMessage SetFavorites(@RequestBody UpdateInfo updateInfo) {
 String studentID = updateInfo.getStudentID();
 String activityID = updateInfo.getActivityID();
 String favoritesID = sqlHandler.getMaxId("ActivityFavorites", "favoritesID");
 favoritesID = (Integer.parseInt(favoritesID)+1) + "";
 ArrayList<String> value = new ArrayList<String>();
 value.add(favoritesID);
 value.add(studentID);
 value.add(activityID);
 boolean result = sqlHandler.insert("ActivityFavorites", value);
 SuccessMessage message = new SuccessMessage();
 if(result) {
  message.setMessage("success");
  sortingHandler.updatePreferenceByActivity("favorite", studentID, activityID);
 }else {
  message.setMessage("fail");
 }
 
 return message;
}

@RequestMapping("/CancelFavorites")
public @ResponseBody SuccessMessage CancelFavorites(@RequestBody UpdateInfo updateInfo) {
 String studentID = updateInfo.getStudentID();
 String activityID = updateInfo.getActivityID();
 String favoritesID = "";
  boolean result = false;
 ResultSet rs = sqlHandler.query("ActivityFavorites", "*", "studentID", "=", studentID);
 try {
  while(rs.next()) {
   if(rs.getString("activityID").equals(activityID)) {
    favoritesID  = rs.getString("favoritesID");
   }
  }
     result = sqlHandler.delete("ActivityFavorites", "favoritesID", favoritesID);  
 } catch (SQLException e) {
  e.printStackTrace();
 }   
 
 SuccessMessage message = new SuccessMessage();
 if(result) {
  message.setMessage("success");
  sortingHandler.updatePreferenceByActivity("cancelFavorite", studentID, activityID);
 }else {
  message.setMessage("fail");
 } 
 return message;
}

//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

/** administrator submits new activity
 * 
 * @param pic and information
 * @param request
 * @throws IOException
 */
@RequestMapping("/submitActivity")
public void submitActivity(@RequestParam(value = "pic", required = false) MultipartFile pic,HttpServletRequest request) throws IOException {
	
	String studentID = request.getParameter("studentID");
	String name = request.getParameter("name");
	String location = request.getParameter("location");
	String participatorNum = request.getParameter("capacity");
	String description = request.getParameter("description");
	String time = request.getParameter("time");
	String type = request.getParameter("typeArray");
	
	//Get society id
	String societyID = "";
		try {
			ResultSet rs = sqlHandler.query("Administrator", "*", "studentID","=", studentID);
			if(rs.next()) {
			societyID = rs.getString("societyID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	String activityID = sqlHandler.getMaxId("Activity", "activityID");
	int actID = Integer.parseInt(activityID);
	actID = actID + 1;
	String newActivityID = actID + "";
	
  String originalFileName = pic.getOriginalFilename();
  System.out.println("1111111");
  if(pic!=null && originalFileName!=null && originalFileName.length()>0){
	//String pic_path = "/Users/sonny/Documents/workspace/image/";
	String pic_path = "/www/image/";
    String newFileName = UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
    File newFile = new File(pic_path+newFileName);
    pic.transferTo(newFile);
    
    ArrayList<String> newActivity = new ArrayList<>();
    newActivity.add(newActivityID);
    newActivity.add(name);
    newActivity.add(type);
    newActivity.add(description);
    newActivity.add(location);
    newActivity.add(time);
    newActivity.add(participatorNum);
    newActivity.add(societyID);
    newActivity.add("http://60.205.225.26:8080/pic/" + newFileName);
    sqlHandler.insert("Activity", newActivity);
  }
 }

/** administrator check society member
 * 
 * @param login
 * @return member name
 */
@RequestMapping("/RequestSocietyMember")
public @ResponseBody SocietyInformation RequestSocietyMember(@RequestBody Login login){
	SocietyInformation societyInfo = new SocietyInformation();
	ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	try {
		ResultSet rs = sqlHandler.query("Administrator", "*", "studentID","=", login.getStudentID());
		if(rs.next()) {
		ResultSet rs2 = sqlHandler.query("SocietyMember", "*", "societyID","=", rs.getString("societyID"));
		while(rs2.next()) {
			ResultSet rs3 = sqlHandler.query("Student", "*", "studentID","=", rs2.getString("studentID"));
			if(rs3.next()) {
			HashMap<String,String> map2 = new HashMap<String,String>();
			String firstname = rs3.getString("firstname");
			map2.put("firstname",firstname);
			String studentID = rs3.getString("studentID");
			map2.put("studentID",studentID);
			map.add(map2);
			}
		}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	societyInfo.setMap(map);
	return societyInfo;
}

@RequestMapping("/RequestSocietyMemberDetail")
public @ResponseBody Student RequestSocietyMemberDetail(@RequestBody Login login) {
	
	Student stu = new Student();
	ResultSet rs = sqlHandler.query("Student", "*", "studentID","=", login.getStudentID());
	try {
		if(rs.next()) {	
							stu.setStudentID(rs.getString("studentID"));
							stu.setGender(rs.getString("gender"));
							stu.setFirstname(rs.getString("firstname"));
							stu.setSurname(rs.getString("surname"));
							stu.setProgramme(rs.getString("programme"));
							stu.setEmail(rs.getString("email"));
							stu.setPreference(rs.getString("preference"));
							stu.setAccount(rs.getString("account"));
							stu.setPassword(rs.getString("password"));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return stu;
}


/** administrator check who have taken participated in activity
 * 
 * @param login
 * @return activity name : [member,member2,member3]
 */
@RequestMapping("/RequestSocietyActivity")
public @ResponseBody ActivityMember RequestSocietyActivity(@RequestBody Login login){
	ActivityMember activityMember = new ActivityMember();
	HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	try {
		ResultSet rs = sqlHandler.query("Administrator", "*", "studentID","=", login.getStudentID());
		if(rs.next()) {
		// already rs.getString("societyID")
		ResultSet rs2 = sqlHandler.query("Activity", "*", "societyID","=", rs.getString("societyID"));
		while(rs2.next()) {
	    	Date date = new Date();
	    	DateHandler dh = new DateHandler();
			Date activitytime = dh.convertStringtoDate(rs2.getString("time"));
			if(date.before(activitytime)) {
			ResultSet rs3 = sqlHandler.query("Participator", "*", "activityID","=", rs2.getString("activityID"));
			ArrayList<String> arr = new ArrayList<String>();
			while(rs3.next()) {
			ResultSet rs4 = sqlHandler.query("Student", "*", "studentID","=", rs3.getString("studentID"));
			if(rs4.next()) {
			String firstname = rs4.getString("firstname");
			arr.add(firstname);
			}
			}
			String activityName = rs2.getString("name");
			map.put(activityName, arr);
		}
		}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	activityMember.setMap(map);
	return activityMember;
}

/** administrator check who want to apply to society
 * 
 * @param login
 * @return student name and (administrator ID and password)
 */
@RequestMapping("/RequestSocietyMemberApplication")
public @ResponseBody SocietyInformation RequestSocietyMemberApplication(@RequestBody Login login){
	SocietyInformation societyInfo = new SocietyInformation();
	ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	try {
		ResultSet rs = sqlHandler.query("Administrator", "*", "studentID","=", login.getStudentID());
		if(rs.next()) {
		ResultSet rs2 = sqlHandler.query("ApplicationForSociety", "*", "societyID","=", rs.getString("societyID"));
		while(rs2.next()) {
			ResultSet rs3 = sqlHandler.query("Student", "*", "studentID","=", rs2.getString("studentID"));
			if(rs3.next()) {
				if(rs2.getString("state").equals("0")) {
			HashMap<String,String> map2 = new HashMap<String,String>();
			String firstname = rs3.getString("firstname");
			map2.put("firstname",firstname);
			String studentID = rs3.getString("studentID");
			map2.put("studentID",studentID);
			map.add(map2);
			}
			}
		}
		}
		
		// return addition studentID password
		ResultSet rs4 = sqlHandler.query("Student", "*", "studentID","=", login.getStudentID());
		HashMap<String,String> map2 = new HashMap<String,String>();
		map2.put("administratorID",login.getStudentID());
		map2.put("password",rs4.getString("password"));
		map.add(map2);
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
	societyInfo.setMap(map);
	return societyInfo;
}

/** administrator check all the activity they manage
 * 
 * @param login
 * @return
 */
@RequestMapping("/SocietyAdministratorActivityList")
public @ResponseBody ActivityInformation SocietyAdministratorActivityList(@RequestBody Login login) {
    ActivityInformation activityInfo = new ActivityInformation();
    ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
    try {
    	ResultSet rs1 = sqlHandler.query("Administrator", "*", "studentID","=", login.getStudentID());
    	if(rs1.next()) {
    	ResultSet rs = sqlHandler.query("Activity", "*", "societyID", "=", rs1.getString("societyID"));
		while(rs.next()) {
			HashMap<String,String> map2 = new HashMap<String, String>();  
			String activityID = rs.getString("activityID");
			map2.put("activityID",activityID);
			String name = rs.getString("name");
			map2.put("name",name);
			String type = rs.getString("type");
			map2.put("type",type);
			String description  = rs.getString("description");
			map2.put("description",description);
			String location = rs.getString("location");
			map2.put("location",location);
			String time = rs.getString("time");
			map2.put("time",time);
			String participatorNum = rs.getString("participatorNum");
			map2.put("participatorNum",participatorNum);
			map2.put("societyID",rs1.getString("societyID"));
			String poster = rs.getString("poster");
			map2.put("poster",poster);
			map.add(map2);
		}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
    activityInfo.setMap(map);    
    return activityInfo;
}

/** student check what society they join in 
 * 
 * @param login
 * @return society detail
 */
@RequestMapping("/StudentSocietyList")
public @ResponseBody ActivityInformation StudentSocietyList(@RequestBody Login login) {
    ActivityInformation activityInfo = new ActivityInformation();
    ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
    try {
    	ResultSet rs1 = sqlHandler.query("SocietyMember", "*", "studentID","=", login.getStudentID());
    	while(rs1.next()) {
    	ResultSet rs = sqlHandler.query("Society", "*", "societyID", "=", rs1.getString("societyID"));
		while(rs.next()) {
			HashMap<String,String> map2 = new HashMap<String, String>();  
			String name  = rs.getString("name");
			map2.put("name",name);
			String type  = rs.getString("type");
			map2.put("type",type);
			String description  = rs.getString("description");
			map2.put("description",description);
			map2.put("societyID",rs.getString("societyID"));
			String poster = rs.getString("poster");
			map2.put("poster",poster);
			map.add(map2);
		}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
    activityInfo.setMap(map);    
    return activityInfo;
}

/** student check all activity he/her join in
 * 
 * @param login
 * @return activity list (arrayList<map>)
 */
@RequestMapping("/SocietyStudentActivityList") // Student join all activity 
public @ResponseBody ActivityInformation SocietyStudentActivityList(@RequestBody Login login) {
    ActivityInformation activityInfo = new ActivityInformation();
    ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
    try {
    	ResultSet rs1 = sqlHandler.query("Participator", "*", "studentID","=", login.getStudentID());
    	while(rs1.next()) {
    	ResultSet rs = sqlHandler.query("Activity", "*", "activityID", "=", rs1.getString("activityID"));
		while(rs.next()) {
			HashMap<String,String> map2 = new HashMap<String, String>();  
			String activityID = rs.getString("activityID");
			map2.put("activityID",activityID);
			String name = rs.getString("name");
			map2.put("name",name);
			String type = rs.getString("type");
			map2.put("type",type);
			String description  = rs.getString("description");
			map2.put("description",description);
			String location = rs.getString("location");
			map2.put("location",location);
			String time = rs.getString("time");
			map2.put("time",time);
			String participatorNum = rs.getString("participatorNum");
			map2.put("participatorNum",participatorNum);
			map2.put("societyID",rs.getString("societyID"));
			String poster = rs.getString("poster");
			map2.put("poster",poster);
			map.add(map2);
		}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
    activityInfo.setMap(map);    
    return activityInfo;
}

/** student check available activity in calendar
 * 
 * @param studentID
 * @return Date in array 
 */
@RequestMapping("/StudentCalendarAvailable") // Student join all activity 
public @ResponseBody AvailableTimeList StudentCalendarAvailable(@RequestBody Calendardetail cal) {
	AvailableTimeList atl = new AvailableTimeList();
	ArrayList<String> arr = new ArrayList<>();
	try {
		ResultSet rs1 = sqlHandler.query("Participator", "*", "studentID","=", cal.getStudentID());
		while(rs1.next()) {
			ResultSet rs = sqlHandler.query("Activity", "*", "activityID", "=", rs1.getString("activityID"));
			while(rs.next()) {
				//String convert to Date
				DateHandler dh = new DateHandler();
				Date activitytime = dh.convertStringtoDate(rs.getString("time"));
				Date caltime = dh.convertStringtoDate(cal.getDate());
				
				//Date convert to String 
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String activitytimeString = sdf.format(activitytime);
				
				if (caltime.before(activitytime)) {
					arr.add(activitytimeString);
				}
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	atl.setArr(arr);
	return atl;
}

/** student check activity detail in calendar
 *
 * @param cal date and studentID
 * @return activity list
 */
@RequestMapping("/StudentCalendarActivityDetail")
public @ResponseBody ActivityInformation StudentCalendarActivityDetail(@RequestBody Calendardetail cal) {
	ActivityInformation activityInfo = new ActivityInformation();
	ArrayList<HashMap<String,String>> map = new ArrayList<HashMap<String,String>>();
	try {
		ResultSet rs1 = sqlHandler.query("Participator", "*", "studentID","=", cal.getStudentID());
		while(rs1.next()) {
			ResultSet rs = sqlHandler.query("Activity", "*", "activityID", "=", rs1.getString("activityID"));
			while(rs.next()) {
				HashMap<String,String> map2 = new HashMap<String, String>(); 
				DateHandler dh = new DateHandler();
				Date activitytime = dh.convertStringtoDate(rs.getString("time"));
				Date caltime = dh.convertStringtoDate(cal.getDate());
				
		        Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        
	
		 
		        Calendar c = Calendar.getInstance();
		        c.setTime(caltime);
		        c.add(Calendar.DAY_OF_MONTH, 1);// 浠婂ぉ+1澶�
		        Date tomorrow = c.getTime();

				
				if (activitytime.after(caltime) && activitytime.before(tomorrow)) {
					String activityID = rs.getString("activityID");
					map2.put("activityID",activityID);
					String name = rs.getString("name");
					map2.put("name",name);
					String type = rs.getString("type");
					map2.put("type",type);
					String description  = rs.getString("description");
					map2.put("description",description);
					String location = rs.getString("location");
					map2.put("location",location);
					
					Date time0 = dh.convertStringtoDate(rs.getString("time"));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf.format(time0);
					
					map2.put("time",time);
					String participatorNum = rs.getString("participatorNum");
					map2.put("participatorNum",participatorNum);
					map2.put("societyID",rs.getString("societyID"));
					String poster = rs.getString("poster");
					map2.put("poster",poster);
					map.add(map2);
				}
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	activityInfo.setMap(map);    
	return activityInfo;
}

/** administrator check society member details
 * 
 * @param login
 * @return
 */
@RequestMapping("/RequestApplicationSocietyMemberDetail")
public @ResponseBody Student RequestApplicationSocietyMemberDetail(@RequestBody Login login) {
	
	Student stu = new Student();
	ResultSet rs = sqlHandler.query("Student", "*", "studentID","=", login.getStudentID());
	try {
		if(rs.next()) {				
				ResultSet rs1 = sqlHandler.query("Administrator", "*", "studentID","=", login.getPassword());//password is administrator studentID
				if(rs1.next()) {
					ResultSet rs2 = sqlHandler.query("ApplicationForSociety", "*", "studentID","=", login.getStudentID());
					while(rs2.next()) {
						if(rs1.getString("societyID").equals(rs2.getString("societyID"))) {
							stu.setState(rs2.getString("state"));
							stu.setStudentID(rs.getString("studentID"));
							stu.setGender(rs.getString("gender"));
							stu.setFirstname(rs.getString("firstname"));
							stu.setSurname(rs.getString("surname"));
							stu.setProgramme(rs.getString("programme"));
							stu.setEmail(rs.getString("email"));
							stu.setPreference(rs.getString("preference"));
							stu.setAccount(rs.getString("account"));
							stu.setPassword(rs.getString("password"));
						}
					}
				}
				return stu;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return stu;
}

/** administrator updates student apply society state
 * 
 * @param stuAdminInf
 */
@RequestMapping("/AdministratorHandleStudentApplication")
public void AdministratorHandleStudentApplication(@RequestBody Student stuAdminInf) {
	try {
		ResultSet rs = sqlHandler.query("Administrator", "*", "studentID","=", stuAdminInf.getAdministratorAccount());
		if(rs.next()) {// only manage society
			//getString("societyID")
			ResultSet rs1 = sqlHandler.query("ApplicationForSociety", "*", "societyID", "=", rs.getString("societyID"));
			while(rs1.next()) {
				if(rs1.getString("studentID").equals(stuAdminInf.getStudentID())) {
					sqlHandler.update("ApplicationForSociety", "applicationID", rs1.getString("applicationID"),
							"state", stuAdminInf.getState());
					if (stuAdminInf.getState().equals("1")) {
						String memberID = sqlHandler.getMaxId("SocietyMember", "memberID");
						int memID = Integer.parseInt(memberID);
						memID = memID + 1;
						String newMemberID = memID + "";
						
						ArrayList<String> newSocietyMember = new ArrayList<>();
						newSocietyMember.add(newMemberID);
						newSocietyMember.add(stuAdminInf.getStudentID());
						newSocietyMember.add(rs.getString("societyID"));
					    sqlHandler.insert("SocietyMember", newSocietyMember);
					}
				}
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

@RequestMapping("/signup")
public void signup(@RequestBody Student stu) {
	ArrayList<String> newStudent = new ArrayList<>();
	newStudent.add(stu.getStudentID());
	newStudent.add(stu.getGender());
	newStudent.add(stu.getFirstname());
	newStudent.add(stu.getSurname());
	newStudent.add(stu.getProgramme());
	newStudent.add(stu.getEmail());
	newStudent.add(stu.getPreference());
	newStudent.add("0");
	newStudent.add(stu.getPassword());
	sqlHandler.insert("Student", newStudent);
}

}
