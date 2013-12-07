package wm.fix.manager.data;

import java.util.Hashtable;

public class FixDataJDBC 
{
	// JDBC stuff
	private String m_jdbc_type = FixJDBCDriverConstants.DB_ORACLE; 
	private String m_jdbc_drivers = FixJDBCDriverConstants.JDBC_ORACLE;
	private String m_jdbc_url = FixJDBCDriverConstants.URL_ORACLE;
	private String m_jdbc_username = "user";
	private String m_jdbc_password = "password"; 

	private String[] m_db_types = {FixJDBCDriverConstants.DB_ORACLE,FixJDBCDriverConstants.DB_SQL_SERVER,FixJDBCDriverConstants.DB_DB2,FixJDBCDriverConstants.DB_SYBASE};
	private Hashtable m_drv_map = null;
	
	public FixDataJDBC()
	{
		initDriver();
	}
	
	public String[] getAllDBTypes(){return m_db_types;}
	public String getDBType(){return m_jdbc_type;}
	public void setDBType(String str){m_jdbc_type=str;}
	public String getJDBCDriver(){return m_jdbc_drivers;}
	public void setJDBCDriver(String str){m_jdbc_drivers=str;}
	public String getURL(){return m_jdbc_url;}
	public void setURL(String str){m_jdbc_url=str;}
	public String getDBUser(){return m_jdbc_username;}
	public void setDBUser(String str){m_jdbc_username=str;}
	public String getDBPassword(){return m_jdbc_password;}
	public void setDBPassword(String str){m_jdbc_password=str;}
	public String getDBDriver(String key)
	{
		String str="";
		return str;
	}
	
	public void print()
	{
		System.out.println("@@@@ JDBC Stuff @@@@");
		System.out.println("m_jdbc_type="+m_jdbc_type);
		System.out.println("m_jdbc_drivers="+m_jdbc_drivers);
		System.out.println("m_jdbc_url="+m_jdbc_url);
		System.out.println("m_jdbc_username="+m_jdbc_username);
		System.out.println("m_jdbc_password="+m_jdbc_password);		
	}
	
	public String getJDBCURL(String str)
	{
		if (str.equals(FixJDBCDriverConstants.DB_ORACLE))
			return FixJDBCDriverConstants.URL_ORACLE;
		else if (str.equals(FixJDBCDriverConstants.DB_SQL_SERVER))
			return FixJDBCDriverConstants.URL_SQL_SERVER;
		else if (str.equals(FixJDBCDriverConstants.DB_DB2))
			return FixJDBCDriverConstants.URL_DB2;
		else if (str.equals(FixJDBCDriverConstants.DB_SYBASE))
			return FixJDBCDriverConstants.URL_SYBASE;
		else
			return FixJDBCDriverConstants.JDBC_NONE;
		
	}
	
	public String getJDBCDriverName(String key)
	{
		String str = "";
		if (m_drv_map != null)
			str = (String)m_drv_map.get(key);
		return str;
	}
	
	private void initDriver()
	{
		if (m_drv_map == null)
			m_drv_map = new Hashtable();
		for (int i=0; i < m_db_types.length; i++)
		{
			m_drv_map.put(m_db_types[i],getDriverName(m_db_types[i]));
		}
	}
	
	private String getDriverName(String str)
	{
		if (str.equals(FixJDBCDriverConstants.DB_ORACLE))
			return FixJDBCDriverConstants.JDBC_ORACLE;
		else if (str.equals(FixJDBCDriverConstants.DB_SQL_SERVER))
			return FixJDBCDriverConstants.JDBC_SQL_SERVER;
		else if (str.equals(FixJDBCDriverConstants.DB_DB2))
			return FixJDBCDriverConstants.JDBC_DB2;
		else if (str.equals(FixJDBCDriverConstants.DB_SYBASE))
			return FixJDBCDriverConstants.JDBC_SYBASE;
		else
			return FixJDBCDriverConstants.JDBC_NONE;
	}

}
