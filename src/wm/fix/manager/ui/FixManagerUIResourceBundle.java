/*
 * Package Name : wm.fix.manager.ui
 * Class Name   : FixManagerUIResourceBundle
 * 
 * Created by   : Igor Androsov
 * Created on   : May 17, 2006
 * Change log:
 *
 * Date			Author				Description
 * --------------------------------------------------------------------------------
 * 2006/05/17	Igor Androsov		version 1.0
 * 				webMethods
 *
 * ---------------------------------------------------------------------------------
 */
package wm.fix.manager.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;

public class FixManagerUIResourceBundle extends PropertyResourceBundle
{
	public FixManagerUIResourceBundle(InputStream is) throws IOException
	{
			super(is);
			// Test case
			
			
	}
	public String getLocalizedString(String key)
	{
		String res = "";
		
		//String str = (String)handleGetObject(key);
		try
		{
			res = getString(key);
			//res = new String(str.getBytes(),"ISO 8859-1");
		}
		catch (Exception e)
		{
			res = "UNKNOWN";
		}
		return res;
	}
	public void dumpStrings()
	{
		System.out.println("##### Dump string table #####");
		Enumeration en = this.getKeys();
		while(en.hasMoreElements())
		{
			String key = (String)en.nextElement();
			String val = (String)this.getString(key);
			System.out.println(key+" = "+val);
		}
	}
	
}
