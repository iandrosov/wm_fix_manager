import java.util.Properties;

import wm.fix.manager.data.ConfigReader;
import wm.fix.manager.data.FixProfileData;
import wm.fix.manager.data.LanguageCnf;
import wm.fix.manager.data.LanguageSelector;
import wm.fix.manager.install.FixInstaller;
import wm.fix.manager.ui.FixManagerView;
import wm.fix.manager.util.FixMgrValidate;

public class WmFixer 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		getParameters(args);
		Properties p = System.getProperties();
		String con = p.getProperty("console");
		boolean console = false;
		if (con.toLowerCase().equals("true"))
			console = true;
		String file = p.getProperty("cnf","ini.xml");
		String name = p.getProperty("profile","Production");
		
		FixMgrValidate fmv = new FixMgrValidate(console);
		//System.out.println(fmv.generate("00-50-56-C0-00-01","96691"));
		//String s = fmv.generate("00-50-56-C0-00-01","96691");
		//System.out.println(s);
		//if (!fmv.system_check())
		//	System.exit(0);
		
		ConfigReader cnf = new ConfigReader();
		if (console)
		{
			// Use console
			try
			{
				FixProfileData fpd = cnf.loadXml(file);
				FixInstaller fi = new FixInstaller();
				fi.installFixProfile(fpd, name);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			// Use GUI
			FixManagerView fix_mgr = new FixManagerView(file);
			fix_mgr.createUI();
		}
		
	}

	/**
	 * getParameters helper method to handle input parameters
	 * for WmFixer main class.
	 * @param args
	 */
	private static void getParameters(String[] args)
	{
		Properties p = System.getProperties();
		// Set defaults
		p.put("console", Boolean.FALSE.toString());
		p.put("auditlog",Boolean.TRUE.toString());
		p.put("logtofile",Boolean.FALSE.toString());
		LanguageCnf lc = null;
		try
		{
			ConfigReader cr = new ConfigReader();
			LanguageSelector ls = cr.loadLanguageConfig("config/language.xml");
			if (ls != null)
				lc = ls.getActiveLanguage();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (lc != null)
			p.put("languagefile",lc.getLangFile());
		for (int i=0; i < args.length; i++)
		{
			if (args[i].equals("-console"))
				p.put("console", Boolean.TRUE.toString());
			else if (args[i].equals("-cnf"))
			{
				p.put("cnf",args[i+1]);
				//System.out.println("cnf "+args[i+1]);
			}
			else if (args[i].equals("-audit"))
			{
				p.put("auditlog",Boolean.TRUE.toString());
				//System.out.println("auditlog ");
			}
			else if (args[i].equals("-logtofile"))
			{
				p.put("logtofile",Boolean.TRUE.toString());
				//System.out.println("logtofile");
			}
			else if (args[i].equals("-logdir"))
			{
				p.put("logdir",args[i+1]);	
				//System.out.println("logdir "+args[i+1]);
			}
			else if (args[i].equals("-profile"))
			{
				p.put("profile",args[i+1]);	
				//System.out.println("profile "+args[i+1]);
			}
		}	
		System.setProperties(p);
		
	}
	
}
