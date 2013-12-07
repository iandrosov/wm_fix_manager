package wm.fix.manager.install;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import wm.fix.archive.util.FileUtils;
import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixDataConstants;
import wm.fix.manager.data.FixDataJDBC;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.data.FixProfileData;
import wm.fix.manager.opt.ui.BackupProgress;
import wm.fix.manager.opt.ui.FixUninstallProgress;
import wm.fix.manager.opt.ui.RestoreProgress;
import wm.fix.manager.ui.FixInstallProgress;
import wm.fix.manager.util.FixUtil;
import wm.fix.manager.util.FixWmUtil;

public class FixInstaller 
{
	private int m_lengthOfTask = 0;
	private int m_current = 0;
	private String m_statMessage = null;
	private FixInstallProgress m_fip = null;
	private FixUninstallProgress m_fup = null;
	private BackupProgress m_bp = null;
	private RestoreProgress m_rp = null;
	
	public FixInstaller()
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
	}

	public FixInstaller(FixInstallProgress fip)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_fip = fip;
	}
	
	public FixInstaller(BackupProgress bp)
	{
		m_lengthOfTask = 0;
		m_current = 0;
		m_statMessage = null;
		m_bp = bp;
	}
	
	public void updateProgress()
	{
		if (m_fip != null)
			m_fip.updateProgress();
		else if (m_fup != null)
			m_fup.updateProgress();
		else if (m_bp != null)
			m_bp.updateProgress();
		else if (m_rp != null)
			m_rp.updateProgress();
	}
	public void finishProgress()
	{
		if (m_fip != null)
			m_fip.finishProgress();
		else if (m_fup != null)
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
    
	public void installFixProfile(FixProfileData fxpd, String name)
	{
		if (fxpd == null)
			return;
		installFixProfile(fxpd.getProfile(name));
	}
	
	public void installFixProfile(FixProfile fp)
	{
		
		if (fp == null)
			return;
		FixLoger.audit_log("### "+Messages.getString("FixInstaller.msg.start.inst.profile")+fp.getName()+" ###", this); //$NON-NLS-1$ //$NON-NLS-2$
		FixData[] fxd_list = fp.getFixList();
		if (fxd_list != null)
		{
			for (int i = 0; i < fxd_list.length; i++)
			{
				FixLoger.audit_log("****** "+Messages.getString("FixInstaller.msg.start.inst.fix")+fxd_list[i].getName()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$
				installFix(fxd_list[i],true);
				FixLoger.audit_log("****** "+Messages.getString("FixInstaller.msg.complete.inst.fix")+fxd_list[i].getName()+" ******",this); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		FixLoger.audit_log("### "+Messages.getString("FixInstaller.msg.complete.inst.profile")+fp.getName()+" ###",this); //$NON-NLS-1$ //$NON-NLS-2$
		finishProgress();
	}

	
	/**
	 * installFix method based on Fix metadata installs a fix
	 * by requested parameters
	 */	
	public void installFix(FixData fxd, boolean internal_access)
	{
		if (fxd == null)
			return;
		// If fix need to be unziped into directory
		if (fxd.getUnzipFlag())
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.start.unzip")+ " " + fxd.getFileName()+ " " + Messages.getString("FixInstaller.msg.into")+" [" + fxd.getTargetDir() + "]",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			unzipWmFix(fxd.getFileName(), fxd.getSourceDir(), fxd.getTargetDir(),fxd.getPkgFlag(),fxd.getName(),fxd.getDBScript(),fxd.jdbc);
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.unzip")+ " " + fxd.getFileName() + " " + Messages.getString("FixInstaller.msg.into") + " [" + fxd.getTargetDir() + "]",this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			//If packages included in fix install them
			//if (fxd.getPkgFlag())
			//{
			//	FixLoger.audit_log("Fix: "+fxd.getFileName()+" includes packages.");
			//	InstallPackageFixes(fxd.getSourceDir(), fxd.getTargetDir());
			//}
		}
		if (fxd.getCopyFlag()) // Fix just copy directive
		{
			m_current+=1;
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.fix")+" "+fxd.getFileName()+" "+Messages.getString("FixInstaller.msg.has.upd.file.copy"),this); //$NON-NLS-1$ //$NON-NLS-2$
			InstallUpdates(fxd.getFileName(), fxd.getTargetDir());
			m_current+=1;
			// Check if Fix is package
			if (FixUtil.isPackage(fxd.getFileName(), fxd.getTargetDir()))
			{
				m_current+=1;
				InstallPackage(fxd);
				m_current+=1;
			}			
		}
		if (!internal_access)
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.inst.fix")+ " " + fxd.getName(),this); //$NON-NLS-1$
			finishProgress();
		}
		fxd.setInstalled(true);
		fxd.setStatus(FixDataConstants.FIX_INSTALL_OK);
	}
	
	
	/**
	 * getFixInstallTaskSize method calculate the installation task
	 * length to display progress.
	 * @param fp FixProfile object
	 * @return task size for installing a Fix profile
	 */
	public int getFixInstallTaskSize(FixProfile fp)
	{
		int size = 0;
		if (fp == null)
			return size; //Nothing to do here
		
		FixData fd[] = fp.getFixList();
		for (int i = 0; i < fd.length; i++)
		{
			size += getFixInstallTaskSize(fd[i]);
		}
		
		return size;
	}
	
	/**
	 * getFixInstallTaskSize method calculate the installation task
	 * length to display progress.
	 * @param fxd - FixData object
	 * @return int number representing arbitrary length for task
	 */
	public int getFixInstallTaskSize(FixData fxd)
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
	
	private void InstallPackage(FixData fxd)
	{
		if (!fxd.getPackageName().equals("Package Name")) //$NON-NLS-1$
			InstallISPackage(fxd.getPackageName(), fxd.getFileName(), fxd.getSourceDir(), fxd.getTargetDir(), fxd.getName());
	}
	
	private void InstallISPackage(String pkg, String pkg_zip, String src, String trg, String fixname)
	{	
		String rep = "/replicate/inbound"; //$NON-NLS-1$
		String t = trg.substring(0,trg.length()-rep.length());
		String target = t + File.separator + "packages" + File.separator + pkg; //$NON-NLS-1$
		String source = pkg_zip;
		String tmp = source.replace('/',File.separatorChar);
		
		// Save package before update
		String salvage = t + File.separator + "replicate" + File.separator + "salvage"+ File.separator + fixname + File.separator + pkg; //$NON-NLS-1$ //$NON-NLS-2$
		if (!isPathExist(target))
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.pkg.dir")+" "+target+" "+Messages.getString("FixInstaller.msg.does.not.exist.no.install")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.start.bak.pkg")+" "+pkg+" "+Messages.getString("FixInstaller.msg.to.dir")+ salvage +" "+ Messages.getString("FixInstaller.msg.before.unpack"),this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		FixUtil.copy_dir(target, salvage);
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.back.pkg")+" "+pkg,this); //$NON-NLS-1$
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.unzip.pkg") +" "+ pkg_zip +" "+ Messages.getString("FixInstaller.msg.into.dir")+" "+target,this); //$NON-NLS-1$ //$NON-NLS-2$
		uzip(tmp, target);
		FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.unzip.pkg")+" "+ pkg_zip,this); //$NON-NLS-1$
		// Update manifest
		String manifest_file = target + File.separator + "manifest.v3"; //$NON-NLS-1$
		String rel = target + File.separator + "manifest.rel"; //$NON-NLS-1$
		// Make copy of original manifest;
		File manifest = new File(manifest_file);
		if (manifest != null)
		{
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.found.manifest.file")+" "+manifest_file+Messages.getString("FixInstaller.msg.for.pkg")+" "+pkg+" "+Messages.getString("FixInstaller.msg.make.bak.copy"),this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			try
			{
				String cp = target + File.separator + "manifest.bak"; //$NON-NLS-1$
				FileUtils.copyFile(manifest,new File(cp));
			}
			catch(Exception e)
			{e.printStackTrace();}
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.update")+" "+manifest_file+" "+Messages.getString("FixInstaller.msg.with.fix.info"),this); //$NON-NLS-1$ //$NON-NLS-2$
			FixWmUtil.update_manifest(manifest_file, rel);
		}
	}
	

		
	private boolean isPathExist(String path)
	{
		boolean rc = false;
		File f = new File(path);
		if (f != null)
			rc = f.exists();
					
		return rc;
	}
	/**
	 * isPackage method check the target directory and file name
	 * If both mathch .zip and replicate/inbound it is package
	 * @param file_name - full file name path for fix
	 * @param trg - target dorectory
	 * @return - true is it is matched to package false otherwise
	 */
/*************MOVED to FixUtil***********************	
	private boolean isPackage(String file_name, String trg)
	{
		boolean rc = false;
		if (FixUtil.isZipFile(file_name) && isReplicateDir(trg))
			rc = true;
		
		return rc;
	}
	
	private boolean isReplicateDir(String trg)
	{
		boolean rc = false;
		String replicate = "replicate"; //$NON-NLS-1$
		String tmp = "inbound"; //$NON-NLS-1$
		String in = replicate + File.separator + tmp;
		if (trg.length() > in.length())
		{			
			String dir = trg.substring(trg.length()-tmp.length(), trg.length());
			String rep = trg.substring(trg.length()-in.length(),(trg.length()-tmp.length())-1);
			if (tmp.equals(dir) && replicate.equals(rep))
				rc = true;
		}
		return rc;
	}
	
	private void removeFile(String file)
	{
		File f = new File(file);
		if (f.exists())
			if (f.isDirectory())
				FileUtils.deleteDir(f);
			else if (f.isFile())
					f.delete();
	}
	
		private int findStart(String str)
		{
			for (int i = str.length()-1; i > 0; i--)
			{
				char ch = str.charAt(i);
				if (ch == '_')
					return i+1;
			}
			return 0;
		}
	
	  private int getZipFileTask(String zip)
	  {
		  int size = 0;
		  ZipFile zipFileObj = null;
		  if (!FixUtil.isFileExist(zip))
			  return size;
		  
		  File source_file = new File(zip);
		  if (!FixUtil.isZipFile(source_file))
			  return size;
			  
		  try
		  {
			  
			  zipFileObj = new ZipFile(source_file);
			  size = zipFileObj.size();
			  zipFileObj.close();
			  zipFileObj = null;

			  return size;
		  }
		  catch(Exception e)
		  {
			  zipFileObj = null;			  
		  }
		  return 0;
	  }

		/**
		 * getScriptName method return a name of DB script derived from full
		 * path file name
		 * @param file - full path file name of DB script
		 * @return - String name of DB script
		 *
		private String getScriptName(String file)
		{
			String str = file;
			File f = new File(file);
			if (f.exists())
				str = f.getName();
			return str;
		}
	  private void copy_dir(String src, String trg)
	  {
		  File s = new File(src);
		  File t = new File(trg);
		  //t.mkdirs();
			try
			{
				FileUtils.copyFiles(s, t);
			}
			catch(Exception e)
			{e.printStackTrace();}
		  
	  }
		private boolean isDirectory(String trg, String str)
		{
			
			String tmp = str.substring(str.length()-1,str.length());
			if (tmp.equals("/")) // directory check //$NON-NLS-1$
			{
				String dir = trg+File.separator+str.substring(0,str.length()-1);
				File fl = new File(dir);
				fl.mkdirs();
				return true;	
			}
			return false;
		}

		private boolean isZipEntryPackage(String str)
		{
			if (str.length() < 18)
				return false;
			
			String tmp = str.substring(0, 18);
			if (tmp.equals("replicate/inbound/")) //$NON-NLS-1$
			{
				// Check for zip file
				tmp = str.substring(str.length()-4,str.length());
				if (tmp.equals(".zip")) //$NON-NLS-1$
					return true;
				else
					return false;
			}
			else return false;
		}
	
		/**
		 * isSelectedScript check if found DB script file is of select DB type
		 * match with configuration DB Type
		 * @param jdbc_cnf
		 * @param dbscript
		 * @return
		 *
		private boolean isSelectedScript(FixDataJDBC jdbc_cnf,String dbscript, String file)
		{
			// Check if script is for install
			if (!isScriptInstall(file))
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
					
		private boolean isScriptInstall(String file)
		{
			String create = "create_"; //$NON-NLS-1$
			String mig = "mig_"; //$NON-NLS-1$
			String postmig = "postmig_"; //$NON-NLS-1$
			String name = FixUtil.getScriptName(file);
			String tmp = name.substring(0,create.length());
			if (tmp.equals(create))
				return true;
			tmp = name.substring(0,mig.length());
			if (tmp.equals(mig))
				return true;
			tmp = name.substring(0,postmig.length());
			if (tmp.equals(postmig))
				return true;
		
			return false;
		}
	
**********************************/	
	/**
	 * unzipWmFix utility method to unzip a fix
	 * into given directory.
	 */
	private void unzipWmFix(String fix, String src, String trg, boolean pkg, String fixname, boolean db, FixDataJDBC jdbc)
	{
		  File file = new File(fix);
		  if (!file.exists())
			  return;
		  
		  if (!file.isDirectory())
		  {
			  if (FixUtil.isZipFile(file.getName()))
				  unpack(fix, src, trg, pkg, fixname, db, jdbc);
			  else
			  {
				  FixLoger.audit_log(Messages.getString("FixInstaller.msg.WARN.file")+" "+file.getName()+" "+Messages.getString("FixInstaller.msg.not.zip.file"),this); //$NON-NLS-1$ //$NON-NLS-2$
			  }
		  }	
	}
	
	  /*
	   * this method installs standard packages by copiing them to 
	   * replicate/inbound directory
	   */
	/*
	  private void InstallPackageFixes(String fix, String trg)
	  {
		  String dir = fix + File.separator + "replicate";
		  File file = new File(dir);
		  if (!file.exists())
			  return;
		  FixLoger.audit_log("InstallPackageFixes: "+fix);
		  String lst[] = file.list();
		  for (int i = 0; i < lst.length; i++)
		  {
			  String src = dir + File.separator + lst[i];
			  String res = trg + File.separator + "replicate" + File.separator + "inbound" + File.separator + lst[i];
			  File ff = new File(src);
			  if (!ff.isDirectory())
			  {	 
				  FixLoger.audit_log("src - "+src);
				  if (!isTextFile(ff.getName()))
				  {				  
					 
					  copy_dir(src,res);
				  }
			  } // END OF IF
		  } // END OF FOR
	  }
	  */
	  /**
	   * InstallUpdates method to install/copy simple update fix
	   * @param fix - full path file name of the fix source
	   * @param trg - full path directory where to place th update
	   */
	  private void InstallUpdates(String fix, String trg)
	  {
		  File file = new File(fix);
		  if (!file.exists())
			  return;
			  	  
		  if (!file.isDirectory())
		  {
			  FixLoger.audit_log(Messages.getString("FixInstaller.msg.install.fix.upd.file")+" "+fix+" "+Messages.getString("FixInstaller.msg.into.dir")+" "+trg,this); //$NON-NLS-1$ //$NON-NLS-2$
			  String dir = trg + File.separator + file.getName(); 
			  FixUtil.copy_dir(fix,dir);
			  FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.copy.upd.file")+" "+fix,this); //$NON-NLS-1$
		  } // END OF IF
	  }
	  	  
	  public void unpack(String fix_file, String src, String trg, boolean pkg, String fixname, boolean db, FixDataJDBC jdbc_cnf)
	  {
			// Get input values
			String zip_file = fix_file;
			String target = trg;
			String file_name = target;
			FileOutputStream ostream = null;
			InputStream read_file = null;
			ZipFile zipFileObj = null;

			try
			{
				File source_file = new File(zip_file);
				//String temp = source_file.getName().substring(0,source_file.getName().length() - 4);
				//file_name += File.separator + temp;
				FixLoger.audit_log(Messages.getString("FixInstaller.msg.unzip.fix.file")+" "+ zip_file+Messages.getString("FixInstaller.msg.into.dir")+" "+trg,this); //$NON-NLS-1$ //$NON-NLS-2$
				zipFileObj = new ZipFile(source_file);
				Enumeration enum1 = zipFileObj.entries();
				ZipEntry zip_entry = null;
				while (enum1.hasMoreElements())
				{
					zip_entry = (ZipEntry)enum1.nextElement();
					//System.out.println(zip_entry.getName());
					m_current++;
					if (!FixUtil.isDirectory(trg, zip_entry.getName()))
					{						
						read_file = zipFileObj.getInputStream(zip_entry);
						byte[] b_data = new byte[255];
						int rc = 0;
						file_name = FixUtil.cleanPath(trg + File.separator + zip_entry.getName());
						ostream = new FileOutputStream(file_name);

						while (rc != -1 && read_file != null)
						{

							// read data from file
							rc = read_file.read(b_data);
							if (rc != -1)
							{
								ostream.write(b_data,0,rc);
							}
						}
						if (read_file != null)
							read_file.close();
						if (ostream != null)
							ostream.close();
						// Check if new file is package and need further
						// unzip process to IS dir
						if (FixUtil.isZipEntryPackage(zip_entry.getName()) && pkg)
						{
							m_current+=1;
							FixLoger.audit_log(Messages.getString("FixInstaller.msg.fix.include.pkg")+" "+ zip_entry.getName(),this); //$NON-NLS-1$
							UnzipISPackage(zip_entry.getName(), src, trg, fixname);
							m_current+=1;
						}
						// Check if file is DB script and needs ti be installed
						if (FixUtil.isDBScript(zip_entry.getName()) && db)
						{
							if (FixUtil.isSelectedScript(jdbc_cnf,zip_entry.getName(),file_name))
							{
								m_current+=1;
								FixLoger.audit_log(Messages.getString("FixInstaller.msg.fix.include.db.script")+" "+ zip_entry.getName(),this); //$NON-NLS-1$
								InstallDBScript(zip_entry.getName(),trg,jdbc_cnf);
								m_current+=1;
							}
						}
					}
				}
				enum1 = null;				
				zipFileObj.close();
				zipFileObj = null;
				ostream = null;
				//b_data = null;
				zip_entry = null;
				source_file = null;
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				try{
				ostream.close();
				read_file.close();
				ostream = null;
				zipFileObj.close();
				zipFileObj = null;
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}		  
	  }
		public void uzip(String src, String trg)
		{
			// Get input values
			String zip_file = src;
			String target = trg;
			String file_name = target;
			FileOutputStream ostream = null;
			InputStream read_file = null;
			ZipFile zipFileObj = null;

			try
			{
				File source_file = new File(zip_file);
				//String temp = source_file.getName().substring(0,source_file.getName().length() - 4);
				//file_name += File.separator + temp;

				zipFileObj = new ZipFile(source_file);
				Enumeration enum1 = zipFileObj.entries();
				ZipEntry zip_entry = null;
				while (enum1.hasMoreElements())
				{
					zip_entry = (ZipEntry)enum1.nextElement();
					//System.out.println(zip_entry.getName());
					
					if (!FixUtil.isDirectory(trg, zip_entry.getName()))
					{
						read_file = zipFileObj.getInputStream(zip_entry);
						byte[] b_data = new byte[255];
						int rc = 0;
						file_name = trg + File.separator + zip_entry.getName();
						ostream = new FileOutputStream(file_name);

						while (rc != -1 && read_file != null)
						{

							// read data from file
							rc = read_file.read(b_data);
							if (rc != -1)
							{
								ostream.write(b_data,0,rc);
							}
						}
						if (read_file != null)
							read_file.close();
						
					}
				}
				enum1 = null;
				ostream.close();
				zipFileObj.close();
				zipFileObj = null;
				ostream = null;
				//b_data = null;
				zip_entry = null;
				source_file = null;
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				try{
				ostream.close();
				read_file.close();
				ostream = null;
				zipFileObj.close();
				zipFileObj = null;
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		
		}
		
		public void UnzipISPackage(String pkg_zip, String src, String trg, String fixname)
		{			
			/*String st = pkg_zip.substring(18,pkg_zip.length());
			int pos = findMatch(st, '_');
			String pkg_name = st.substring(0,pos);
			*/
			String zip_file_pkg = trg + File.separator + pkg_zip;
			String pkg_name = FixWmUtil.getTargetPackageName(zip_file_pkg);
			
			String target = trg + File.separator + "packages" + File.separator + pkg_name; //$NON-NLS-1$
			String source = trg + File.separator + pkg_zip;
			String tmp = source.replace('/',File.separatorChar);
			
			// Save package before update
			String salvage = trg + File.separator + "replicate" + File.separator + "salvage"+ File.separator + fixname + File.separator + pkg_name; //$NON-NLS-1$ //$NON-NLS-2$
			if (!isPathExist(target))
			{
				FixLoger.audit_log(Messages.getString("FixInstaller.msg.pkg.or.dir")+" "+target+" "+Messages.getString("FixInstaller.msg.not.eixst.will.not.install"),this); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}

			FixLoger.audit_log(Messages.getString("FixInstaller.msg.start.bak.pkg")+" "+pkg_name+" "+Messages.getString("FixInstaller.msg.to.dir")+" " +salvage+" " +Messages.getString("FixInstaller.msg.before.unpack"),this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			FixUtil.copy_dir(target, salvage);
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.backup.pkg")+" "+pkg_name,this); //$NON-NLS-1$
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.unzip.pkg")+" " +pkg_zip +" "+Messages.getString("FixInstaller.msg.into.dir")+" "+target,this); //$NON-NLS-1$ //$NON-NLS-2$
			uzip(tmp, target);
			FixLoger.audit_log(Messages.getString("FixInstaller.msg.complete.unzip.pkg")+" "+ pkg_zip,this); //$NON-NLS-1$
			// Update manifest
			String manifest_file = target + File.separator + "manifest.v3"; //$NON-NLS-1$
			String rel = target + File.separator + "manifest.rel"; //$NON-NLS-1$
			// Make copy of original manifest;
			File manifest = new File(manifest_file);
			if (manifest != null)
			{
				FixLoger.audit_log(Messages.getString("FixInstaller.msg.found.manifest.file")+" "+manifest_file+" "+Messages.getString("FixInstaller.msg.for.pkg")+" "+pkg_name+" "+Messages.getString("FixInstaller.msg.make.backaup.copy"),this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				try
				{
					String cp = target + File.separator + "manifest.bak"; //$NON-NLS-1$
					FileUtils.copyFile(manifest,new File(cp));
				}
				catch(Exception e)
				{e.printStackTrace();}
				FixLoger.audit_log(Messages.getString("FixInstaller.msg.update")+" "+manifest_file+" "+Messages.getString("FixInstaller.msg.with.fix.info"),this); //$NON-NLS-1$ //$NON-NLS-2$
				FixWmUtil.update_manifest(manifest_file, rel);
			}
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
		
}
