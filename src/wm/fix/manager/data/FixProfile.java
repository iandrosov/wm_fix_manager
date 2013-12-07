package wm.fix.manager.data;

import java.util.ArrayList;


public class FixProfile 
{
	private String m_name = null;
	private String m_desc = null;
	private String m_src = null;
	private String m_trg = null;
	private String m_trg_dev = null;
	private String m_trg_is = null;
	private String m_trg_broker = null;
	private String m_trg_common = null;
	
	//private Hashtable fix_data;
	private ArrayList fix_data;
	
	public FixProfile()
	{
		m_name = "";
		m_src = "";
		m_desc = "";
		m_trg = "";
		m_trg_dev = "";
		m_trg_is = "";
		m_trg_broker = "";
		m_trg_common = "";
		
		fix_data = new ArrayList();
	}
	
	public String toString(){return m_name;}
	public String getName(){return m_name;}
	public void setName(String name){m_name = name;}
	public String getDesc(){return m_desc;}
	public void setDesc(String name){m_desc = name;}
	public String getSourceDir(){return m_src;}
	public void setSourceDir(String dir){m_src = dir;}
	public String getTargetDir(){return m_trg;}
	public void setTargeDir(String dir){m_trg = dir;}

	public String getDeveloperDir(){return m_trg_dev;}
	public void setDeveloperDir(String dir){m_trg_dev = dir;}
	public String getISDir(){return m_trg_is;}
	public void setISDir(String dir){m_trg_is = dir;}
	public String getBrokerDir(){return m_trg_broker;}
	public void setBrokerDir(String dir){m_trg_broker = dir;}
	public String getCommonDir(){return m_trg_common;}
	public void setCommonDir(String dir){m_trg_common = dir;}
	
	public void addFixData(FixData fd)
	{
		if (fd == null)
			return;
		if (fix_data == null)
			fix_data = new ArrayList();

		fix_data.add(fd);
	}
	
	public void removeFixData(String name)
	{
/*		
		if (fix_data != null)
		{
			if (fix_data.containsKey(name))
				fix_data.remove(name);
		}
*/
		if (fix_data != null)
		{
			for (int i=0; i < fix_data.size(); i++)
			{
				FixData fd = (FixData)fix_data.get(i);
				if (fd.getName().equals(name))
					fix_data.remove(i);
			}
		}
	}
	
	public FixData[] getFixList()
	{
		Object[] obj = fix_data.toArray();		
		FixData[] fxd = new FixData[obj.length];
		for (int i=0; i < obj.length; i++)
		{			
			fxd[i] = (FixData)obj[i];
		}
		
		return fxd;
	}

	public FixData getFixByName(String name)
	{
		FixData fd = null;
		if (fix_data != null)
		{
			for (int i=0; i < fix_data.size(); i++)
			{
				fd = (FixData)fix_data.get(i);
				if (fd.getName().equals(name))
					return fd;
			}
		}
		return fd;
	}
	
	public void updateAllFixDirectory()
	{
		if (fix_data != null)
		{
			for (int i=0; i < fix_data.size(); i++)
			{
				FixData fd = (FixData)fix_data.get(i);
				fd.updatePath(m_src,m_trg);
				fix_data.set(i,fd);
			}
		}
		
	}
	
	public void print()
	{
		System.out.println("#### Print Profile Data #####"); 
		System.out.println("m_name="+m_name);
		System.out.println("m_desc="+m_desc);
		System.out.println("m_src="+m_src);
		System.out.println("m_trg="+m_trg);
		System.out.println("m_trg_is="+m_trg_is);
		System.out.println("m_trg_broker="+m_trg_broker);
		System.out.println("m_trg_dev="+m_trg_dev);
		System.out.println("m_trg_common="+m_trg_common);
		
		FixData[] fd = getFixList();
		for (int i=0; i<fd.length; i++)
			fd[i].print();
	}

}
