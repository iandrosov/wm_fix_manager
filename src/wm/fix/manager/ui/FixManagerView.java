package wm.fix.manager.ui;


import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import wm.fix.manager.data.ConfigReader;
import wm.fix.manager.data.ConfigWriter;
import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.data.FixProfileData;
import wm.fix.manager.opt.install.FixAnalyzer;
import wm.fix.manager.opt.ui.BackupProgress;
import wm.fix.manager.opt.ui.FixUninstallProgress;
import wm.fix.manager.opt.ui.RestoreProgress;
import wm.fix.manager.util.FixManagerUtil;
import wm.fix.manager.util.FixUtil;


public class FixManagerView 
{
	//UI component section
	public static Shell m_shell = null;
	private Display m_display = null;
	private Tree m_tree = null;
	private Label m_dumy1 = null;
	private Label m_dumy2 = null;	
	private Button m_buttonReload = null;
	private Button m_buttonSave = null;
	private Button m_buttonClose = null;
	private Menu mBar = null;
	//Data section
	private String m_config_file = null;
	private String m_lang = "";
	private FixManagerUIResourceBundle m_resource = null;
	
	public FixManagerView(String file)
	{
		m_config_file = file;
		readLanguageConfig();
	}
	
	
	public void createUI()
	{
		m_display = new Display ();
		m_shell = new Shell(m_display);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;			
		m_shell.setLayout( gridLayout );
		//shell.setLayout( new RowLayout() );
		m_shell.setMinimumSize(new Point(300,450));
		m_shell.setSize(new Point(300, 500));	
		m_shell.setText(m_resource.getString(FixManagerConstants.CNF_FIX_MANAGER_TITLE));
		m_shell.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_WINDOW_ICON));
		
		// Create all controls for this view 
		createWidgets();
		
		 m_shell.pack();
		 m_shell.open ();
		 while (!m_shell.isDisposed ()) 
		 {
			 if (!m_display.readAndDispatch ()) 
				 m_display.sleep ();
		 }
		 m_display.dispose ();		
	}
	
	public void refreshView()
	{
		//Clear all widgets
		disposeWidgets();
		// refresh language
		readLanguageConfig();
		// Reload all widgets
		createWidgets();
		
	}
	
	public void createWidgets()
	{
		// Update main window title
		m_shell.setText(m_resource.getString(FixManagerConstants.CNF_FIX_MANAGER_TITLE));
		//Create menu bar
		createMenuBar(m_shell);
		// Create tree
		m_tree = createFixTree(m_shell);
		m_dumy1 = new Label(m_shell,SWT.NONE);
		m_dumy2 = new Label(m_shell,SWT.NONE);
		m_dumy1.setVisible(false);
		m_dumy1.setSize(0,0);
		m_dumy2.setVisible(false);
		m_dumy2.setSize(0,0);
		//m_buttonClear = new Button(m_shell,SWT.PUSH);
		//m_buttonClear.setText("Clear");
		//m_buttonClear.addMouseListener(new buttonClearMouseListener(this));
		m_buttonReload = new Button(m_shell,SWT.PUSH);
		m_buttonReload.setText(m_resource.getString(FixManagerConstants.CNF_BTN_REFRESH));
		GridData gd = getButtonGridData();
		m_buttonReload.setLayoutData(gd);
		m_buttonReload.addMouseListener(new buttonReloadMouseListener(this));
		m_buttonSave = new Button(m_shell,SWT.PUSH);
		m_buttonSave.setText(m_resource.getString(FixManagerConstants.CNF_BTN_SAVE));
		//m_buttonSave.setLayoutData(gd);
		m_buttonSave.addMouseListener(new buttonSaveMouseListener(this));
		m_buttonClose = new Button(m_shell,SWT.PUSH);
		m_buttonClose.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CLOSE));
		//m_buttonClose.setLayoutData(gd);
		m_buttonClose.addMouseListener(new buttonCloseMouseListener(m_shell));	
	}
	
	public void disposeWidgets()
	{
		if (mBar != null)
			mBar.dispose();
		if (m_tree != null)
		{
			Menu m = m_tree.getMenu();
			if (m != null)
				m.dispose();
			m_tree.removeAll();
			m_tree.dispose();
			m_tree = null;
		}
		m_dumy1.dispose();
		m_dumy2.dispose();
		//m_buttonClear.dispose();
		m_buttonReload.dispose();
		m_buttonSave.dispose();
		m_buttonClose.dispose();
	}
	
	public void saveConfigData()
	{
		ConfigWriter cw = new ConfigWriter();
		cw.generateConfigXML(m_config_file,m_tree);		
	}
	private void readLanguageConfig()
	{
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}		
	}
	private void createMenuBar(Shell s)
	{
		mBar = new Menu(s,SWT.BAR);
		s.setMenuBar(mBar);
		Menu optMenu = new Menu(s,SWT.DROP_DOWN);	
		MenuItem mi = new MenuItem(mBar,SWT.CASCADE);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_OPTIONS));
		mi.setMenu(optMenu);
		
		MenuItem lang = new MenuItem(optMenu,SWT.PUSH);
		lang.setText(m_resource.getString(FixManagerConstants.CNF_MENU_LANG));
		lang.addSelectionListener(new languageMenuItemListener(s,this));
		
		Menu helpMenu = new Menu(s,SWT.DROP_DOWN);
		MenuItem help = new MenuItem(mBar,SWT.CASCADE);
		help.setText(m_resource.getString(FixManagerConstants.CNF_MENU_HELP));
		help.setMenu(helpMenu);
		
		MenuItem about = new MenuItem(helpMenu,SWT.PUSH);
		about.setText(m_resource.getString(FixManagerConstants.CNF_MENU_ABOUT));
		about.addSelectionListener(new aboutMenuItemListener(s));
	}
	/**
	 * createFixTree method builds Fix tree with complete
	 * nodes and data.
	 * @param shell
	 * @return Tree object
	 */
	private Tree createFixTree(Shell shell)
	{
		if (m_config_file == null)
			return null;
		// Create Fix data object
		ConfigReader cnf = new ConfigReader();
		FixProfileData fpd = null;
		try
		{
			fpd = cnf.loadXml(m_config_file);
		}
		catch(Exception e)
		{
			// Error occures cannot load XML
			//return null;
		}
		/////////////////////////////////////////////
		// Start to create tree ctructure for fixes
		Tree tree = new Tree(shell, SWT.SINGLE|SWT.BORDER);
		GridData gridData = getTreeGridData();
		tree.setLayoutData(gridData);
		tree.setSize(new Point(300,200));
		
		// Create root item
		TreeItem root_item = new TreeItem(tree,SWT.NULL);
		root_item.setText("Fix Template");
		//root_item.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_PROFILE));
		root_item.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_NODE_CLOSED));
		if (fpd != null)
		{
			FixProfile fp[] = fpd.getFixProfileList();		
			for (int i = 0; i < fp.length; i++)
			{
				TreeItem itemProfile = new TreeItem(root_item,SWT.NULL);
				itemProfile.setText(fp[i].getName());
				itemProfile.setData(fp[i]);
				itemProfile.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_NODE_CLOSED));
			
				// build fix nodes
				FixData fd[] = fp[i].getFixList();
				for (int j = 0; j < fd.length; j++)
				{
					TreeItem itemFix =  new TreeItem(itemProfile,SWT.NULL);
					itemFix.setText(fd[j].getName());
					itemFix.setData(fd[j]);
					itemFix.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_FIX));
				}
			}
		}
		m_tree = tree;
		// Create Tree popup menu
		createTreePopUpMenu(m_tree);	
		m_tree.addTreeListener(
				new TreeListener()
				{
					public void treeExpanded(TreeEvent e)
					{						
						TreeItem ti = (TreeItem)e.item;
						ti.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_NODE_OPEN));
					}
					public void treeCollapsed(TreeEvent e)
					{						
						TreeItem ti = (TreeItem)e.item;
						ti.setImage(FixUtil.createImage(m_display,FixManagerConstants.FIX_MGR_TREE_NODE_CLOSED));
					}
				}
		);
		m_tree.addSelectionListener(
				 new SelectionAdapter()
				 {
					 public void widgetSelected(SelectionEvent e)
					 {						 
						 Menu m = m_tree.getMenu();
						 if (m != null)
						 {							 							 
							 TreeItem ti = (TreeItem)e.item;
							 Object obj = ti.getData();
							 m.setData(obj);							 
						 }
					 }
				 }
		 );
		
		return tree;
	}
	private GridData getTreeGridData()
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		return gridData;
	}
	private GridData getButtonGridData()
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.minimumWidth = 80;
		return gridData;
	}

	private void createTreePopUpMenu(Tree tree)
	{
		Menu m = new Menu(tree.getShell(),SWT.POP_UP);
		
		MenuItem mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_ANALYZE));
		mi.addSelectionListener(new analyzeMenuItemListener(tree.getShell(),tree,m_resource));
		mi = new MenuItem(m,SWT.SEPARATOR);
		
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_ADD));
		mi.addSelectionListener(new addMenuItemListener(tree.getShell(),tree));
		
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_DELETE));
		mi.addSelectionListener(new deleteMenuItemListener(tree.getShell(),tree,m_resource));
	
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_EDIT));
		mi.addSelectionListener(new editMenuItemListener(m_tree));
		mi = new MenuItem(m,SWT.SEPARATOR);
		
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_INSTALL));
		mi.addSelectionListener(new installMenuItemListener(tree.getShell()));

		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_UNINSTALL));
		mi.addSelectionListener(new uninstallMenuItemListener(tree.getShell(),m_resource));

		//mi = new MenuItem(m,SWT.PUSH);
		//mi.setText("Update All Fixes");
		//mi.addSelectionListener(new updateallMenuItemListener());
		mi = new MenuItem(m,SWT.SEPARATOR);
		
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_BACKUP));
		mi.addSelectionListener(new backupMenuItemListener(tree.getShell(),m_resource));
		
		mi = new MenuItem(m,SWT.PUSH);
		mi.setText(m_resource.getString(FixManagerConstants.CNF_MENU_RESTORE));
		mi.addSelectionListener(new restoreMenuItemListener(tree.getShell(),m_resource));
			
		tree.setMenu(m);
	}
}

