package wm.fix.manager.opt.install;

import java.io.File;
import java.io.IOException;


import wm.fix.archive.util.FileUtils;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.install.FixLoger;
import wm.fix.manager.install.Messages;
import wm.fix.manager.opt.ui.RestoreProgress;

public class ProfileRestoreProcess 
{
	private int m_lengthOfTask = 0;
	private int m_current = 0;
	private String m_statMessage = null;
	private RestoreProgress m_rp = null;
	
	public ProfileRestoreProcess(RestoreProgress rp)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_rp = rp;
	}
	public void updateProgress()
	{
		if (m_rp != null)
			m_rp.updateProgress();
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
	 * getRestoreTaskSize method calculate the restore task
	 * length to display progress.
	 * @param fp FixProfile object
	 * @return task size for backup a profile
	 */
	public int getRestoreTaskSize(FixProfile fp)
	{
		int size = 0;
		if (fp == null)
			return size; //Nothing to do here
		
		String is_pkg_dir = fp.getISDir()+File.separator+"packages"; //$NON-NLS-1$
		File f = new File(is_pkg_dir);
		if (f.exists())
		{
			File dir_list[] = f.listFiles();
			for (int i = 0; i < dir_list.length; i++)
			{
				if (dir_list[i].isDirectory())
				{
					if (isWmPackage(dir_list[i].getName()))
						size++;
				}
			}
		}
		
		return size;
	}
    
	public void restoreProfile(FixProfile fp)
	{
		if (fp == null)
			return;
		String msg = Messages.getString("ProfileRestoreProcess.start.restore.pkg");
		FixLoger.audit_log("### "+msg+" "+fp.getName()+" ###", this); //$NON-NLS-1$ //$NON-NLS-2$
		String is_pkg_dir = fp.getISDir()+File.separator+"packages"; //$NON-NLS-1$
		String profile_dir = makeProfileName(fp.getName());
		String is_backup_dir = fp.getISDir()+File.separator+"replicate"+File.separator+"salvage" + File.separator + profile_dir; //$NON-NLS-1$ //$NON-NLS-2$
		try
		{
			File f_bak_dir = new File(is_backup_dir);
			if (f_bak_dir.exists())
			{
				// Get list of saved packages in backup directory
				File bak_dir_list[] = f_bak_dir.listFiles();
				for (int i = 0; i < bak_dir_list.length; i++)
				{
					// Restore packages from backup
					if (bak_dir_list[i].isDirectory())
					{
						// remove IS package directory
						File pkg_dir_test = new File(is_pkg_dir+File.separator+bak_dir_list[i].getName());
						if (pkg_dir_test.exists())
						{
							FixLoger.audit_log("***** "+Messages.getString("ProfileRestoreProcess.start.remove.pkg")+" "+pkg_dir_test.getPath()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							FileUtils.deleteDir(pkg_dir_test);
							FixLoger.audit_log("***** "+Messages.getString("ProfileRestoreProcess.complete.remove.pkg")+" "+pkg_dir_test.getPath()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						// Copy package directory from backup
						String dir = is_pkg_dir+File.separator+bak_dir_list[i].getName();
						File pkg_dir = new File(dir);
						if (!pkg_dir.exists())
						{
							pkg_dir.mkdirs();
							pkg_dir = new File(dir);
						}
						FixLoger.audit_log("***** "+Messages.getString("ProfileRestoreProcess.start.copy.pkg")+" "+bak_dir_list[i].getName()+" "+Messages.getString("ProfileRestoreProcess.to")+" "+dir+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						FileUtils.copyFiles(bak_dir_list[i],pkg_dir);
						m_current++;
						FixLoger.audit_log("***** "+Messages.getString("ProfileRestoreProcess.complete.copy.pkg")+" "+bak_dir_list[i].getName()+" "+Messages.getString("ProfileRestoreProcess.to")+" "+dir+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$						
					}
				}
			}
			
			FixUninstaller fu = new FixUninstaller(m_rp);
			fu.uninstallFixProfile(fp);
		}
		catch(IOException e)
		{
			FixLoger.audit_log("@@@@ "+Messages.getString("ProfileRestoreProcess.ERROR.during.restore")+" "+e.getMessage()+" @@@@@@@",this); //$NON-NLS-1$ //$NON-NLS-2$
		}
		FixLoger.audit_log("### "+Messages.getString("ProfileRestoreProcess.complete.restore.pkg.process")+" "+fp.getName()+" ###",this); //$NON-NLS-1$ //$NON-NLS-2$
		if (m_rp != null)
			m_rp.finishProgress();
		
	}
	
	private boolean isWmPackage(String n)
	{
		boolean rc = false;
		if (n.length() > 2)
		{
			String str = n.substring(0,2).toLowerCase();
			if (str.equals("wm")) //$NON-NLS-1$
				rc = true;
		}
		return rc;
	}
	private String makeProfileName(String str)
	{
		String nm = ""; //$NON-NLS-1$
		nm = str.replace(' ','_');
		return nm;
	}

}
