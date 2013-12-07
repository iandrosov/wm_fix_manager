package wm.fix.manager.opt.install;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixDataConstants;
import wm.fix.manager.data.FixDataJDBC;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.install.FixDBScript;
import wm.fix.manager.install.FixLoger;
import wm.fix.manager.install.Messages;
import wm.fix.manager.opt.ui.BackupProgress;
import wm.fix.manager.opt.ui.FixUninstallProgress;
import wm.fix.manager.opt.ui.RestoreProgress;
import wm.fix.manager.util.FixUtil;
import wm.fix.manager.util.FixWmUtil;

public class FixUninstaller 
{
	private int m_lengthOfTask = 0;
	private int m_current = 0;
	private String m_statMessage = null;
	private FixUninstallProgress m_fup = null;
	
	private BackupProgress m_bp = null;
	private RestoreProgress m_rp = null;
	
	public FixUninstaller()
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
	}

	public FixUninstaller(FixUninstallProgress fup)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_fup = fup;
	}
	
	public FixUninstaller(RestoreProgress rp)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_rp = rp;
	}
	
	public void updateProgress()
	{
		if (m_fup != null)
			m_fup.updateProgress();
		else if (m_bp != null)
			m_bp.updateProgress();
		else if (m_rp != null)
			m_rp.updateProgress();
	}
	public void finishProgress()
	{
		if (m_fup != null)
			m_fup.finishProgress();
		else if (m_bp != null)
			m_bp.finishProgress();
		else if (m_rp != null)
			m_rp.finishProgress();		
	}
	public int getCurrent(){return m_current;}
	public void setCurrent(int c){m_current = c;}
    public int getLengthOfTask(){return m_lengthOfTask;} 
    public void setLengthOfTask(int len){m_lengthOfTask = len;}
    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {return m_statMessage;}
    public void setMessage(String msg) {m_statMessage = msg;}

	/**
	 * getFixInstallTaskSize method calculate the installation task
	 * length to display progress.
	 * @param fp FixProfile object
	 * @return task size for installing a Fix profile
	 */
	public int getFixUninstallTaskSize(FixProfile fp)
	{
		int size = 0;
		if (fp == null)
			return size; //Nothing to do here
		
		FixData fd[] = fp.getFixList();
		for (int i = 0; i < fd.length; i++)
		{
			size += getFixUninstallTaskSize(fd[i]);
		}
		
		return size;
	}
	
	/**
	 * getFixInstallTaskSize method calculate the installation task
	 * length to display progress.
	 * @param fxd - FixData object
	 * @return int number representing arbitrary length for task
	 */
	public int getFixUninstallTaskSize(FixData fxd)
	{
		int size = 0;
		if (fxd == null)
			return size; //Nothing to do here
		// If task size is set return it
		//if (m_lengthOfTask > 0)
		//	return m_lengthOfTask;
		
		// If fix need to be unziped into directory
		if (fxd.getUnzipFlag())
		{
			size = FixUtil.getZipFileTask(fxd.getFileName());
			if (fxd.getPkgFlag())
				size += 2;
			if (fxd.getDBScript())
				size += 2;
			//m_lengthOfTask = size;
			return size;
		}
		
		if (fxd.getCopyFlag()) // Fix just copy directive
		{
			size += 2;
			if (FixUtil.isPackage(fxd.getFileName(), fxd.getTargetDir()))
				size += 2; //Steps for install of package
			m_lengthOfTask = size;
			return size;
		}
		m_lengthOfTask = size;
		return size; 
	}
	
