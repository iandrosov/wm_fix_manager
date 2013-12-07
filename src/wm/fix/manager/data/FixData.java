package wm.fix.manager.data;

public class FixData 
{
	private boolean m_installed;
	private int m_install_status;
	private String m_fix_name;
	private String m_file_name;
	private String m_src;
	private String m_trg;
	private boolean m_copy;
	private boolean m_unzip;
	private boolean m_pkg_install;
	private boolean m_db_script;
	private String m_pkg_name;

	public FixDataJDBC jdbc;
	
	public FixData()
	{
		m_install_status = FixDataConstants.FIX_INSTALL_INIT;
		m_installed = true;
		m_fix_name = "";
		m_file_name = "";
		m_src = "";
		m_trg = "";
		m_pkg_name = "Package Name";
		m_copy = false;
		m_unzip = false;
		m_pkg_install = false;
		m_db_script = false;
		jdbc = new FixDataJDBC();
	}
	public int getStatus(){return m_install_status;}
	public void setStatus(int status){m_install_status = status;}
	public boolean getInstalled(){return m_installed;}
	public void setInstalled(boolean b){m_installed = b;}
	public String getName(){return m_fix_name.trim();}
	public void setName(String name){m_fix_name = name.trim();}
	public String getFileName(){return m_file_name;}
	public void setFileName(String file){m_file_name = file;}
	public String getSourceDir(){return m_src;}
	public void setSourceDir(String dir){m_src = dir;}
	public String getTargetDir(){return m_trg;}
	public void setTargeDir(String dir){m_trg = dir;}
	public boolean getCopyFlag(){return m_copy;}
	public void setCopyFlag(boolean bl){m_copy = bl;}
	public boolean getUnzipFlag(){return m_unzip;}
	public void setUnzipFlag(boolean bl){m_unzip = bl;}
	public boolean getPkgFlag(){return m_pkg_install;}
	public void setPkgFlag(boolean bl){m_pkg_install = bl;}
	public boolean getDBScript(){return m_db_script;}
	public void setDBScript(boolean bl){m_db_script = bl;}
	public void setPackageName(String name){m_pkg_name=name;}
	public String getPackageName(){return m_pkg_name;}
	public String getFixName(){return m_fix_name;}
	public void print()
	{
		System.out.println("#### Print Fix Data #####"); 
		System.out.println("m_fix_name="+m_fix_name);
		System.out.println("m_file_name="+m_file_name);
		System.out.println("m_src="+m_src);
		System.out.println("m_trg="+m_trg);
		System.out.println("m_copy="+Boolean.toString(m_copy));
		System.out.println("m_unzip="+Boolean.toString(m_unzip));
		System.out.println("m_pkg_install="+Boolean.toString(m_pkg_install));
		
		
	}
	public String toString(){return m_fix_name;}
	
	public void updatePath(String src, String trg)
	{
		String tmp = "";
		String path = "";
		if (m_file_name.length() >= src.length())
		{
			path = m_file_name.substring(0,src.length());
			if (path.equals(src))
			{
				tmp = m_file_name.substring(src.length(),m_file_name.length());
				m_file_name = src + tmp;
			}
		}
		if (m_src.length() >= src.length())
		{
			path = m_src.substring(0,src.length());
			if (path.equals(src))
			{
				tmp = m_src.substring(src.length(),m_src.length());
				m_src = src + tmp;
			}
		}
		if (m_trg.length() >= trg.length())
		{
			path = m_trg.substring(0,trg.length());
			if (path.equals(trg))
			{
				tmp = m_trg.substring(src.length(),m_trg.length());
				m_trg = trg + tmp;
			}		
		}		
	}

}