class buttonClearMouseListener implements MouseListener
{
	FixManagerView m_fmv = null;
	public buttonClearMouseListener(FixManagerView fmv)
	{
		m_fmv = fmv;
	}
	public void mouseDown(MouseEvent e)
	{
		m_fmv.disposeWidgets();
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}
	
}
class buttonReloadMouseListener implements MouseListener
{
	FixManagerView m_fmv = null;
	public buttonReloadMouseListener(FixManagerView fmv)
	{
		m_fmv = fmv;
	}
	public void mouseDown(MouseEvent e)
	{
		//m_fmv.createWidgets();
		m_fmv.refreshView();
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}
	
}

class buttonSaveMouseListener implements MouseListener
{
	FixManagerView m_fmv = null;
	
	public buttonSaveMouseListener(FixManagerView fmv)
	{
		m_fmv = fmv;
		
	}
	public void mouseDown(MouseEvent e)
	{
		m_fmv.saveConfigData();
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}
}

class buttonCloseMouseListener implements MouseListener
{
	Shell m_shell = null;
	public buttonCloseMouseListener(Shell s)
	{
		m_shell = s;
	}
	public void mouseDown(MouseEvent e)
	{		
		m_shell.dispose();
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}
}

class analyzeMenuItemListener implements SelectionListener
{
	Shell d = null;
	Tree m_tree = null;
	FixManagerUIResourceBundle m_resource = null;
	public analyzeMenuItemListener(Shell sh, Tree tree,FixManagerUIResourceBundle res)
	{
		d = sh;
		m_tree = tree;
		m_resource = res;
	}
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		FixProfile fp = null;
		FixData fd = null;
		Object md = m.getData();
		if (md != null)
		{		
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				fp = (FixProfile)md;
			}
			else
			{
				fd = (FixData)md;
			}
			String s_msg = getEventMessage(md);
			MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
			msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_ANALYZE_TITLE));
			msg.setMessage(s_msg);
			if (msg.open() == SWT.OK)
			{
				try
				{
					if (fd != null)
					{
						//////////////////////////////////////////////
						FixAnalyzer fa = new FixAnalyzer();
						int status = fa.analyzeFix(fd);
						fd.setStatus(status);
						fd.setInstalled(fa.isFixInstalled(status));
						// Set status icon for tree		       
						TreeItem[] items = m_tree.getSelection();			       
						items[0].setImage(fa.getStatusImage(d.getDisplay(),status));
					}
					else if (fp != null)
					{
						FixAnalyzer fa = new FixAnalyzer();
						fa.evaluateProfile(fp);
						setFixStatusIcon(fp,fa);
					}
				}
				catch(java.lang.NoClassDefFoundError e)
				{
					FixManagerUtil.DefaultNotAvailableMessage(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_ANALYZE_TITLE), m_resource, d);
				}

			}			
		}
		
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{
		
	}
	private void setFixStatusIcon(FixProfile fp, FixAnalyzer fa)
	{
		TreeItem[] items = m_tree.getSelection();
		TreeItem[] child = items[0].getItems();
		if (child != null)
		{
			for (int i=0; i < child.length; i++)
			{
				//System.out.println(child[i].getText());
				FixData fd = fp.getFixByName(child[i].getText());
				if (fd != null)
					child[i].setImage(fa.getStatusImage(d.getDisplay(),fd.getStatus()));
			}
		}
	}
	private String getEventMessage(Object obj)
	{
		String msg = "";
		if (obj != null)
		{
			msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_ANALYZE) + " ";			
			if (obj.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				FixProfile fp = (FixProfile)obj;
				msg += m_resource.getString(FixManagerConstants.FIX_MGR_MSG_FIX_PROFILE) + " - " + fp.getName() + " ";
			}
			else
			{
				FixData fd = (FixData)obj;
				msg += m_resource.getString(FixManagerConstants.FIX_MGR_MSG_FIX_DATA) + " - " + fd.getName() + " ";
			}
			msg +=m_resource.getString(FixManagerConstants.FIX_MGR_MSG_YES_NO);			
		}
		return msg;
	}
}	

