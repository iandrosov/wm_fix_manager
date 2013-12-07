package wm.fix.manager.data;

public class LanguageCnf 
{
	private String m_lang_name = null;
	private String m_desc = null;
	private boolean m_active = false;
	private String m_lang_file = null;

	LanguageCnf()
	{
		m_lang_name = "";
		m_desc = "";
		m_lang_file = "";
		m_active = false;
	}
	public String getName(){return m_lang_name;}
	public void setName(String str){m_lang_name = str;}
	public String getDesc(){return m_desc;}
	public void setDesc(String str){m_desc = str;}
	public String getLangFile(){return m_lang_file;}
	public void setLangFile(String str){m_lang_file = str;}
	public boolean isActive(){return m_active;}
	public void setActive(boolean b){m_active = b;}
	
	public void print()
	{
		System.out.println("#### Print Language Data #####"); 
		System.out.println("m_name="+m_lang_name);
		System.out.println("m_desc="+m_desc);
		System.out.println("m_lang_file="+m_lang_file);
		if (m_active)
			System.out.println("m_active=TRUE");
		else
			System.out.println("m_active=FALSE");
	}	
}
