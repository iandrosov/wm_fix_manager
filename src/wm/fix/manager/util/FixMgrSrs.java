package wm.fix.manager.util;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import wm.fix.manager.ui.FixManagerConstants;
import wm.fix.manager.ui.FixManagerUIResourceBundle;


public class FixMgrSrs extends Dialog
{
	Shell dialog = null;
    Text m_log_text = null;
    Button m_buttonCancel = null; 
    Button m_buttonOK = null; 
    private String m_lang = ""; //$NON-NLS-1$
    private FixManagerUIResourceBundle m_resource = null;
    private String m_key = null;
    private int m_btn = 0;
	public FixMgrSrs(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL);
    	Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile"); //$NON-NLS-1$
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}    	
	}

	public String getKey()
	{
		
		return m_key;
	}
	
	public int open()
	{
		int rc = 0;
		
		dialog = new Shell(this.getParent(),SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		dialog.setText("Fix Manager License");
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		dialog.setLayout(gridLayout);
		dialog.setSize(new Point(420, 100));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_WINDOW_ICON));
		
		this.createWidgets(dialog);
		
		dialog.open();
		Display display = this.getParent().getDisplay();
		while(!dialog.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		rc = m_btn;
		return rc;
		
	}
	
    protected void createWidgets(Shell shell)
    {   
    	Label label = new Label(dialog,SWT.NONE);
		label.setText("Enter License:");    	
    	m_log_text = new Text(dialog,SWT.SINGLE|SWT.BORDER);
    	
    	
		m_buttonOK = new Button(shell, SWT.PUSH);
		m_buttonOK.setText(m_resource.getString(FixManagerConstants.CNF_BTN_OK));
		m_buttonOK.addMouseListener(new btnOKMouseListener(shell,m_key));
    	
		m_buttonCancel = new Button(shell, SWT.PUSH);
		m_buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CANCEL));
		m_buttonCancel.addMouseListener(new btnCancelMouseListener(shell));
    }
	
    protected void disposeWidgets()
    {
    	m_log_text.dispose();	
		m_buttonCancel.dispose();
		m_buttonOK.dispose();
    }
      
    class btnOKMouseListener implements MouseListener
    {
    	Shell d = null;
    	String m_t = null;
    	
    	public btnOKMouseListener(Shell dlg, String t)
    	{
    		d    = dlg;
    		m_t  = t;
    	}
    	public void mouseDown(MouseEvent e)
    	{
    		m_btn = 1;
    		m_key = m_log_text.getText();
    		d.dispose();
    	}
    	public void mouseDoubleClick(MouseEvent e){}
    	public void mouseUp(MouseEvent e){}
    	
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
    		m_btn = 0;
    		d.dispose();
    	}
    	public void mouseDoubleClick(MouseEvent e){}
    	public void mouseUp(MouseEvent e){}
    	
    }

}