//////////////////////////////////////////////////
// MOVED FROM FixInstaller package
//////////////////////////////////////////////////
	public void uninstallFixProfile(FixProfile fp)
	{
		
		if (fp == null)
			return;
		FixLoger.audit_log("### "+Messages.getString("FixInstaller.msg.tart.uninst.profile")+fp.getName()+" ###", this); //$NON-NLS-1$ //$NON-NLS-2$
		FixData[] fxd_list = fp.getFixList();
		if (fxd_list != null)
		{
			for (int i = 0; i < fxd_list.length; i++)
			{
				FixLoger.audit_log("****** "+Messages.getString("FixInstaller.msg.start.uninst.fix")+fxd_list[i].getName()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$
				if (fxd_list[i].getUnzipFlag())
				{
					UninstallArchive(fxd_list[i]);
				}
				else if (fxd_list[i].getCopyFlag()) // Uninstall Fix with just copy directive
				{
					//m_current+=1;
					FixLoger.audit_log(Messages.getString("FixInstaller.msg.fix")+" "+fxd_list[i].getFileName()+" "+Messages.getString("FixInstaller.msg.has.upd.file.uninstall"),this); //$NON-NLS-1$ //$NON-NLS-2$
					UninstallUpdates(fxd_list[i].getFileName(), fxd_list[i].getTargetDir());
					//m_current+=1;
					// Check if Fix is package
				}
				FixLoger.audit_log("****** "+Messages.getString("FixInstaller.msg.complete.uninst.fix")+fxd_list[i].getName()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$
				fxd_list[i].setInstalled(false);
				fxd_list[i].setStatus(FixDataConstants.FIX_INSTALL_ERROR);

			}
		}
		FixLoger.audit_log("### "+Messages.getString("FixInstaller.msg.complete.uninst.profile")+fp.getName()+" ###",this); //$NON-NLS-1$ //$NON-NLS-2$
		
	}

	/**
	 * unistallFix methods uninstalls select fix from target system
	 * @param fxd
	 * @param internal_access
	 */
	public void uninstallFix(FixData fxd, boolean internal_access)
	{
		if (fxd == null)
			return;

		if (fxd.getUnzipFlag())
		{
			UninstallZipArchiveFix(fxd);
		}
		else if (fxd.getCopyFlag()) // Uninstall Fix with just copy directive
		{
			m_current+=1;
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.fix")+" "+fxd.getFileName()+" "+Messages.getString("FixInstaller.msg.has.upd.file.uninstall"),this); //$NON-NLS-1$ //$NON-NLS-2$
			UninstallUpdates(fxd.getFileName(), fxd.getTargetDir());
			m_current+=1;
			// Check if Fix is package
			if (FixUtil.isPackage(fxd.getFileName(), fxd.getTargetDir()))
			{
				m_current+=1;
				UninstallPackage(fxd);
				m_current+=1;
			}			
		}
		// Complete uninstall
		if (!internal_access)
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.uninstall.fix") +" "+ fxd.getName(),this); //$NON-NLS-1$
			/////////////////////// TEMPORARY
			finishProgress();
		}
		fxd.setInstalled(false);
		fxd.setStatus(FixDataConstants.FIX_INSTALL_ERROR);
		
	}

	  /**
	   * UninstallUpdates method unstalls update from IS directory
	   * typically a jar file.
	   * @param fix - fix file with full path
	   * @param trg - target directory from where to uninstall update
	   */
	  private void UninstallUpdates(String fix, String trg)
	  {
		  File file = new File(fix);
		  if (file.exists())
		  {
			  String dir = trg + File.separator + file.getName(); 
			  File trgFile = new File(dir);
			  if (!trgFile.exists())
				  return;
		    	  
			  if (!trgFile.isDirectory())
			  {
				  FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.fix.upd.file")+" "+trgFile.getName(),this); //$NON-NLS-1$
				  trgFile.delete();			  
				  FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.remove.upd")+" "+trgFile.getName(),this); //$NON-NLS-1$
			  } // END OF IF
		  }
	  }	  
	
	public void UninstallZipArchiveFix(FixData fxd)
	{
		ZipFile zipFileObj = null;

		try
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.fix.file")+" "+ fxd.getFileName()+" "+Messages.getString("FixInstaller.msg.from.dir")+" "+fxd.getTargetDir(),this); //$NON-NLS-1$ //$NON-NLS-2$
			File source_file = new File(fxd.getFileName());				
			zipFileObj = new ZipFile(source_file);
			Enumeration enum1 = zipFileObj.entries();
			ZipEntry zip_entry = null;
			while (enum1.hasMoreElements())
			{
				zip_entry = (ZipEntry)enum1.nextElement();
				m_current++;
				if (!FixUtil.isDirectory(fxd.getTargetDir(), zip_entry.getName()))
				{											
					String file_name = FixUtil.cleanPath(fxd.getTargetDir() + File.separator + zip_entry.getName());
					RemoveUpdateReadmeFile(file_name);
					if (FixUtil.isJarFile(zip_entry.getName()))
					{
						FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.fix.jar")+" "+ file_name,this); //$NON-NLS-1$
						FixUtil.removeFile(file_name);
					}							
					// Check if new file is package and can be unistalled
					if (FixUtil.isZipEntryPackage(zip_entry.getName()) && fxd.getPkgFlag())
					{
						m_current+=1;
						FixLoger.audit_log(Messages.getString("FixInstaller.msg.try.uninstall.pkg")+" "+ file_name,this); //$NON-NLS-1$
						if (FixUtil.isFileExist(file_name))
						{
							if (isBackUpExist(fxd.getName(),fxd.getTargetDir(),file_name))
							{
								FixLoger.audit_log(Messages.getString("FixInstaller.msg.found.bak.pkg")+" "+ zip_entry.getName()+" "+Messages.getString("FixInstaller.msg.try.restore"),this); //$NON-NLS-1$ //$NON-NLS-2$
								RestorePackage(fxd,zip_entry.getName());
							}
						}
						m_current+=1;
					}
												
					// Check if file is DB script and needs ti be installed
					if (FixUtil.isDBScript(zip_entry.getName()) && fxd.getDBScript())
					{
						if (FixUtil.isSelectedScript(fxd.jdbc,zip_entry.getName(),file_name))
						{
							m_current+=1;
							String msg = Messages.getString("FixInstaller.msg.WARN.this.fix")+" "+fxd.getName()+" "+Messages.getString("FixInstaller.msg.include.db.script")+" "+ zip_entry.getName(); //$NON-NLS-1$ //$NON-NLS-2$
							msg += " "+Messages.getString("FixInstaller.msg.db.script.cannot.uninstall"); //$NON-NLS-1$
							FixLoger.audit_log(msg,this);								
							m_current+=1;
						}
						else if (isSelectedUninstallScript(fxd.jdbc,zip_entry.getName(),file_name))
						{
							m_current+=1;
							FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.db.change")+" "+ zip_entry.getName(),this); //$NON-NLS-1$
							InstallDBScript(zip_entry.getName(),fxd.getTargetDir(),fxd.jdbc);
							m_current+=1;
							
						}
						else FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.ignore.db.script")+" "+ zip_entry.getName(),this);	 //$NON-NLS-1$
					}
						
						
				}
			} // End of WHILE
				
				enum1 = null;				
				zipFileObj.close();
				zipFileObj = null;
				zip_entry = null;
				source_file = null;
			
		}
		catch(Exception e)
		{
				e.printStackTrace();
				
				try {
					zipFileObj.close();
					zipFileObj = null;
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
		}		  
	}
	private void RemoveUpdateReadmeFile(String file_name)
	{
		String ext = file_name.substring(file_name.length()-4,file_name.length());
		if (ext.equals(".txt")) //$NON-NLS-1$
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.upd.readme")+" "+ file_name,this); //$NON-NLS-1$
			FixUtil.removeFile(file_name);
		}
	}
	public void UninstallArchive(FixData fxd)
	{
		ZipFile zipFileObj = null;

		try
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.fix.file")+" "+ fxd.getFileName()+" "+Messages.getString("FixInstaller.msg.from.dir")+" "+fxd.getTargetDir(),this); //$NON-NLS-1$ //$NON-NLS-2$
			File source_file = new File(fxd.getFileName());				
			zipFileObj = new ZipFile(source_file);
			Enumeration enum1 = zipFileObj.entries();
			ZipEntry zip_entry = null;
			while (enum1.hasMoreElements())
			{
				zip_entry = (ZipEntry)enum1.nextElement();
				m_current++;
				if (!FixUtil.isDirectory(fxd.getTargetDir(), zip_entry.getName()))
				{											
					String file_name = FixUtil.cleanPath(fxd.getTargetDir() + File.separator + zip_entry.getName());
					RemoveUpdateReadmeFile(file_name);
					if (FixUtil.isJarFile(zip_entry.getName()))
					{
						FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.fix.jar")+" "+ file_name,this); //$NON-NLS-1$
						FixUtil.removeFile(file_name);
					}							
					// Check if new file is package and can be unistalled
					if (FixUtil.isZipEntryPackage(zip_entry.getName()) && fxd.getPkgFlag())
					{	
						String zip_package = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+zip_entry);
						FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.zip.pkg")+" "+ zip_package,this); //$NON-NLS-1$
						if (FixUtil.isZipFile(zip_package))
							FixUtil.removeFile(zip_package);
					}
					// Check if file is DB script and needs ti be installed
					if (FixUtil.isDBScript(zip_entry.getName()) && fxd.getDBScript())
					{
						if (FixUtil.isSelectedScript(fxd.jdbc,zip_entry.getName(),file_name))
						{
							m_current+=1;
							String msg = Messages.getString("FixInstaller.msg.WARN.this.fix")+" "+fxd.getName()+" "+Messages.getString("FixInstaller.msg.include.db.script")+" "+ zip_entry.getName(); //$NON-NLS-1$ //$NON-NLS-2$
							msg += " "+Messages.getString("FixInstaller.msg.db.script.cannot.uninstall"); //$NON-NLS-1$
							FixLoger.audit_log(msg,this);								
							m_current+=1;
						}
						else if (isSelectedUninstallScript(fxd.jdbc,zip_entry.getName(),file_name))
						{
							m_current+=1;
							FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.db.change")+" "+ zip_entry.getName(),this); //$NON-NLS-1$
							InstallDBScript(zip_entry.getName(),fxd.getTargetDir(),fxd.jdbc);
							m_current+=1;
							
						}
						else FixLoger.audit_log(Messages.getString("FixInstaller.msg.uninstall.ignore.db.script")+" "+ zip_entry.getName(),this);	 //$NON-NLS-1$
					}
						
						
				}
			} // End of WHILE
				
				enum1 = null;				
				zipFileObj.close();
				zipFileObj = null;
				zip_entry = null;
				source_file = null;
			
		}
		catch(Exception e)
		{
				e.printStackTrace();
				
				try {
					zipFileObj.close();
					zipFileObj = null;
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
		}		  
		
	}
	/**
	 * UninstallPackage - uninstall package from IS system
	 * @param fxd - Fixdata object
	 */
	private void UninstallPackage(FixData fxd)
	{
		String trg = FixUtil.cleanPath(fxd.getTargetDir());
		String rep = File.separator + "replicate"+ File.separator +"inbound"; //$NON-NLS-1$ //$NON-NLS-2$
		String t = FixUtil.cleanPath(trg.substring(0,trg.length() - rep.length()));
		String pkg_name = FixWmUtil.getTargetPackageName(fxd.getFileName());
		String back_up_dir = FixUtil.cleanPath(t + File.separator + "replicate"+ File.separator +"salvage"+ File.separator +fxd.getName()+ File.separator +pkg_name); //$NON-NLS-1$ //$NON-NLS-2$
		
		File d = new File(back_up_dir);
		if (!d.exists())
		{
			//No backup to restore package
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.WARN.cannot.uninstall.fix")+" "+fxd.getName(),this); //$NON-NLS-1$
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.package")+" "+ pkg_name +" "+Messages.getString("FixInstaller.msg.bak.not.available")+ " [" +back_up_dir+"]",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			FixLoger.audit_log("********** "+Messages.getString("FixInstaller.msg.failed.to.uninstall")+" "+fxd.getName()+" ************",this); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		// Remove package zip file from inbound dir
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.fix")+ " "+fxd.getName()+" "+Messages.getString("FixInstaller.msg.pkg.from.inbound"),this); //$NON-NLS-1$ //$NON-NLS-2$
		File f = new File(fxd.getFileName());
		String pkg_zip = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+f.getName());
		FixUtil.removeFile(pkg_zip);
		
		// Remove real package from IS directory
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.pkg")+" "+ pkg_name+" " +Messages.getString("FixInstaller.msg.from.IS"),this); //$NON-NLS-1$ //$NON-NLS-2$
		String is_pkg_dir = FixUtil.cleanPath(t + File.separator + "packages" + File.separator + pkg_name); //$NON-NLS-1$
		FixUtil.removeFile(is_pkg_dir);
		// Copy package from backup	
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.restore.pkg")+" "+ pkg_name +Messages.getString("FixInstaller.msg.from.bakup")+" "+back_up_dir,this); //$NON-NLS-1$ //$NON-NLS-2$
		String is_dir = FixUtil.cleanPath(t + File.separator + "packages"+ File.separator + pkg_name); //$NON-NLS-1$
		FixUtil.copy_dir(back_up_dir,is_dir);
		
		// Delete backup dir
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.bakup.dir")+" "+back_up_dir,this); //$NON-NLS-1$
		FixUtil.removeFile(back_up_dir);	
		String salvage_fix_dir = FixUtil.cleanPath(t + File.separator + "replicate"+ File.separator +"salvage"+ File.separator +fxd.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		FixUtil.removeFile(salvage_fix_dir);
	}

	private void RestorePackage(FixData fxd, String zip_entry)
	{
		String salvage = "replicate"+File.separator+"salvage"; //$NON-NLS-1$ //$NON-NLS-2$
		String zip_package = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+zip_entry);
		String pkg = FixWmUtil.getTargetPackageName(zip_package);
		if (pkg == null || pkg.length() == 0)
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.ERR.pkg.name.not.found")+" "+zip_entry,this); //$NON-NLS-1$
			return;
		}
		String is_pkg = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+"packages"+File.separator+pkg); //$NON-NLS-1$
		String bakup_pkg = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+salvage+File.separator+fxd.getName()+File.separator+pkg);
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.zip.pkg")+" "+ zip_package,this); //$NON-NLS-1$
		if (FixUtil.isZipFile(zip_package))
			FixUtil.removeFile(zip_package);
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.is.pkg")+" "+ is_pkg,this); //$NON-NLS-1$
		FixUtil.removeFile(is_pkg);
		String is_dir = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+"packages"+File.separator+pkg); //$NON-NLS-1$
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.restore.is.pkg.from.backup")+" "+ bakup_pkg,this); //$NON-NLS-1$
		FixUtil.copy_dir(bakup_pkg,is_dir);		
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.remove.backup")+" "+ bakup_pkg,this); //$NON-NLS-1$
		FixUtil.removeFile(bakup_pkg);
		String fix_dir = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+salvage+File.separator+fxd.getName());
		if (FixUtil.isDirEmpty(fix_dir))
			FixUtil.removeFile(fix_dir);
	}		
			
	
	private boolean isBackUpExist(String fix_name,String trg,String file_name)
	{
		boolean rc = false;
		String pkg = FixWmUtil.getTargetPackageName(file_name);
		String dir = FixUtil.cleanPath(trg + File.separator + "replicate" + File.separator + "salvage" + File.separator + fix_name + File.separator + pkg); //$NON-NLS-1$ //$NON-NLS-2$
		File f = new File(dir);
		if (f.exists())
			rc = true;
		
		return rc;
	}

	private boolean isSelectedUninstallScript(FixDataJDBC jdbc_cnf,String dbscript, String file)
	{
		// Check if script is for install
		if (!isScriptUninstall(file))
			return false;
		// Check type of DB
		int end = dbscript.length()-4;
		int start = FixUtil.findStart(dbscript);
		String str = dbscript.substring(start,end);
		//System.out.println(str);
		if (str.equals(jdbc_cnf.getDBType()))
			return true;
		
		return false;
	}

	private boolean isScriptUninstall(String file)
	{
		String drop = "drop_"; //$NON-NLS-1$
		String name = FixUtil.getScriptName(file);
		String tmp = name.substring(0,drop.length());
		if (tmp.equals(drop))
			return true;
	
		return false;			
	}
	
	private void InstallDBScript(String db_script, String trg, FixDataJDBC jdbc_cnf)
	{
		FixDBScript db_obj = new FixDBScript();
		//String dbfile = db_script.replaceAll("/",File.separator);
		String script = trg + File.separator + db_script;
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.read.fix.db.script")+" "+ db_script,this); //$NON-NLS-1$
		if (db_obj.read_script(script))
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.read.db.script")+" "+ db_script,this); //$NON-NLS-1$
		else
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.ERR.read.db.script")+" "+ db_script+Messages.getString("FixInstaller.msg.failed.stop.script.install"),this); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.exec.db.script")+" "+ db_script,this); //$NON-NLS-1$
		String msg = db_obj.run_script(jdbc_cnf);
		FixLoger.audit_log(msg,this);
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.db.script")+" "+ db_script,this); //$NON-NLS-1$
	}
	
//////////////////////////////////////////////////
	
}
