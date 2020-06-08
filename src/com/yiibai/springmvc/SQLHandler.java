package com.yiibai.springmvc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class SQLHandler {
    private Connection con;
	
	public SQLHandler() {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://60.205.225.26:3306/group2";
        String user = "group2";
        String password = "asdfg123";
   
        try {
            Class.forName(driver);
           
            
            con = DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
 
        } catch(ClassNotFoundException e) {   
        
            System.out.println("Sorry,can`t find the Driver!");   
            e.printStackTrace();   
            } catch(SQLException e) {        
            e.printStackTrace();  
            }catch (Exception e) {
            e.printStackTrace();
        }        
        
    } 
    
    public boolean insert(String tableName, ArrayList<String> value) {
    	boolean flag = false;
    	try {
			Statement statement = con.createStatement();
			String sql = "insert into "+tableName+" values (";
			for(int i = 0; i<value.size();i++) {
				String string = value.get(i);
				if(i != value.size()-1) {
				    sql += "'"+ string +"',";
				}else {
					sql += "'"+ string +"'";
				}
			}
			sql += ")";
			flag = (statement.executeUpdate(sql) != 0);
		} catch (SQLException e) {
			System.out.println("Execution fails.......");
			e.printStackTrace();
		}
    	return flag;
    }
    
    public boolean delete(String tableName, String attributeName, String attributeValue) {
        boolean flag = false;
        try {
			Statement statement = con.createStatement();
			String sql = "delete from "+tableName+" where "+attributeName+" = '"+attributeValue+"'";
			flag = (statement.executeUpdate(sql) != 0);
        } catch (SQLException e) {
        	System.out.println("Execution fails");
			e.printStackTrace();
		}
       
    	return flag;
    }
    
    //use * to query all the attributes
    public ResultSet query(String tableName, String targetAttribute,String attributeName, 
    		String operation,String attributeValue){
    	 try {
 			Statement statement = con.createStatement();
 			String sql = "select "+targetAttribute+" from "+tableName+" where "+attributeName+" "+operation+" '"+attributeValue+"'";
 			ResultSet rs = statement.executeQuery(sql);
 			return rs;
         } catch (SQLException e) {
         	System.out.println("Execution fails");
 			e.printStackTrace();
 			return null;
 		}    	
    }
    
    public boolean update(String tableName, String attributeName, String attributeValue,
    		              String newAttribute, String newValue) {
    	boolean flag = false;
    	try {
    		Statement statement = con.createStatement();
    		String sql = "update "+tableName+" set " + newAttribute+" = '"+newValue+
    				      "' where "+attributeName+" = '"+attributeValue+"'";
    		flag = (statement.executeUpdate(sql) != 0);
    	}catch(SQLException e) {
    		System.out.println("Execution fails");
    		e.printStackTrace();
    	}
    	return flag;
    	
    }
    
    public String getMaxId(String tableName, String targetAttribute) {
        String result = "";
        try {
         Statement statement = con.createStatement();
         String sql = "select MAX("+targetAttribute+") from "+tableName;
         ResultSet rs = statement.executeQuery(sql);
         if(rs.next()) {
          result = rs.getString(1);
         }
        }catch(SQLException e) {
         System.out.println("Excution fails");
            e.printStackTrace();
        }
        return result;
       }
    
    public ResultSet getTableOrder(String tableName, String targetAttribute, String order) {
        try {
         Statement statement = con.createStatement();
         String sql = "SELECT * FROM `"+tableName+"` ORDER BY `"+tableName+"`.`"+targetAttribute+"` "+order;
         ResultSet rs = statement.executeQuery(sql);
         return rs;
        }catch(SQLException e) {
         System.out.println("Excution fails");
            e.printStackTrace();
            return null;
        }
       }
    
    //SELECT * FROM `Activity` ORDER BY `Activity`.`time` ASC
    
}
