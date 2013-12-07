package wm.fix.manager.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixDataJDBC;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.install.FixDBScript;
import wm.fix.manager.util.FixUtil;
import wm.fix.manager.util.FixWmUtil;

public class FixDataView extends Dialog
{
	
	public FixData m_fix_data = null;
	public FixProfile m_fp 	= null;
	
	// Local control set
	// Controls for Fix Location
	private Text m_fix_name = null;
	private Combo m_combo_fix_name = null;
	private Text m_fix_file = null;
	private Text m_src = null;
	private Text m_trg = null;
	
	//Controls for Options
	private Button m_btn_copy = null;
	private Button m_btn_unzip = null;
	private Button m_btn_pkg = null;
	private Text m_pkg_name = null;
	
	//Controls for JDBC
	private Text m_jdbc_driver = null;
	private Text m_jdbc_url = null;
	private Text m_db_user = null;
	private Text m_db_pwd = null;
	private Button m_db_check = null;
	private Combo m_db_type = null;
	private Button m_db_test = null;
	
	private String m_lang = "";
	private FixManagerUIResourceBundle m_resource = null;
	
	public FixDataView(Shell parent, int style, FixData fd, FixProfile fp)
	{
		super(parent, style);
		m_fix_data = fd;
		m_fp = fp;	
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}		
	}
	
	public FixData open()
	{
		Shell dialog = new Shell(this.getParent(),SWT.RESIZE|SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_DLG_TITLE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		dialog.setLayout(gridLayout);
		//dialog.setLayout(new RowLayout());
		dialog.setSize(new Point(460, 310));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_GEAR));
		
		this.showWidgets(dialog);
		
		dialog.open();
		Display display = this.getParent().getDisplay();
		while(!dialog.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		return m_fix_data;
		
	}
	
	private void showWidgets(final Shell dialog)
	{
		TabFolder tabFolder = new TabFolder(dialog, SWT.NONE);
		tabFolder.setLayout(new RowLayout());
		//new Label(dialog,SWT.NONE);
		
		TabItem item = new TabItem(tabFolder,SWT.NONE);
		item.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_TAB_LOC));
		item.setControl(getTabControlLocation(tabFolder,dialog));
		
		TabItem itemInst = new TabItem(tabFolder,SWT.NONE);
		itemInst.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_TAB_OPT));
		itemInst.setControl(getTabControlOptions(tabFolder));

		TabItem itemJDBC = new TabItem(tabFolder,SWT.NONE);
		itemJDBC.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_TAB_JDBC));
		itemJDBC.setControl(getTabControlJDBC(tabFolder));
		
		initFixData();

		Button bt = new Button(dialog,SWT.PUSH);
		bt.setText(m_resource.getString(FixManagerConstants.CNF_BTN_SUBMIT));
		GridData gd = getButtonGridData();
		bt.setLayoutData(gd);
		bt.addMouseListener(new submitMouseListener(dialog,m_fix_data, this));
		Button buttonCancel = new Button(dialog, SWT.PUSH);
		buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CANCEL));
		buttonCancel.setLayoutData(gd);
		buttonCancel.addMouseListener(new btnCancelMouseListener(dialog));				
	}

	private GridData getButtonGridData()
	{
		GridData gridData = new GridData();
		//gridData.horizontalAlignment = GridData.CENTER;
		//gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.minimumWidth = 110;
		return gridData;
	}
	
	public String getName()
	{
		if (m_fix_name != null)
			return m_fix_name.getText();
		else
			return m_combo_fix_name.getText();
	}
	public String getFile()	{return m_fix_file.getText();}
	public String getSourceDir(){return m_src.getText();}
	public String getTargetDir(){return m_trg.getText();}
	public boolean getCopyFlag(){return m_btn_copy.getSelection();}
	public boolean getUnzipFlag(){return m_btn_unzip.getSelection();}
	public boolean getPkgFlag(){return m_btn_pkg.getSelection();}
	public String getPkgName(){	return m_pkg_name.getText();}
	public boolean getDBFlag(){	return m_db_check.getSelection();}
	public String getJDBCDriver(){return m_jdbc_driver.getText();}
	public String getDBURL(){return m_jdbc_url.getText();}
	public String getDBUser(){return m_db_user.getText();}
	public String getDBPassword(){return m_db_pwd.getText();}
	public String getDBType(){return m_db_type.getText();}
	
	private Control getTabControlOptions(TabFolder tabFolder)
	{
		Composite comp = new Composite(tabFolder,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		comp.setLayout(gridLayout);
		//comp.setLayout(new FillLayout(SWT.VERTICAL));
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_COPY));
		m_btn_copy = new Button(comp,SWT.CHECK);		
		m_btn_copy.addMouseListener(new btnCopyMouseListener(tabFolder.getShell()));

		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_UNZIP));
		m_btn_unzip = new Button(comp,SWT.CHECK);		
		m_btn_unzip.addMouseListener(new btnUnzipMouseListener(tabFolder.getShell()));
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_PKG));
		m_btn_pkg = new Button(comp,SWT.CHECK);		
				
		m_pkg_name = new Text(comp,SWT.SINGLE|SWT.BORDER);
		m_pkg_name.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_PKGNAME));
		m_pkg_name.setEnabled(false);
		
		m_btn_pkg.addMouseListener(new btnPkgMouseListener(tabFolder.getShell(),m_pkg_name,m_btn_pkg,m_fix_file));
		return comp;
	}
	
	private Control getTabControlLocation(TabFolder tabFolder, Shell d)
	{
		Composite comp = new Composite(tabFolder,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		comp.setLayout(gridLayout);
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_FIX_NAME));
		if (m_fix_data.getFixName().length() > 0)
			m_fix_name = new Text(comp,SWT.SINGLE|SWT.BORDER);
		else
		{			
			setupFixCombo(comp);
		}
		new Label(comp,SWT.NONE).setText("");
		// File selection
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_FIX_FILE));
		m_fix_file = new Text(comp,SWT.SINGLE|SWT.BORDER);
		m_fix_file.setSize(300,50);
		Button btn_FixFile = new Button(comp,SWT.PUSH);
		btn_FixFile.setText("<<");
		Image img = FixUtil.createImage(d.getDisplay(), FixManagerConstants.FIX_MGR_OPEN_FILE);
		btn_FixFile.setImage(img);				
		btn_FixFile.addMouseListener(new FixFileMouseListener(tabFolder.getShell()));

		// Set fix source directory
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_SRC_DIR));
		m_src = new Text(comp,SWT.SINGLE|SWT.BORDER);
		Button btn_SourceDir = new Button(comp,SWT.PUSH);
		btn_SourceDir.setText("<<");
		btn_SourceDir.setImage(img);
		btn_SourceDir.addMouseListener(new SourceDirMouseListener(tabFolder.getShell()));
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_TRG_DIR));
		m_trg = new Text(comp,SWT.SINGLE|SWT.BORDER);
		Button btn_TargetDir = new Button(comp,SWT.PUSH);
		btn_TargetDir.setText("<<");
		btn_TargetDir.setImage(img);
		btn_TargetDir.addMouseListener(new TargetDirMouseListener(tabFolder.getShell()));
		
		return comp;		
	}

	private Control getTabControlJDBC(TabFolder tabFolder)
	{
		Composite comp = new Composite(tabFolder,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		comp.setLayout(gridLayout);
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_SCRIPT));
		m_db_check = new Button(comp,SWT.CHECK);
		//m_db_check.setText("DB");
		m_db_check.addMouseListener(new DBButtonMouseListener(tabFolder.getShell()));
		
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_TYPE));
		m_db_type = new Combo(comp,SWT.DROP_DOWN);
		initDBTypeCombo();		
		
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_JDBC_DRV));
		m_jdbc_driver = new Text(comp,SWT.SINGLE|SWT.BORDER);
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_JDBC_URL));
		m_jdbc_url = new Text(comp,SWT.SINGLE|SWT.BORDER);
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_USER));
		m_db_user = new Text(comp,SWT.SINGLE|SWT.BORDER);
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_PWD));
		m_db_pwd = new Text(comp,SWT.SINGLE|SWT.BORDER|SWT.PASSWORD);
		new Label(comp,SWT.NONE).setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_TEST));
		m_db_test = new Button(comp,SWT.PUSH);
		m_db_test.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_BTN_TEST));
		m_db_test.addMouseListener(new DBTestButtonMouseListener(tabFolder.getShell()));
		
		// Setup JDBC Driver types
		m_db_type.addSelectionListener(
				new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						String str = m_db_type.getText();					    
						if (m_fix_data != null && m_fix_data.jdbc != null)
						{
							m_jdbc_driver.setText(m_fix_data.jdbc.getJDBCDriverName(str));
							m_jdbc_url.setText(m_fix_data.jdbc.getJDBCURL(str));
						}						
					}				
				}
		);
		
		return comp;		
	}

	/**
	 * setupFixCombo method creates  combo control to set
	 * Fix name and its selection listener to update
	 * fields when selection changes
	 * @param comp - Composite SWT control object
	 */
	private void setupFixCombo(Composite comp)
	{
		m_combo_fix_name = new Combo(comp,SWT.DROP_DOWN);
		// Setup Fix list selector
		m_combo_fix_name.addSelectionListener(
				new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						String str = m_combo_fix_name.getText();
						if (m_fp != null)
						{
							String file = m_fp.getSourceDir()+File.separator+str+".zip";
							File f = new File(file);
							if (!f.exists())
							{
								file = m_fp.getSourceDir()+File.separator+str+".jar";
								f = new File(file);
							}
							if (f.exists())
							{
								m_fix_file.setText(f.getPath());
								m_src.setText(m_fp.getSourceDir());
								m_trg.setText(m_fp.getTargetDir());
							}
						}
					}				
				}
		);	
	}
	
	/**
	 * initDBTypeCombo initialize list of available
	 * Database types to select from.
	 * @param
	 */
	private void initDBTypeCombo()
	{
		if (m_db_type == null)
			return;
		FixDataJDBC jdbc = new FixDataJDBC ();
		String[] db = jdbc.getAllDBTypes();
		for (int i = 0; i < db.length; i++)
		{			  
			m_db_type.add(db[i]);
		}
		m_db_type.select(0);
	}
	
	private String getFixName(String str)
	{
		  String b = str;
		  if (str.length()>4)
			  b = str.substring(0,str.length()-4);
		  return b;
	}
	
	private void initFixCombo(FixProfile fp)
	{
		if (fp != null)
		{			
			File fl = new File(fp.getSourceDir());
			if (fl != null)
			{
				File list[] = fl.listFiles();
				if (list != null)
				{
					int count = 0; 
					for (int i = 0; i < list.length; i++)
					{						 
						 if (FixUtil.isZipJarFile(list[i].getName()))
						 {
							 String str = getFixName(list[i].getName());
							 m_combo_fix_name.add(str);
							 if (count == 0)
							 {
								 m_fix_file.setText(list[i].getPath());
								 m_src.setText(fp.getSourceDir());
								 m_trg.setText(fp.getTargetDir());
							 }
							 count++;
						 }
					  }
				  }
				  // Set combo selection to 1st item
				m_combo_fix_name.select(0);
			  }
		  }
		
	}
	private void initFixData()
	{
		if (m_fix_data != null)
		{
			if (m_fix_data.getFixName().length() > 0)
			{
				m_fix_name.setText(m_fix_data.getFixName());
				m_fix_file.setText(m_fix_data.getFileName());
				m_src.setText(m_fix_data.getSourceDir());
				m_trg.setText(m_fix_data.getTargetDir());				
			}
			else
				initFixCombo(m_fp);
			
			//Controls for Options
			if (m_fix_data.getCopyFlag())
				m_btn_copy.setSelection(true);
			if (m_fix_data.getUnzipFlag())
				m_btn_unzip.setSelection(true);
			if (m_fix_data.getPkgFlag())
			{
				m_btn_pkg.setSelection(true);
				m_pkg_name.setEnabled(true);
				m_pkg_name.setText(m_fix_data.getPackageName());
			}
			//Controls for JDBC
			if (m_fix_data.getDBScript())
			{
				FixDataJDBC jdbc_obj = m_fix_data.jdbc;
				if (jdbc_obj != null)
				{
					m_jdbc_driver.setText(jdbc_obj.getJDBCDriver());
					m_jdbc_url.setText(jdbc_obj.getURL());
					m_db_user.setText(jdbc_obj.getDBUser());
					m_db_pwd.setText(jdbc_obj.getDBPassword());
			
					m_db_check.setSelection(true);
					m_db_type.setEnabled(true);
					//private Combo m_db_type = null;
				}
			}
			else
			{
				m_db_check.setSelection(false);
				m_db_type.setEnabled(false);
				m_jdbc_driver.setEnabled(false);
				m_jdbc_url.setEnabled(false);
				m_db_user.setEnabled(false);
				m_db_pwd.setEnabled(false);
			}
		}
	}
	
	///////////////////////////////////////////////////
	// Handler Classes
	///////////////////////////////////////////////////
	class btnCopyMouseListener implements MouseListener
	{
		Shell d = null;
		
		public btnCopyMouseListener(Shell dlg)
		{
			d = dlg;		
		}
		
		public void mouseDown(MouseEvent e)
		{
			//System.out.println("Pressed Copy button");
			//MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
			//msg.setMessage("Copy message box");
			//msg.open();
		}
		public void mouseDoubleClick(MouseEvent e)
		{
			
		}
		public void mouseUp(MouseEvent e){}
	}
	
	class btnUnzipMouseListener implements MouseListener
	{
		Shell d = null;
		
		public btnUnzipMouseListener(Shell dlg)
		{
			d = dlg;		
		}
		
		public void mouseDown(MouseEvent e)
		{
			//System.out.println("Pressed UNzip button");
			//MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
			//msg.setMessage("Unzip message box");
			//msg.open();
		}
		public void mouseDoubleClick(MouseEvent e)
		{
			
		}
		public void mouseUp(MouseEvent e){}
	}
	
	class btnPkgMouseListener implements MouseListener
	{
		Shell d = null;
		Text m_pkg_name = null;
		Button m_btn = null;
		Text m_file_name = null;
		public btnPkgMouseListener(Shell dlg, Text pkg, Button btn, Text file_name)
		{
			d = dlg;		
			m_pkg_name = pkg;
			m_btn = btn;
			m_file_name = file_name;
		}
		
		public void mouseDown(MouseEvent e)
		{
			if (!m_btn.getSelection())
			{
				// Activate package selector
				m_pkg_name.setEnabled(true);
				m_pkg_name.setText("Package Name");
				setPackageNameValue();
			}
			else
			{
				// Disable package selection
				m_pkg_name.setEnabled(false);
				m_pkg_name.setText("Package Name");
			}	
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
		private void setPackageNameValue()
		{		  
			String file_name = m_file_name.getText();
			if (FixUtil.isZipFile(file_name))
			{				
				String pkg = FixWmUtil.getTargetPackageName(file_name);
				if (pkg != null)
				{
					if (pkg.length() > 0)
						m_pkg_name.setText(pkg);
					//else
					//	m_pkg_name.setText("Multiple Found");
				}
			}
		}
		
	}
	
	class FixFileMouseListener implements MouseListener
	{
		Shell m_dlg = null;
		public FixFileMouseListener(Shell s)
		{
			m_dlg = s;
		}
		public void mouseDown(MouseEvent e)
		{
			//System.out.println("Pressed Fix file button");
			FileDialog f_dialog = new FileDialog(m_dlg,SWT.OPEN);
			f_dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_SEL_FILE));
			String name = f_dialog.open();
			if (name != null)
				m_fix_file.setText(name);				
			
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
	}
	
	class SourceDirMouseListener implements MouseListener
	{
		Shell m_dlg = null;
		public SourceDirMouseListener(Shell s)
		{
			m_dlg = s;
		}
		public void mouseDown(MouseEvent e)
		{
			//System.out.println("Pressed Source button");
			DirectoryDialog f_dialog = new DirectoryDialog(m_dlg);
			f_dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_SEL_SRC_DIR));
			f_dialog.setFilterPath("C:/Program Files");
			String name = f_dialog.open();
			if (name != null)
				m_src.setText(name);
			
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
	}
	
	class TargetDirMouseListener implements MouseListener
	{
		Shell m_dlg = null;
		public TargetDirMouseListener(Shell s)
		{
			m_dlg = s;
		}
		
		public void mouseDown(MouseEvent e)
		{
			//System.out.println("Pressed Target button");
			DirectoryDialog f_dialog = new DirectoryDialog(m_dlg);
			f_dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_SEL_TRG_DIR));
			f_dialog.setFilterPath("C:/Program Files");
			String name = f_dialog.open();
			if (name != null)
				m_trg.setText(name);
			
		}
		public void mouseDoubleClick(MouseEvent e)
		{
			
		}
		public void mouseUp(MouseEvent e){}
	}
	
	class submitMouseListener implements MouseListener
	{
		FixData m_fd = null;
		FixDataView m_fdv = null; 
		Shell d = null;
		
		
		public submitMouseListener(Shell dlg, FixData fd, FixDataView fdv)
		{
			m_fd = fd;
			m_fdv = fdv;
			d = dlg;
			
		}
		public void mouseDown(MouseEvent e)
		{
			updateData(m_fd,m_fdv);
			d.dispose();			
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}
		
		private void updateData(FixData fd,FixDataView fdv)
		{
			if (fd != null && fdv != null)
			{
				fd.setName(fdv.getName());
				fd.setSourceDir(fdv.getSourceDir());
				fd.setTargeDir(fdv.getTargetDir());
				fd.setFileName(fdv.getFile());
				fd.setCopyFlag(fdv.getCopyFlag());
				fd.setPkgFlag(fdv.getPkgFlag());
				fd.setPackageName(fdv.getPkgName());
				fd.setUnzipFlag(fdv.getUnzipFlag());
				//JDBC
				fd.setDBScript(fdv.getDBFlag());
				if (fd.jdbc == null)
					fd.jdbc = new FixDataJDBC();
				fd.jdbc.setJDBCDriver(fdv.getJDBCDriver());
				fd.jdbc.setURL(fdv.getDBURL());
				fd.jdbc.setDBType(fdv.getDBType());
				fd.jdbc.setDBUser(fdv.getDBUser());
				fd.jdbc.setDBPassword(fdv.getDBPassword());
			}
		}		
	}
	
	class DBTestButtonMouseListener  implements MouseListener
	{
		Shell m_dlg = null;
		public DBTestButtonMouseListener(Shell s)
		{
			m_dlg = s;
		}
		
		public void mouseDown(MouseEvent e)
		{		
			FixDataJDBC fd_jdbc = new FixDataJDBC();
			fd_jdbc.setDBType(m_db_type.getText());
			fd_jdbc.setJDBCDriver(m_jdbc_driver.getText());
			fd_jdbc.setURL(m_jdbc_url.getText());
			fd_jdbc.setDBUser(m_db_user.getText());
			fd_jdbc.setDBPassword(m_db_pwd.getText());
			FixDBScript db = new FixDBScript();
			boolean res = db.testConnection(fd_jdbc);
			if (res)
			{				
				displayMessage(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_TEST_OK),true);
			}
			else
			{
				displayMessage(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_VIEW_DB_TEST_FAILED),false);
			}
		}
		public void mouseDoubleClick(MouseEvent e){}
		public void mouseUp(MouseEvent e){}	
		
		private void displayMessage(String s, boolean b)
		{		
			MessageBox msg = null;
			if (b)
				msg = new MessageBox(m_dlg,SWT.OK|SWT.ICON_INFORMATION);
			else
				msg = new MessageBox(m_dlg,SWT.OK|SWT.ICON_ERROR);
			msg.setText(m_resource.getString(FixManagerConstants.CNF_FIX_DATA_DLG_TITLE));
			msg.setMessage(s);
			msg.open();			
		}
	}
	
	class DBButtonMouseListener  implements MouseListener
	{
		Shell m_dlg = null;
		public DBButtonMouseListener(Shell s)
		{
			m_dlg = s;
		}
		
		public void mouseDown(MouseEvent e)
		{
			
			if (!m_db_check.getSelection())
			{
				//System.out.println("Select JDBC button");
				m_db_type.setEnabled(true);
				m_jdbc_driver.setEnabled(true);
				m_jdbc_url.setEnabled(true);
				m_db_user.setEnabled(true);
				m_db_pwd.setEnabled(true);
			}
			else
			{
				//System.out.println("UNselect JDBC button");
				m_db_type.setEnabled(false);
				m_jdbc_driver.setEnabled(false);
				m_jdbc_url.setEnabled(false);
				m_db_user.setEnabled(false);
				m_db_pwd.setEnabled(false);				
			}
		}
		public void mouseDoubleClick(MouseEvent e)
		{
			
		}
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
	
}