class addMenuItemListener implements SelectionListener
{
	Shell d = null;
	Tree m_tree = null;
	
	public addMenuItemListener(Shell sh, Tree tree)
	{
		d = sh;
		m_tree = tree;
	}
	
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();		
		Object md = m.getData();
		if (md != null)
		{
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{				
				FixData fd = new FixData();
				FixDataView fdv = new FixDataView(FixManagerView.m_shell,SWT.APPLICATION_MODAL,fd,(FixProfile)md);
				FixData fd_result = fdv.open();
				if (fd_result != null)
				{
					TreeItem[] items = m_tree.getSelection();
					if (items[0] != null && fd_result.getName().length() > 0)
					{		
						TreeItem new_item =  new TreeItem(items[0],SWT.NULL);
						new_item.setText(fd_result.getName());
						new_item.setData(fd_result);
						new_item.setImage(FixUtil.createImage(m_tree.getDisplay(),FixManagerConstants.FIX_MGR_TREE_FIX));
						//Update Fix Profile data add new Fix
						FixProfile fp = (FixProfile)md;
						fp.addFixData(fd_result);
						items[0].setData(fp);
					}
				}
			}
			else
			{
				FixProfile fp = null;
				TreeItem[] items = m_tree.getSelection();
				if (items == null)
					return;
				TreeItem prof_item = items[0].getParentItem();
				if (prof_item != null)
				{
					Object data = prof_item.getData();
					if (data != null)
					{
						if (data.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
							fp = (FixProfile)data;
					}
				
					FixData fd = new FixData();
					FixDataView fdv = new FixDataView(FixManagerView.m_shell,SWT.APPLICATION_MODAL,fd,fp);
					fd = fdv.open();
					if (fd != null && fd.getName().length() > 0)
					{
						TreeItem new_item =  new TreeItem(prof_item,SWT.NULL);
						new_item.setText(fd.getName());
						new_item.setData(fd);
						new_item.setImage(FixUtil.createImage(m_tree.getDisplay(),FixManagerConstants.FIX_MGR_TREE_FIX));	
						
						fp.addFixData(fd);
						prof_item.setData(fp);
					}
				}		
			}
		}
		else
		{
			FixProfile fp = new FixProfile();
			FixProfileView fpv = new FixProfileView(FixManagerView.m_shell,SWT.APPLICATION_MODAL,fp);
			FixProfile result = fpv.open();
			if (result == null)
				return;
			// Add tree item here
			TreeItem[] items = m_tree.getSelection();
			if (items[0] != null && result.getName().length() > 0)
			{
				TreeItem new_item =  new TreeItem(items[0],SWT.NULL);
				new_item.setText(result.getName());
				new_item.setData(result);
				new_item.setImage(FixUtil.createImage(m_tree.getDisplay(),FixManagerConstants.FIX_MGR_TREE_PROFILE));
			}
		}
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{
	}
}	

class deleteMenuItemListener implements SelectionListener
{
	Shell d = null;
	Tree m_tree = null;
	FixManagerUIResourceBundle m_resource = null;
	public deleteMenuItemListener(Shell sh, Tree tree,FixManagerUIResourceBundle res)
	{
		d = sh;
		m_tree = tree;
		m_resource = res;
	}
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		// Delete tree node
		Object md = m.getData();
		if (md != null)
		{
			String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_DELETE);
			//System.out.println("Delete Menu Selection - "+md.getClass().getName());
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				FixProfile fp = (FixProfile)md;
				s_msg += m_resource.getString(FixManagerConstants.FIX_MGR_MSG_FIX_PROFILE)+ " - " + fp.getName();
			}
			else
			{
				FixData fd = (FixData)md;
				s_msg += " "+m_resource.getString(FixManagerConstants.FIX_MGR_MSG_FIX_DATA) + " - " + fd.getName();
			}
			s_msg +=" "+m_resource.getString(FixManagerConstants.FIX_MGR_MSG_YES_NO);
			MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
			msg.setMessage(s_msg);
			//m_resource.dumpStrings();
			msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_DELETE_TITLE));
			if (msg.open() == SWT.OK)
			{				
				TreeItem[] items = m_tree.getSelection();
				if (items != null)
				{
					for (int i = 0; i < items.length; i++)
					{
						Object o = items[i].getData();
						if (o.getClass().getName().equals("wm.fix.manager.data.FixData"))
						{
							TreeItem prof_item = items[i].getParentItem();
							if (prof_item != null)
							{
								Object data = prof_item.getData();
								if (data != null)
								{
									if (data.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
									{
										FixProfile fp = (FixProfile)data;
										FixData temp = (FixData)o;
										fp.removeFixData(temp.getName());
										prof_item.setData(fp);
									}
								}				
							}				
							
						}
						items[i].dispose();
					}
				}
			}

			
		}
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{
		
	}
}	

