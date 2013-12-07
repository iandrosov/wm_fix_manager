package wm.fix.manager.data;

public interface FixJDBCDriverConstants 
{
//	 JDBC Driver names
    public static final String JDBC_ORACLE 		= "oracle.jdbc.driver.OracleDriver";
    public static final String JDBC_SQL_SERVER  = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	public static final String JDBC_DB2    		= "com.ibm.db2.jcc.DB2Driver";
	public static final String JDBC_SYBASE    	= "com.ddtek.jdbc.sybase.SybaseDriver";
	public static final String JDBC_NONE 		= "NA";
	
//	DB Names
	public static final String DB_ORACLE 		= "Oracle";
	public static final String DB_SQL_SERVER	= "SQLServer";
	public static final String DB_DB2 			= "DB2";
	public static final String DB_SYBASE 		= "Sybase";
	
	public static final String URL_ORACLE 		= "jdbc:oracle:thin:@<host>:1521:<SID>";
	public static final String URL_SQL_SERVER   = "jdbc:microsoft:sqlserver://<host>:1433";
	public static final String URL_DB2    		= "jdbc:db2://<host>:<5021>/<database name>";
	public static final String URL_SYBASE    	= "jdbc:datadirect:sybase://<host>:<port>; databaseName=<DB>";

}
