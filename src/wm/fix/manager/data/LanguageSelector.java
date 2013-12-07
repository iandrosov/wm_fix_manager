package wm.fix.manager.data;

import java.util.ArrayList;

public class LanguageSelector 
{
	private ArrayList m_lang_list;
	
	LanguageSelector()
	{
		m_lang_list = new ArrayList();
	}

	public void addLanguage(LanguageCnf lc)
	{
		if (lc == null)
			return;
		if (m_lang_list == null)
			m_lang_list = new ArrayList();

		m_lang_list.add(lc);
	}
	
	public void removeLanguage(String name)
	{
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				LanguageCnf lc = (LanguageCnf)m_lang_list.get(i);
				if (lc.getName().equals(name))
					m_lang_list.remove(i);
			}
		}
	}
	
	public LanguageCnf[] getLanguageList()
	{
		Object[] obj = m_lang_list.toArray();		
		LanguageCnf[] lc = new LanguageCnf[obj.length];
		for (int i=0; i < obj.length; i++)
		{			
			lc[i] = (LanguageCnf)obj[i];
		}
		
		return lc;
	}
	public LanguageCnf getActiveLanguage()
	{
		LanguageCnf lc = null;
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				lc = (LanguageCnf)m_lang_list.get(i);
				if (lc.isActive())
				{
					return lc;
				}
			}
		}
		return lc;
	}
	public void deactivateLangugaeByName(String name)
	{
		LanguageCnf lc = null;
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				lc = (LanguageCnf)m_lang_list.get(i);
				if (lc.getName().equals(name))
				{
					lc.setActive(false);
					m_lang_list.set(i,lc);
				}
			}
		}
		
	}
	
	public void activateLanguageByName(String name)
	{
		LanguageCnf lc = null;
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				lc = (LanguageCnf)m_lang_list.get(i);
				if (lc.getName().equals(name))
				{
					lc.setActive(true);
					m_lang_list.set(i,lc);
				}
				else
				{
					lc.setActive(false);
					m_lang_list.set(i,lc);					
				}
			}
		}
		
	}
	
	public LanguageCnf getLanguageByName(String name)
	{
		LanguageCnf lc = null;
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				lc = (LanguageCnf)m_lang_list.get(i);
				if (lc.getName().equals(name))
					return lc;
			}
		}
		return lc;
	}
	
	public void print()
	{
		if (m_lang_list != null)
		{
			for (int i=0; i < m_lang_list.size(); i++)
			{
				LanguageCnf lc = (LanguageCnf)m_lang_list.get(i);
				if (lc!= null)
					lc.print();
			}
		}		
	}
}
