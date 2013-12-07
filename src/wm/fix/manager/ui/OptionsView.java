package wm.fix.manager.ui;

import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import wm.fix.manager.data.ConfigReader;
import wm.fix.manager.data.ConfigWriter;
import wm.fix.manager.data.LanguageCnf;
import wm.fix.manager.data.LanguageSelector;
import wm.fix.manager.util.FixUtil;

public class OptionsView extends Dialog
{
	Button m_buttonSubmit = null;
    Button m_buttonCancel = null; 
    Shell dialog 		  = null;
    Combo m_combo_lang 	  = null;
    
	private String m_lang = "";
	private FixManagerUIResourceBundle m_resource = null;
    private LanguageCnf[] m_lang_list = null;
    private LanguageSelector m_ls = null;
    private String m_selected_lang = null;
    private int m_result = SWT.CANCEL;
    
    public OptionsView(Shell parent) 
    {
    	super(parent, SWT.APPLICATION_MODAL);
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}
		// Read language configuration
		try
		{
			ConfigReader cr = new ConfigReader();
			m_ls = cr.loadLanguageConfig("config/language.xml");
			if (m_ls != null)
				m_lang_list = m_ls.getLanguageList();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
    
	public int open()
	{	
		dialog = new Shell(this.getParent(),SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		dialog.setText(m_resource.getString(FixManagerConstants.CNF_OPTIONS_DLG_TITLE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		dialog.setLayout(gridLayout);
		dialog.setSize(new Point(300, 150));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_WINDOW_ICON));
		
		this.createWidgets(dialog);
		
		dialog.open();
		Display display = this.getParent().getDisplay();
		while(!dialog.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return m_result;
	}
	public void updateResult(int val)
	{
		m_result = val;
	}
	public void updateLanguageConfig()
	{
		if (m_ls != null)
		{
			//m_ls.print();
			ConfigWriter cw = new ConfigWriter();
			cw.generateLangConfXML("config/language.xml", m_ls);			
		}
	}
	public void changeLanguage()
	{
		if (m_ls != null)
		{
			m_ls.activateLanguageByName(m_selected_lang);
			LanguageCnf lc = m_ls.getLanguageByName(m_selected_lang);
			if (lc != null)
			{
				Properties p = System.getProperties();
				p.put("languagefile",lc.getLangFile());				
			}
		}
	}
    protected void createWidgets(Shell shell)
    {
    	
		Label label = new Label(dialog,SWT.NONE);
		label.setText(m_resource.getString(FixManagerConstants.CNF_OPTIONS_DLG_LANG_SEL));
		
		setupLanguageCombo();
		
    	m_buttonSubmit = new Button(dialog,SWT.PUSH);
    	m_buttonSubmit.setText(m_resource.getString(FixManagerConstants.CNF_BTN_SUBMIT));
    	m_buttonSubmit.addMouseListener(new btnSubmitMouseListener(this));
    	
		m_buttonCancel = new Button(dialog, SWT.PUSH);
		m_buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CANCEL));
		m_buttonCancel.addMouseListener(new btnCancelMouseListener(this));
			
    }

	/**
	 * setupLanguageCombo method creates  combo control to
	 * select language preference
	 * @param 
	 */
	private void setupLanguageCombo()
	{
		m_combo_lang = new Combo(dialog,SWT.DROP_DOWN);
		// Setup Fix list selector
		m_combo_lang.addSelectionListener(
				new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						m_selected_lang = m_combo_lang.getText();
					}				
				}
		);	
		////////////////////////////////////
		// Initialize language combo list
		if (m_lang_list != null)
		{
			int idx = 0;
			for (int i = 0; i < m_lang_list.length; i++)
			{			  
				m_combo_lang.add(m_lang_list[i].getName());
				if (m_lang_list[i].isActive())
					idx = i;
			}
			m_combo_lang.select(idx);			
		}
	}
    
    ///////////////////////////////////////////////////
    // Event handler classes
    ///////////////////////////////////////////////////
	class btnSubmitMouseListener implements MouseListener
	{
		OptionsView m_optv = null;
		
		public btnSubmitMouseListener(OptionsView optv)
		{
			m_optv = optv;
		}
		public void mouseDown(MouseEvent e)
		{
			m_optv.changeLanguage();
			m_optv.updateLanguageConfig();
			m_optv.updateResult(SWT.OK);
			m_optv.dialog.dispose();
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
	}
    
	class btnCancelMouseListener implements MouseListener
	{
		OptionsView m_optv = null;
		public btnCancelMouseListener(OptionsView s)
		{
			m_optv = s;
		}
		public void mouseDown(MouseEvent e)
		{
			m_optv.updateResult(SWT.CANCEL);
			m_optv.dialog.dispose();
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
	}
    
    
}
