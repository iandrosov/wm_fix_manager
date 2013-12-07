package wm.fix.manager.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import wm.fix.manager.data.FixProfile;
import wm.fix.manager.util.FixUtil;

	
public class FixProfileView extends Dialog
{
	public FixProfile m_fp 	= null;

	private Text m_prf_name  = null;
	private Text m_prf_desc  = null;
	private Text m_prf_src   = null;
	private Text m_prf_trg   = null;
	private Text m_prf_is    = null;
	private Text m_prf_bk    = null;
	private Text m_prf_dev   = null;
	private Text m_prf_com   = null;
	
	private Button m_btn_src = null;
	private Button m_btn_trg = null;
	private Button m_btn_is  = null;
	private Button m_btn_bk  = null;
	private Button m_btn_dev = null;
	private Button m_btn_com = null;

	private String m_lang = "";
	private FixManagerUIResourceBundle m_resource = null;
	
	public FixProfileView(Shell parent,int style,FixProfile fp)
	{
		super(parent, style);
		m_fp = fp;
		Properties p = System.getProperties();
		m_lang = p.getProperty("languagefile");
		try
		{
			m_resource = new FixManagerUIResourceBundle(new FileInputStream(m_lang));
		}
		catch(Exception e){}
		
	}
	
	public FixProfile open()
	{
		Shell dialog = new Shell(this.getParent(),SWT.RESIZE|SWT.DIALOG_TRIM|SWT.PRIMARY_MODAL);
		dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_TITLE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		dialog.setLayout(gridLayout);
		//dialog.setLayout(new RowLayout());
		dialog.setSize(new Point(350, 340));
		dialog.setImage(FixUtil.createImage(dialog.getDisplay(),FixManagerConstants.FIX_MGR_GEAR));
				
		this.showWidgets(dialog);
		
		dialog.open();
		Display display = this.getParent().getDisplay();
		while(!dialog.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		return m_fp;
	}
	
	private void showWidgets(final Shell dialog)
	{
		Label label = new Label(dialog,SWT.NONE);
		label.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_PROF));
		m_prf_name = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		Button btn1 = new Button(dialog, SWT.NONE);
		btn1.setEnabled(false);
		btn1.setVisible(false);

		Label label1 = new Label(dialog,SWT.NONE);
		label1.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_DESC));		
		m_prf_desc = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		Button btn2 = new Button(dialog, SWT.NONE);
		btn2.setEnabled(false);
		btn2.setVisible(false);

		Label label2 = new Label(dialog,SWT.NONE);
		label2.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_SRC));		
		m_prf_src = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_src = new Button(dialog, SWT.NONE);
		m_btn_src.setText("<<");
		Image img = FixUtil.createImage(dialog.getDisplay(), FixManagerConstants.FIX_MGR_OPEN_FILE);
		m_btn_src.setImage(img);		
		m_btn_src.addMouseListener(new btnFileMouseListener(dialog,m_prf_src,m_resource));
		
		Label label3 = new Label(dialog,SWT.NONE);
		label3.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_TRG));		
		m_prf_trg = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_trg = new Button(dialog, SWT.NONE);
		m_btn_trg.setText("<<");
		m_btn_trg.setImage(img);
		m_btn_trg.addMouseListener(new btnTargetFileMouseListener(dialog,m_prf_trg,this,m_resource));
		
		Label label4 = new Label(dialog,SWT.NONE);
		label4.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_IS));		
		m_prf_is = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_is = new Button(dialog, SWT.NONE);
		m_btn_is.setText("<<");
		m_btn_is.setImage(img);
		m_btn_is.addMouseListener(new btnFileMouseListener(dialog,m_prf_is,m_resource));
		
		Label label5 = new Label(dialog,SWT.NONE);
		label5.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_BROKER));		
		m_prf_bk = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_bk = new Button(dialog, SWT.NONE);
		m_btn_bk.setText("<<");
		m_btn_bk.setImage(img);
		m_btn_bk.addMouseListener(new btnFileMouseListener(dialog,m_prf_bk,m_resource));
		
		Label label6 = new Label(dialog,SWT.NONE);
		label6.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_DEV));		
		m_prf_dev = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_dev = new Button(dialog, SWT.NONE);
		m_btn_dev.setText("<<");
		m_btn_dev.setImage(img);
		m_btn_dev.addMouseListener(new btnFileMouseListener(dialog,m_prf_dev,m_resource));
		
		Label label7 = new Label(dialog,SWT.NONE);
		label7.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_COM));		
		m_prf_com = new Text(dialog,SWT.SINGLE|SWT.BORDER);
		m_btn_com = new Button(dialog, SWT.NONE);
		m_btn_com.setText("<<");
		m_btn_com.setImage(img);
		m_btn_com.addMouseListener(new btnFileMouseListener(dialog,m_prf_com,m_resource));
		init();
		
		Button button = new Button(dialog, SWT.NONE);
		button.setText(m_resource.getString(FixManagerConstants.CNF_BTN_SUBMIT));
		GridData gd = getButtonGridData();
		button.setLayoutData(gd);
		button.addMouseListener(new btnSubmitMouseListener(dialog,m_fp,this));
		Button buttonCancel = new Button(dialog, SWT.NONE);
		buttonCancel.setText(m_resource.getString(FixManagerConstants.CNF_BTN_CANCEL));
		buttonCancel.setLayoutData(gd);
		buttonCancel.addMouseListener(new btnCancelMouseListener(dialog));
		
	}

	private GridData getButtonGridData()
	{
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.minimumWidth = 80;
		return gridData;
	}
	
	private void init()
	{
		if (m_fp != null)
		{
			m_prf_name.setText(m_fp.getName());
			m_prf_desc.setText(m_fp.getDesc());
			m_prf_src.setText(m_fp.getSourceDir());
			m_prf_trg.setText(m_fp.getTargetDir());
			m_prf_is.setText(m_fp.getISDir());
			m_prf_bk.setText(m_fp.getBrokerDir());
			m_prf_dev.setText(m_fp.getDeveloperDir());
			m_prf_com.setText(m_fp.getCommonDir());
		}
		else
		{
			m_prf_name.setText("");
			m_prf_desc.setText("");
			m_prf_src.setText("");
			m_prf_trg.setText("");
			m_prf_is.setText("");
			m_prf_bk.setText("");
			m_prf_dev.setText("");
			m_prf_com.setText("");			
		}
	}	
	
	public String getName(){return m_prf_name.getText();}
	public String getDesc(){return m_prf_desc.getText();}
	public String getSrc(){	return m_prf_src.getText();	}
	public String getTarget(){return m_prf_trg.getText();}
	public String getIS(){return m_prf_is.getText();}
	public void setIS(String str){m_prf_is.setText(str);}
	public String getBroker(){return m_prf_bk.getText();}
	public void setBroker(String str){m_prf_bk.setText(str);}
	public String getDev(){return m_prf_dev.getText();}
	public void setDev(String str){m_prf_dev.setText(str);}
	public String getCommon(){return m_prf_com.getText();}
	public void setCommon(String str){m_prf_com.setText(str);}
}

