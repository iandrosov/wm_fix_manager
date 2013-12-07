package wm.fix.manager.ui;


import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import wm.fix.manager.util.FixUtil;

public class AboutView   extends Dialog
{
    Text m_log_text = null;
    Button m_buttonCancel = null; 
    Shell dialog = null;
    private String m_lang = ""; //$NON-NLS-1$
    private FixManagerUIResourceBundle m_resource = null;
    
    public AboutView(Shell parent) 
    {
    	super(parent, SWT.APPLICATION_MODAL);
    	Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile"); //$NON-NLS-1$
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}    	
    }

	public int open()
	{
		int rc = 0;
		
		dialog = new Shell(this.getParent(),SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		dialog.setText(m_resource.getString(FixManagerConstants.CNF_ABOUT_DLG_TITLE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		dialog.setLayout(gridLayout);
		dialog.setSize(new Point(420, 290));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_WINDOW_ICON));
		
		this.createWidgets(dialog);
		
		dialog.open();
		Display display = this.getParent().getDisplay();
		while(!dialog.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return rc;
	}
	
    protected void createWidgets(Shell shell)
    {    	
    	m_log_text = createLogTextBox(shell);
    	m_log_text.setText(getAboutText());
    	
		m_buttonCancel = new Button(shell, SWT.PUSH);
		m_buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_OK));
		m_buttonCancel.addMouseListener(new btnCancelMouseListener(shell));
    }

    private String getAboutText()
    {    	
    	String msg = ""; //$NON-NLS-1$
    	/*
    	try
    	{
    		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("config/copyright.txt"))); //$NON-NLS-1$
    		String line = br.readLine();
    		while (line != null)
    		{
    			msg += line + "\n"; //$NON-NLS-1$
    			line = br.readLine();
    			
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	*/
    	// Backup copyright in case file missing
    	if (msg.length() < 100)
    	{
    		msg += "webMethods Fix Manager\n\n"; //$NON-NLS-1$
    		msg += "This software created by Igor Androsov\n"; //$NON-NLS-1$
    		msg += "and distributed by AI Solitions Inc.\n"; //$NON-NLS-1$
    		msg += "For information or recomendations contact Igor by e-mail: iandrosov@yahoo.com\n\n"; //$NON-NLS-1$
 
    		msg += "Version: 1.0.0\n\n"; //$NON-NLS-1$
    	
    		msg += "(c) Copyright AI Solitions Inc. and Igor Androsov 2005 2006.  All rights reserved.\n\n"; //$NON-NLS-1$
    		
    		msg += "This product uses software libraries developed by:\n"; //$NON-NLS-1$
    		msg += "Apache Software Foundation http://www.apache.org/\n"; //$NON-NLS-1$
    		msg += "Eclipse contributors Visit http://www.eclipse.org/\n"; //$NON-NLS-1$   		
    		msg += "webMethods Inc http://www.webmethods.com/\n"; //$NON-NLS-1$
    		msg += "webMethods is a registered trademark of webMethods, Inc.\n"; //$NON-NLS-1$
    	}
    	return msg;
    }
    
    protected void disposeWidgets()
    {
    	m_log_text.dispose();	
		m_buttonCancel.dispose();				  	
    }
    
    protected Text createLogTextBox(Shell parent)
    {
    	Text tx = new Text(parent,SWT.MULTI|SWT.BORDER|SWT.READ_ONLY);
    	tx.setSize(200,200);
    	return tx;
    }
 
	class btnCancelMouseListener implements MouseListener
	{
		Shell d = null;
		public btnCancelMouseListener(Shell s)
		{
			d = s;
		}
		public void mouseDown(MouseEvent e)
		{
			d.dispose();
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
	}
    
}
