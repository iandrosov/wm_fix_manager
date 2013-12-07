package wm.fix.manager.install;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wm.fix.manager.data.FixDataJDBC;


public class FixDBScript 
{
	private ArrayList al = null;
	public FixDBScript()
	{
		al = new ArrayList();
	}
	
	/**
	 * read_script parses a gfive SQL script file and craetes
	 * list of SQL commands to execute later.
	 * @param script_file
	 */
	public boolean read_script(String script_file)
	{
		try
		{
			BufferedReader fr = new BufferedReader(new FileReader(script_file));
			String line = "";
			String sql = ""; 
			while ((line = fr.readLine()) != null)
			{				
				if (isSQL(line))
				{
					if (isProcedureFunctionSQL(line))
					{
						sql = readProcedure(line,fr);
						al.add(sql);
						sql = "";
					} 
					else if (isCompleteSQL(line))
					{
						sql += line;
						al.add(sql);
						sql = "";
					}
					else
						sql += line;
				}
				
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	private String readProcedure(String sql_line, BufferedReader bfr) throws IOException
	{
		String line = "";
		String sql = sql_line+"\n"; 
		while ((line = bfr.readLine()) != null)
		{				
			if (!isProcedureEND(line))
			{
				sql += line +"\n";
			}
			else
			{
				sql += line +"\n";
				return sql;
			}
		}
		return sql;
	}
	
	
	/**
	 * Run complete set of SQL commands found in a script
	 * @param jdbc - JDBC parm data
	 * @return - String message of resulting SQL executionsa nd statuses 
	 */
	public String run_script(FixDataJDBC jdbc)
	{		
		Connection conn = null;
		String msg = "";
		try
		{
			conn = getConnection(jdbc);
			for (int i = 0; i < al.size(); i++)
			{
				//System.out.println((String)al.get(i));
				msg += executeQuery((String)al.get(i), conn);
			}
		}
		catch(Exception e)
		{		
			e.printStackTrace();
		}
		try
		{
 	   		if (conn != null)
 	   			conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if (msg.length()== 0)
			msg = "### DB Script was not read correctly or invalid."; // Default message
		return msg;
	}
	
    private String executeQuery(String sql, Connection conn)throws SQLException, IOException
    {   	
    	
    	String msg = "Execute sql: ";
       try
       {   	   

    	   Statement stat = conn.createStatement();
    	   String ex = sql.substring(0,sql.length()-1);
    	   msg += ex + "\n";
    	   stat.execute(ex);
    	   
    	   msg += "SQL execution Completed.\n";
    	   
       }
       catch (SQLException e)
       {
    	   //e.printStackTrace();
    	   msg += e.getMessage() + "\n";
       }
       catch (Exception e)
       {
    	   //e.printStackTrace();
    	   msg += e.getMessage()+ "\n";
       }
       return msg;
    }
	/**
	 * testConnection public method used to test JDBC connection
	 * @param jdbc_cnf
	 * @return true if connection success false otherwise
	 */
    public boolean testConnection(FixDataJDBC jdbc_cnf)
    {
    	boolean rc = false;
    	if (jdbc_cnf != null)
    	{
    		try
    		{
    			Connection conn = getConnection(jdbc_cnf);
    			if (conn != null)
    			{
    				conn.close();
    				rc = true;
    			}
    		}
    		catch(Exception e)
    		{
    			rc = false;
    		}
    	}
    	return rc;
    }
    
    /**
    Gets a connection from the properties specified
    in the file database.properties
    @return the database connection
    */
	private Connection getConnection(FixDataJDBC jdbc_cnf)
    throws SQLException, IOException
    {
		//Properties props = new Properties();
		//FileInputStream in = new FileInputStream("database.properties");
		//props.load(in);
		//in.close();

		String drivers = jdbc_cnf.getJDBCDriver();//props.getProperty("jdbc.drivers");
		if (drivers != null) 
			System.setProperty("jdbc.drivers", drivers);
		String url = jdbc_cnf.getURL(); //props.getProperty("jdbc.url");
		String username = jdbc_cnf.getDBUser(); //props.getProperty("jdbc.username");
		String password = jdbc_cnf.getDBPassword(); //props.getProperty("jdbc.password");

		return DriverManager.getConnection(url, username, password);
    }

	private boolean isCompleteSQL(String str)
	{
		String a = str.trim();
		String tmp = a.substring(a.length()-1,a.length());
		
		if (tmp.equals(";"))
			return true;
		else
			return false;
	}
	
	private boolean isSQL(String str)
	{
		if (str.length() < 2)
			return false;
		String cmd = "prompt";
		if (str.length() > cmd.length())
		{
			String pr = str.substring(0,cmd.length());
			if (pr.equals(cmd))
				return false;
		}
		String tmp = str.substring(0,2);
		if (tmp.equals("--") || tmp.equals("=="))
			return false;
		else
			return true;
	}

	private boolean isProcedureEND(String line)
	{
		String tmp = "END;";
		if (line.length()< tmp.length())
			return false;
		String a = line.trim().toUpperCase();
		int start = a.length()-tmp.length();
		int end = start+tmp.length();
		String b = a.substring(start,end);
		if (b.equals(tmp))
			return true;
		
		return false;
	}
	
	private boolean isProcedureFunctionSQL(String sql)
	{
		String tmp = "CREATE FUNCTION";		
		if (sql.trim().length() < tmp.length())
			return false;
		String a = sql.trim().toUpperCase();
		String b = a.substring(0,tmp.length());
		if (b.equals(tmp))
			return true;
		
		tmp = "CREATE PROCEDURE";
		if (sql.trim().length() < tmp.length())
			return false;
		a = sql.trim().toUpperCase();
		b = a.substring(0,tmp.length());
		if (b.equals(tmp))
			return true;

		tmp = "CREATE OR REPLACE FUNCTION";
		if (sql.trim().length() < tmp.length())
			return false;
		a = sql.trim().toUpperCase();
		b = a.substring(0,tmp.length());
		if (b.equals(tmp))
			return true;

		tmp = "CREATE OR REPLACE PROCEDURE";
		if (sql.trim().length() < tmp.length())
			return false;
		a = sql.trim().toUpperCase();
		b = a.substring(0,tmp.length());
		if (b.equals(tmp))
			return true;
		
		return false;
	}
	
}