class editMenuItemListener implements SelectionListener
{
	Tree m_tree = null;
	public editMenuItemListener(Tree tree)
	{
		m_tree = tree;
	}
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();		
		Object md = m.getData();
		if (md != null)
		{
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				FixProfileView fpv = new FixProfileView(FixManagerView.m_shell,SWT.APPLICATION_MODAL,(FixProfile)md);
				FixProfile result = fpv.open();
				if (result != null)
				{
					TreeItem[] items = m_tree.getSelection();
					if (items[0] != null)
						items[0].setData(result);
				}
			}
			else
			{
				FixProfile fp = null;
				TreeItem[] items = m_tree.getSelection();
				if (items == null)
					return;
				TreeItem prof_item = items[0].getParentItem();
				if (prof_item != null)
				{
					Object data = prof_item.getData();
					if (data != null)
					{
						if (data.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
							fp = (FixProfile)data;
					}				
				}				
				FixDataView fdv = new FixDataView(FixManagerView.m_shell,SWT.APPLICATION_MODAL,(FixData)md,fp);
				FixData fd_result = fdv.open();
				if (fd_result != null)
				{
					if (items[0] != null)
						items[0].setData(fd_result);					
				}
			}
		}		
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{	
		
	}
}	

class installMenuItemListener implements SelectionListener
{
	Shell d = null;
	public installMenuItemListener(Shell sh)
	{
		d = sh;
	}
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		FixProfile fp = null;
		FixData fd = null;
		Object md = m.getData();
		if (md != null)
		{
			//String s_msg = FixManagerConstants.FIX_MGR_MSG_INSTALL;
			
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				fp = (FixProfile)md;
				FixInstallProgress fip = new FixInstallProgress(d,fp,true);
				fip.open();
				
				//s_msg += FixManagerConstants.FIX_MGR_MSG_FIX_PROFILE+ " - " + fp.getName();
			}
			else
			{
				fd = (FixData)md;
				FixInstallProgress fip = new FixInstallProgress(d,fd,true);
				fip.open();
				
				//s_msg += FixManagerConstants.FIX_MGR_MSG_FIX_DATA + " - " + fd.getName();
			}
			//s_msg +=FixManagerConstants.FIX_MGR_MSG_YES_NO;
			//MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
			//msg.setText(FixManagerConstants.FIX_MGR_MSG_INSTALL_TITLE);
			//msg.setMessage(s_msg);
			/*
			if (msg.open() == SWT.OK)
			{
				if (fp != null)
				{
					FixInstallProgress fip = new FixInstallProgress(d,fp,true);
					fip.open();
				}
				else if (fd != null)
				{
					FixInstallProgress fip = new FixInstallProgress(d,fd,true);
					fip.open();
				}
			}
			*/
		}
		
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{
		
	}
}	

