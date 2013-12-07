package wm.fix.manager.opt.install;

import java.io.File;
import java.io.IOException;


import wm.fix.archive.util.FileUtils;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.install.FixLoger;
import wm.fix.manager.install.Messages;
import wm.fix.manager.opt.ui.BackupProgress;

public class ProfileBackupProcess 
{
	private int m_lengthOfTask = 0;
	private int m_current = 0;
	private String m_statMessage = null;
	private BackupProgress m_bp = null;
	
	public ProfileBackupProcess(BackupProgress bp)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_bp = bp;
	}

	public void updateProgress()
	{
		if (m_bp != null)
			m_bp.updateProgress();
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
	 * getBackupTaskSize method calculate the backup task
	 * length to display progress.
	 * @param fp FixProfile object
	 * @return task size for backup a profile
	 */
	public int getBackupTaskSize(FixProfile fp)
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
    
	public void backupProfile(FixProfile fp)
	{
		if (fp == null)
			return;
		FixLoger.audit_log("### "+Messages.getString("ProfileBackupProcess.start.bak.pkg")+" "+fp.getName()+" ###", this); //$NON-NLS-1$ //$NON-NLS-2$
		String is_pkg_dir = fp.getISDir()+File.separator+"packages"; //$NON-NLS-1$
		String profile_dir = makeProfileName(fp.getName());
		String is_backup_dir = fp.getISDir()+File.separator+"replicate"+File.separator+"salvage" + File.separator + profile_dir; //$NON-NLS-1$ //$NON-NLS-2$
		try
		{
			File f_dir = new File(is_backup_dir);
			if (!f_dir.exists())
			{
				f_dir.mkdirs(); // make sure directory exists if not create it
				f_dir = new File(is_backup_dir);
			}
			File f = new File(is_pkg_dir);
			if (f.exists())
			{
				File dir_list[] = f.listFiles();
				for (int i = 0; i < dir_list.length; i++)
				{
					// Copy directory to back up location
					if (dir_list[i].isDirectory())
					{
						if (isWmPackage(dir_list[i].getName()))
						{
							// Make backup package directory
							String dir = is_backup_dir+File.separator+dir_list[i].getName();
							File bak_dir = new File(dir);
							if (!bak_dir.exists())
							{
								bak_dir.mkdirs();
								bak_dir = new File(dir);
							}
							FixLoger.audit_log("***** "+Messages.getString("ProfileBackupProcess.start.copy.pkg")+" "+dir_list[i].getName()+" "+Messages.getString("ProfileBackupProcess.to")+" "+dir+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							FileUtils.copyFiles(dir_list[i],bak_dir);
							m_current++;
							FixLoger.audit_log("***** "+Messages.getString("ProfileBackupProcess.complete.copy.pkg")+" "+dir_list[i].getName()+" "+Messages.getString("ProfileBackupProcess.to")+" "+dir+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			}
		}
		catch(IOException e)
		{
			FixLoger.audit_log("@@@@ "+Messages.getString("ProfileBackupProcess.ERROR.during.bak")+" "+e.getMessage()+" @@@@@@@",this); //$NON-NLS-1$ //$NON-NLS-2$
		}
		FixLoger.audit_log("### "+Messages.getString("ProfileBackupProcess.complete.bak.pkg.process")+" "+fp.getName()+" ###",this); //$NON-NLS-1$ //$NON-NLS-2$
		if (m_bp != null)
			m_bp.finishProgress();
		
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
