package wm.fix.manager.opt.ui;

import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.opt.install.UninstallerTask;
import wm.fix.manager.ui.FixManagerConstants;
import wm.fix.manager.ui.FixManagerUIResourceBundle;
import wm.fix.manager.util.FixUtil;

/*
 * Package Name : wm.fix.manager.ui
 * Class Name   : FixUninstallProgress
 * 
 * Created by   : Igor Androsov
 * Created on   : May 15, 2006
 * Change log:
 *
 * Date			Author				Description
 * --------------------------------------------------------------------------------
 * 2006/05/15	Igor Androsov		version 1.0
 * 				webMethods
 *
 * ---------------------------------------------------------------------------------
 */


public class FixUninstallProgress extends Dialog
{
    private boolean m_install = true;
    private String newline = "\n";
    private String m_message = "";
    private UninstallerTask m_task = null;
    FixData m_fd = null;
    FixProfile m_fp = null;
    
    private ProgressBar m_progress = null;
    private Text m_log_text = null;
    Button m_buttonStart = null;
    Button m_buttonCancel = null; 
    Shell dialog = null;
    // Refernce itself in class
    FixUninstallProgress m_fip = null;
	private String m_lang = "";
	private FixManagerUIResourceBundle m_resource = null;
    
    public FixUninstallProgress(Shell parent, FixData fd, boolean install) 
    {
    	super(parent, SWT.APPLICATION_MODAL);
    	m_install = install;
    	m_fd = fd;
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}
    	
    }
    
    public FixUninstallProgress(Shell parent, FixProfile fp, boolean install) 
    {
    	super(parent, SWT.APPLICATION_MODAL);
    	m_install = install;
    	m_fp = fp;
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}
    	
    }
 
	public int open()
	{
		int rc = 0;
		m_fip = this;
		dialog = new Shell(this.getParent(),SWT.RESIZE|SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		if (m_install)
			dialog.setText(m_resource.getString(FixManagerConstants.CNF_INSTALLER_DLG_TITLE));
		else
			dialog.setText(m_resource.getString(FixManagerConstants.CNF_UNINSTALLER_DLG_TILE));
		//GridLayout gridLayout = new GridLayout();
		//gridLayout.numColumns = 1;
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		dialog.setLayout(formLayout);
		dialog.setSize(new Point(550, 350));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_GEAR));
		
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
	
    public void updateProgress()
    {
        if (!m_progress.isDisposed()) 
        {
//        	System.out.println("### "+Integer.toString(m_task.getCurrent()));
            //int v = m_progress.getSelection() + m_task.getCurrent();
            int v = m_task.getCurrent();
            if (v > m_progress.getMaximum())
                v = m_progress.getMinimum();
            
            m_progress.setSelection(v);
        } 

        if (!m_log_text.isDisposed()) 
        {
        	String s = m_task.getMessage();
        	if (s != null && !s.equals(m_message))
        	{
        		m_message = s;
        		m_log_text.append(s + newline);
        	}
        }
        if (m_task.isDone()) {
        	m_buttonStart.setEnabled(true);
            //setCursor(null); //turn off the wait cursor
            m_progress.setSelection(m_progress.getMinimum());
        }   
        
    }
    
    public void finishProgress()
    {
        m_buttonStart.setEnabled(true);
        //setCursor(null); //turn off the wait cursor            
        m_progress.setSelection(m_task.getLengthOfTask());    	
    }
    
    public void runTask()
    {
    	FixInstallerTaskThread task = new FixInstallerTaskThread(dialog.getDisplay());  
    	if (m_buttonStart != null && !m_buttonStart.isDisposed())
    		m_buttonStart.setEnabled(false);
    	task.run();   	
    }
    public void setProgressBar()
    {
    	if (!m_progress.isDisposed() && m_task != null)
    	{
    		m_progress.setMinimum(0);
    		m_progress.setMaximum(m_task.getLengthOfTask());
    		m_progress.setSelection(m_task.getCurrent());
    	}    	
    }
    
    protected void createWidgets(Shell shell)
    {
    	m_progress = createHProgressBar(shell,0, 1, 100);
    	FormData formData = new FormData();
    	//formData.top = new FormAttachment(55,60);
    	//formData.bottom = new FormAttachment(100,-5);
    	formData.left = new FormAttachment(-3,100);
    	formData.right  = new FormAttachment(100,-3);
    	m_progress.setLayoutData(formData);
    	
    	m_log_text = createLogTextBox(shell);
    	
    	FormData formData1 = new FormData();
    	formData1.top = new FormAttachment(m_progress,10);
    	formData1.bottom = new FormAttachment(95,0);
    	formData1.left = new FormAttachment(-3,100);
    	formData1.right  = new FormAttachment(100,-3);
    	m_log_text.setLayoutData(formData1);
    	
		m_buttonStart = new Button(shell,SWT.PUSH);
		m_buttonStart.setText(m_resource.getString(FixManagerConstants.CNF_BTN_START));
		m_buttonStart.addMouseListener(new startMouseListener(this));
    	FormData btn_formData = new FormData();
    	btn_formData.top = new FormAttachment(0,0);
    	btn_formData.width=FixManagerConstants.PROGRESS_BTN_WIDTH;
    	m_buttonStart.setLayoutData(btn_formData);
		
		m_buttonCancel = new Button(shell, SWT.PUSH);
		m_buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CANCEL));
		m_buttonCancel.addMouseListener(new btnCancelMouseListener(shell));
	  	FormData btn_formData1 = new FormData();
    	btn_formData1.top = new FormAttachment(m_buttonStart,10);
    	btn_formData1.width=FixManagerConstants.PROGRESS_BTN_WIDTH;
    	m_buttonCancel.setLayoutData(btn_formData1);
			
    }

    protected void disposeWidgets()
    {
    	m_progress.dispose();
    	m_log_text.dispose();	
		m_buttonStart.dispose();
		m_buttonCancel.dispose();				  	
    }
    protected Text createLogTextBox(Shell parent)
    {
    	Text tx = new Text(parent,SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER|SWT.READ_ONLY);
    	tx.setSize(200,200);
    	return tx;
    }
    protected ProgressBar createHProgressBar(Shell parent, int min, int current, int max)
    {
    	return createProgressBar(parent,SWT.HORIZONTAL,min,current,max);
    }

    private ProgressBar createProgressBar(Shell parent, int style, int min, int current, int max )
    {
    	ProgressBar pb = new ProgressBar(parent,style);
    	if(min>=0)
    		pb.setMinimum(min);
    	if(max>=0)
    		pb.setMaximum(max);
    	if(current>=0)
    		pb.setSelection(current);
    	pb.setBounds(10,100,350,20);
    	
    	return pb;
    }
  
	class startMouseListener implements MouseListener
	{
		FixUninstallProgress m_fip = null;
		
		public startMouseListener(FixUninstallProgress fip)
		{
			m_fip = fip;
		}
		public void mouseDown(MouseEvent e)
		{
			m_fip.runTask();
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
			d.dispose();
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
	}
    
	
	protected class FixInstallerTaskThread extends Thread 
	{
	    protected Display display;

	    public FixInstallerTaskThread(Display display) 
	    {
	        this.display = display;
	    }

	    public void run() 
	    {	    
	        try 
	        {
	        	if (!display.isDisposed()) 
	        	{
	                display.syncExec(new Runnable() {
	                	public void run() {	                                    	
	                			if (m_fp != null)
	                            {
	                                m_task = new UninstallerTask(m_fp,m_fip);
	                                setProgressBar();
	                                m_task.go();
	                            }
	                            else if (m_fd != null)
	                            {
	                                m_task = new UninstallerTask(m_fd,m_fip);
	                                setProgressBar();
	                                m_task.go();
	                            }
	                    }
	                });
	            }	                      	                    
	        }
	        catch (Exception e) {
	             e.printStackTrace();
	        }
	    }
	} // End of FixInstallerTaskThread class
	
}