class uninstallMenuItemListener implements SelectionListener
{
	Shell d = null;
	FixManagerUIResourceBundle m_resource = null;
	public uninstallMenuItemListener(Shell sh,FixManagerUIResourceBundle res)
	{
		d = sh;
		m_resource = res;
	}
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		
		Object md = m.getData();
		if (md != null)
		{
			//String s_msg = FixManagerConstants.FIX_MGR_MSG_UNINSTALL;
			
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_NO_UNINTALL);
				MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
				msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_UNINSTALL_TITLE));
				msg.setMessage(s_msg);
				msg.open();
				
			}
			else
			{
				try
				{
					FixData fd = (FixData)md;
					FixUninstallProgress fip = new FixUninstallProgress(d,fd,false);
					fip.open();
				}
				catch(java.lang.NoClassDefFoundError e)
				{
					FixManagerUtil.DefaultNotAvailableMessage(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_UNINSTALL_TITLE), m_resource, d);
				}
			}
		}
		
	}
	public void widgetDefaultSelected(SelectionEvent event)
	{
		
	}
}	

class updateallMenuItemListener implements SelectionListener
{
	public void widgetSelected(SelectionEvent event)
	{
		//Object obj = event.getSource();
		//MenuItem mi = (MenuItem)obj;
		//Menu m = mi.getParent();
		
		//Object md = m.getData();
		//if (md != null)
		//		System.out.println("Update all Menu Selection - "+md.getClass().getName());
		
	}
	public void widgetDefaultSelected(SelectionEvent event){}
}	

