package wm.fix.manager.install;

import java.util.Properties;

public class FixConfig 
{
	private boolean m_audit_log = true;
	private boolean m_log_to_file = false;
	private boolean m_console = false;
	private String m_dir = null;
	
	public FixConfig()
	{
		Properties p = System.getProperties();
		m_dir = p.getProperty("logdir","");
		m_audit_log = Boolean.getBoolean("auditlog");
		m_log_to_file = Boolean.getBoolean("logtofile");
		m_console = Boolean.getBoolean("console");
		
	}
	
	public String get_audit_log_dir()
	{
		return m_dir;
	}
	public boolean is_console()
	{
		return m_console;
	}
	public boolean is_audit_log()
	{
		return m_audit_log;
	}
	
	public boolean is_audit_log_file()
	{
		return m_log_to_file;
	}
}