class btnFileMouseListener implements MouseListener
{
	Shell d = null;
	Text  m_txt = null;
	private FixManagerUIResourceBundle m_resource = null;
	
	public btnFileMouseListener(Shell dlg,Text tx,FixManagerUIResourceBundle res)
	{
		d = dlg;
		m_txt = tx;
		m_resource = res;
	}
	public void mouseDown(MouseEvent e)
	{				
			DirectoryDialog f_dialog = new DirectoryDialog(d);
			f_dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_SEL_DIR));
			f_dialog.setFilterPath("C:/Program Files");			
			String name = f_dialog.open();
			if (name != null)			
				m_txt.setText(name);
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}	
}
class btnTargetFileMouseListener implements MouseListener
{
	Shell d = null;
	Text  m_txt = null;
	FixProfileView m_fpv = null;
	private FixManagerUIResourceBundle m_resource = null;
	
	public btnTargetFileMouseListener(Shell dlg, Text tx, FixProfileView fpv,FixManagerUIResourceBundle res)
	{
		d = dlg;
		m_txt = tx;
		m_fpv = fpv;
		m_resource = res;
	}
	public void mouseDown(MouseEvent e)
	{				
			DirectoryDialog f_dialog = new DirectoryDialog(d);
			f_dialog.setText(m_resource.getString(FixManagerConstants.CNF_FIX_PROF_DLG_SEL_DIR));
			f_dialog.setFilterPath("C:/Program Files");			
			String name = f_dialog.open();
			if (name != null)
			{
				m_txt.setText(name);
				m_fpv.setBroker(name+File.separator+"Broker");
				m_fpv.setCommon(name+File.separator+"common");
				m_fpv.setDev(name+File.separator+"Developer");
				m_fpv.setIS(name+File.separator+"IntegrationServer");
			}
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

class btnSubmitMouseListener implements MouseListener
{
	Shell d = null;
	FixProfile  m_data = null;
	FixProfileView m_fpv = null;
	
	public btnSubmitMouseListener(Shell dlg, FixProfile fp, FixProfileView v)
	{
		d       = dlg;
		m_data  = fp;
		m_fpv   = v;
	}
	public void mouseDown(MouseEvent e)
	{
		updateData(m_data,m_fpv);
		d.dispose();
	}
	public void mouseDoubleClick(MouseEvent e){}
	public void mouseUp(MouseEvent e){}
	
	private void updateData(FixProfile fp,FixProfileView fpv)
	{
		if (fp != null && fpv != null)
		{
			fp.setBrokerDir(fpv.getBroker());
			fp.setCommonDir(fpv.getCommon());
			fp.setDesc(fpv.getDesc());
			fp.setDeveloperDir(fpv.getDev());
			fp.setISDir(fpv.getIS());
			fp.setName(fpv.getName());
			fp.setSourceDir(fpv.getSrc());
			fp.setTargeDir(fpv.getTarget());
		}
	}
}

