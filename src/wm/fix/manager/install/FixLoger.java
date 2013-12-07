package wm.fix.manager.install;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import wm.fix.manager.opt.install.FixUninstaller;
import wm.fix.manager.opt.install.ProfileBackupProcess;
import wm.fix.manager.opt.install.ProfileRestoreProcess;

public class FixLoger 
{
	public FixLoger(){}
	
	public static void audit_log(String msg)
	{
		String str = null;
		FixConfig oc = new FixConfig();
		
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{
				System.out.println(getStamp(str) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(null);
				writeLog(getStamp(str) + msg, fn);
			}
		}
	}

	public static void audit_log(String msg, FixInstaller fi)
	{
		String str = null;
		FixConfig oc = new FixConfig();
		// Set status message to FixInstaller object
		if (fi != null)
		{
			fi.setMessage(msg);
			fi.updateProgress();
		}
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{
				System.out.println(getStamp(str) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(null);
				writeLog(getStamp(str) + msg, fn);
			}
		}
	}

	public static void audit_log(String msg, FixUninstaller fi)
	{
		String str = null;
		FixConfig oc = new FixConfig();
		// Set status message to FixInstaller object
		if (fi != null)
		{
			fi.setMessage(msg);
			fi.updateProgress();
		}
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{
				System.out.println(getStamp(str) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(null);
				writeLog(getStamp(str) + msg, fn);
			}
		}
	}
	
	public static void audit_log(String msg, ProfileBackupProcess bp)
	{
		String str = null;
		FixConfig oc = new FixConfig();
		// Set status message to FixInstaller object
		if (bp != null)
		{
			bp.setMessage(msg);
			bp.updateProgress();
		}
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{
				System.out.println(getStamp(str) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(null);
				writeLog(getStamp(str) + msg, fn);
			}
		}
	}

	public static void audit_log(String msg, ProfileRestoreProcess rp)
	{
		String str = null;
		FixConfig oc = new FixConfig();
		// Set status message to FixInstaller object
		if (rp != null)
		{
			rp.setMessage(msg);
			rp.updateProgress();
		}
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{
				System.out.println(getStamp(str) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(null);
				writeLog(getStamp(str) + msg, fn);
			}
		}
	}
	
	public static void audit_log(String msg, String name, String cnf_file)
	{
		FixConfig oc = new FixConfig();
		if (oc.is_audit_log())
		{
			if (!oc.is_audit_log_file())
			{			
				System.out.println(getStamp(name) + msg);
			}
			else // write to file
			{
				checkDir(oc.get_audit_log_dir());
				String fn = oc.get_audit_log_dir() + File.separator + getFileName(name);
				writeLog(getStamp(name) + msg, fn);				
			}
		}

	}
	
	private static String getStamp(String name)
	{
		String str = "[";
		if (name != null)
			str += name + ":";
		Date currentTime = new Date();
		str += currentTime.toString() + "] ";
		return str;
	}
	
	private static String getFileName(String name)
	{
		String file_name = "wmfixer_";
		//Get timestamp for file name
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
			
		file_name += dateString + ".log";
		if (name != null)
		{
			String str = removeInvalidCharacters(name) + "_" + file_name;
			file_name = str;
		}
		
		return file_name;		
	}
	private static String removeInvalidCharacters(String name)
	{
		String s = name.replace(':','_');
		String n = s.replace('.','_');
		return n;
	}
	private static void checkDir(String dir)
	{
		try
		{
			File f = new File(dir);
			if (f != null)
				if (!f.exists())
					f.mkdirs();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private static void writeLog(String msg, String name)
	{
		try
		{
			FileWriter fw = new FileWriter(name,true);
			fw.write(msg+"\n");
			fw.flush();
			fw.close();
			fw = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
