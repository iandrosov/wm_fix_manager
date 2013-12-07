package wm.fix.manager.data;

import java.util.Enumeration;
import java.util.Hashtable;


public class FixProfileData 
{
	private Hashtable fix_profile_data = null;
	
	public FixProfileData()
	{
		fix_profile_data = new Hashtable();
	}
	public FixProfile getProfile(String name)
	{
		FixProfile fp = null;
		
		if (fix_profile_data != null)
			fp = (FixProfile)fix_profile_data.get(name);
		
		return fp;
	}
	public void addFixProfile(FixProfile fp)
	{
		if (fp == null)
			return;
		if (fix_profile_data == null)
			fix_profile_data = new Hashtable();
		
		fix_profile_data.put(fp.getName(),fp);
	}
	
	public FixProfile[] getFixProfileList()
	{
		int size = fix_profile_data.size();
		FixProfile[] fp = new FixProfile[size];
		Enumeration enm = fix_profile_data.elements();
		int i = 0;
		while(enm.hasMoreElements())
		{
			fp[i] = (FixProfile)enm.nextElement();
			i++;
		}
		
		return fp;
	}
	
	public int getSize()
	{
		return fix_profile_data.size();
	}

}
