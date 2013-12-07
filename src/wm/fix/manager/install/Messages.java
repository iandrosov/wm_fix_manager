package wm.fix.manager.install;

import java.io.FileInputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import wm.fix.manager.ui.FixManagerUIResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "wm.fix.manager.install.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/* Original method save for later use or erplace
	 * 
	 *
	public static String getString(String key) {
		// TODO Auto-generated method stub
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	******/
	
	public static String getString(String key) 
	{
		Properties p = System.getProperties();
		String m_lang = p.getProperty("languagefile");
		FixManagerUIResourceBundle  m_resource = null;
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
			if (m_resource != null)
			{
				return m_resource.getString(key);
			}
			else
			{
				try {
					return RESOURCE_BUNDLE.getString(key);
				} catch (MissingResourceException e) {
					return '!' + key + '!';
				}
			}
		}
		catch(Exception e)
		{
			return '!' + key + '!';
		}		
		
	}

}