class backupMenuItemListener implements SelectionListener
{
	Shell d = null;
	FixManagerUIResourceBundle m_resource = null;
	public backupMenuItemListener(Shell sh,FixManagerUIResourceBundle res)
	{
		d = sh;
		m_resource = res;
	}
	
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		
		Object md = m.getData();
		if (md != null)
		{
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				FixProfile fp = (FixProfile)md;
				
				if (fp != null)
				{
					String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BAK_WM)+fp.getName();
					s_msg += m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BAK_REQ);
					MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
					msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BACKUP_TITLE));
					msg.setMessage(s_msg);
					if (msg.open() == SWT.OK)
					{
						try
						{
							BackupProgress bp = new BackupProgress(d,fp);
							bp.open();
						}
						catch(java.lang.NoClassDefFoundError e)
						{
							FixManagerUtil.DefaultNotAvailableMessage(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BACKUP_TITLE), m_resource, d);
						}
					}
				}
			}
			else
			{
				String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BAK_NO);
				MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_ERROR);
				msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_BACKUP_TITLE));
				msg.setMessage(s_msg);
				msg.open();
			}
		}
	}
	public void widgetDefaultSelected(SelectionEvent event){}
}	

class restoreMenuItemListener implements SelectionListener
{
	Shell d = null;
	FixManagerUIResourceBundle m_resource = null;
	public restoreMenuItemListener(Shell sh,FixManagerUIResourceBundle res)
	{
		d = sh;
		m_resource = res;
	}
	
