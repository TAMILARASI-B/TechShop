package util;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {
  
    	private static final String fileName="C:\\Users\\ELCOT\\Workspace\\TechShop\\src\\db.properties";
    	public static Connection getDbConnection() throws IOException {
    		Connection con=null;
    		String connString=null;
    		connString=DBPropertyUtil.getConnectionString(fileName);
    		if(connString!=null) 
    		{
    			try 
    			{
    				con=DriverManager.getConnection(connString);
    		
    			}
    			catch (SQLException e) 
    			{
    				System.out.println("Error While Establishing DBConnection........");
    				e.printStackTrace();
    			}
    		}
    		return con;
    	}

    	
	
       
    }