	public void widgetSelected(SelectionEvent event)
	{
		Object obj = event.getSource();
		MenuItem mi = (MenuItem)obj;
		Menu m = mi.getParent();
		
		Object md = m.getData();
		if (md != null)
		{
			if (md.getClass().getName().equals("wm.fix.manager.data.FixProfile"))
			{
				FixProfile fp = (FixProfile)md;
				
				if (fp != null)
				{
					String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_WM)+fp.getName();
					s_msg += m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_REQ);
					MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
					msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_TITLE));
					msg.setMessage(s_msg);
					if (msg.open() == SWT.OK)
					{
						try 
						{
							RestoreProgress rp = new RestoreProgress(d,fp);
							rp.open();
						}
						catch(java.lang.NoClassDefFoundError e)
						{
							FixManagerUtil.DefaultNotAvailableMessage(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_TITLE), m_resource, d);
						}
					}
				}
			}
			else
			{
				String s_msg = m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_NO);
				MessageBox msg = new MessageBox(d,SWT.OK|SWT.CANCEL|SWT.ICON_ERROR);
				msg.setText(m_resource.getString(FixManagerConstants.FIX_MGR_MSG_RESTORE_TITLE));
				msg.setMessage(s_msg);
				msg.open();
			}
		}
	}
	public void widgetDefaultSelected(SelectionEvent event){}
}	

/**
 * aboutMenuItemListener Help->About manu handler class
 * @author Igor Androsov
 *
 */
class aboutMenuItemListener implements SelectionListener
{
	Shell m_shell = null;
	public aboutMenuItemListener(Shell s)
	{
		m_shell = s;
	}
	public void widgetSelected(SelectionEvent event)
	{		
		AboutView av = new AboutView(m_shell);
		av.open();		
	}
	public void widgetDefaultSelected(SelectionEvent event){}
}

/**
 * languageMenuItemListener Help->About manu handler class
 * @author Igor Androsov
 *
 */
class languageMenuItemListener implements SelectionListener
{
	Shell m_shell = null;
	FixManagerView m_fmv = null;
	public languageMenuItemListener(Shell s,FixManagerView fmv)
	{
		m_shell = s;
		m_fmv = fmv;
	}
	public void widgetSelected(SelectionEvent event)
	{
		OptionsView ov = new OptionsView(m_shell);
		if (ov.open() == SWT.OK)
			m_fmv.refreshView();
	}
	public void widgetDefaultSelected(SelectionEvent event){}
}
