package wm.fix.manager.opt.install;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixDataConstants;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.data.FixProfileData;
import wm.fix.manager.util.FixUtil;
import wm.fix.manager.util.FixWmUtil;

public class FixAnalyzer 
{
	public FixAnalyzer(){}
	
	/**
	 * evaluateProfile method eveluate each Fix included in
	 * profile and set its status adn installed flags
	 * @param fp - FixProfile object
	 */
    public void evaluateProfile(FixProfile fp)
    {
    	if (fp == null)
    		return;      
		FixData[] fxd_list = fp.getFixList();
		if (fxd_list != null)
		{
			for (int i = 0; i < fxd_list.length; i++)
			{				
				int rc = analyzeFix(fxd_list[i]);
				fxd_list[i].setStatus(rc);
				fxd_list[i].setInstalled(isFixInstalled(rc));
			}
		}	    	
    }
	
	/**
	 * analyzeProfile method analyzes WM environment based on selected
	 * rules and determaines if a requested profile has been installed.
	 * 
	 * @param fxpd - FixProfileData object representing all configured profiles
	 * @param name - name of profile to be analyzed
	 * @return
	 */
	public int analyzeProfile(FixProfileData fxpd, String name)
	{
		if (fxpd == null)
			return FixDataConstants.FIX_INSTALL_INIT;
		int rc = analyzeProfile(fxpd.getProfile(name));	
		return rc;
	}
	
	/**
	 * analyzeProfile method analyzes WM environment based on selected
	 * rules and determaines if a requested profile has been installed.
	 *  
	 * @param fp - FixProfile object selected to be analyzed
	 * @return
	 */
	public int analyzeProfile(FixProfile fp)
	{
		if (fp == null)
			return FixDataConstants.FIX_INSTALL_INIT;
		int result = FixDataConstants.FIX_INSTALL_INIT;
		int err = 0;
		int warn = 0;
		int init = 0;
		int ok = 0;
		FixData[] fxd_list = fp.getFixList();
		if (fxd_list != null)
		{
			for (int i = 0; i < fxd_list.length; i++)
			{				
				int rc = analyzeFix(fxd_list[i]);
				if( rc==FixDataConstants.FIX_INSTALL_INIT )
					init++;
				else if(rc==FixDataConstants.FIX_INSTALL_OK)
					ok++;
				else if(rc==FixDataConstants.FIX_INSTALL_ERROR)
					err++;
				else if(rc==FixDataConstants.FIX_INSTALL_WARNING)
					warn++;
				else
					init++; // Default to init status				
			}
			// Decide profile status
			if (fxd_list.length == ok)
				result = FixDataConstants.FIX_INSTALL_OK;
			else if (err > 0)
				result = FixDataConstants.FIX_INSTALL_ERROR;
			else if (warn > 0)
				result = FixDataConstants.FIX_INSTALL_WARNING;
			else
				result = FixDataConstants.FIX_INSTALL_INIT;
		}	
		return result;
	}
	
	/**
	 * analyzeFix method analyzes WM environment based on selected
	 * rules and determaines if a requested fix has been installed.
	 *  
	 * @param fd - FixData object representing fix to be analyzed
	 * @return
	 */
	public int analyzeFix(FixData fd)
	{
		if (fd == null)
			return FixDataConstants.FIX_INSTALL_INIT;
		
		return isFixInstalled(fd);
	}
	
	/**
	 * isFixInstalled method to get boolean value of fix
	 * install evaluation.
	 * @param fix_status - int numeric value representing
	 * fix installtion status as result of analysis
	 * @return boolean true/false if fix installed or not respectively
	 */
    public boolean isFixInstalled(int fix_status)
    {
    	if (fix_status == FixDataConstants.FIX_INSTALL_ERROR
    		|| fix_status ==FixDataConstants.FIX_INSTALL_INIT)
    		return false;
    	else
    		return true;
    }

    /**
     * getStatusImage returns an Image object representing
     * a Fix status.
     * @param d - System Display object
     * @param status - int numeric status value
     * @return Image object craeted from relative path
     */
    public Image getStatusImage(Display d, int status)
    {
    	String path = "images/fix.gif";
    	switch (status)
    	{
    		case FixDataConstants.FIX_INSTALL_ERROR:
    			path = FixDataConstants.FIX_ICON_ERROR;
    			break;
    		case FixDataConstants.FIX_INSTALL_INIT:
    			path = FixDataConstants.FIX_ICON_UNDEF;
    			break;
    		case FixDataConstants.FIX_INSTALL_OK:
    			path = FixDataConstants.FIX_ICON_OK;
    			break;
    		case FixDataConstants.FIX_INSTALL_WARNING:
    			path = FixDataConstants.FIX_ICON_WARN;
    			break;
    	}
    	
    	Image img = FixUtil.createImage(d, path);
    	return img;
    }
    
    /**
     * isFixInstalled private utility method
     * @param fd - FixData object to analyze
     * @return - int value based on result of analysis. 
     * Possible return constant values:
     * FixDataConstants.FIX_INSTALL_ERROR
     * FixDataConstants.FIX_INSTALL_UNDEF
     * FixDataConstants.FIX_INSTALL_OK
     * FixDataConstants.FIX_INSTALL_WARNING
     */
	private int isFixInstalled(FixData fd)
	{
		
		if (FixUtil.isJarFile(fd.getFileName()))
			return isFixJarInstalled(fd);
		
		if (FixUtil.isZipFile(fd.getFileName()))
		{
			// Check amnifest if include fix or not
			if (fd.getCopyFlag() && fd.getPkgFlag())
			{
				if (!isManifestIncludeFix(fd))
					return FixDataConstants.FIX_INSTALL_ERROR;
			}
			int zip_count = getZipFixWeight(fd);
			int fix_count = getFixInstalledWeight(fd);
			//System.out.println("Analyze - zip="+Integer.toString(zip_count)+" fix="+Integer.toString(fix_count));
			if (zip_count == 0)
				return FixDataConstants.FIX_INSTALL_UNDEF;
			if (zip_count == fix_count)
				return FixDataConstants.FIX_INSTALL_OK;
			else if (fix_count != 0 && fix_count < zip_count)
				return FixDataConstants.FIX_INSTALL_WARNING;
			else
				return FixDataConstants.FIX_INSTALL_ERROR;
				
		}
		return FixDataConstants.FIX_INSTALL_ERROR;
	}
	
	private int isFixJarInstalled(FixData fd)
	{
		File f = new File(fd.getFileName());
		String F_jar = fd.getTargetDir() + File.separator + f.getName();
		
		if (FixUtil.isFileExist(F_jar))
		{
			//System.out.println(F_jar + " INSTALLED");
			return FixDataConstants.FIX_INSTALL_OK;
		}
		else
			return FixDataConstants.FIX_INSTALL_ERROR;
		
	}
	/**
	 * getZipFixWeight calculate approximate value of fix zip file
	 * @param fd
	 * @return
	 */
	private int getZipFixWeight(FixData fd)
	{
		ZipFile zipFileObj = null;
		int weight = 0;
		try
		{
			File source_file = new File(fd.getFileName());
			zipFileObj = new ZipFile(source_file);
			Enumeration enum1 = zipFileObj.entries();
			ZipEntry zip_entry = null;
			while (enum1.hasMoreElements())
			{
				zip_entry = (ZipEntry)enum1.nextElement();
								
				if (FixUtil.isJarFile(zip_entry.getName()))
				{										
					weight++;
				} 
				else if (FixUtil.isClassFile(zip_entry.getName()))
				{
					weight++;
				}
				// Check if new file is package and if its installed
				// into IS directory
				else if (FixUtil.isZipEntryPackage(zip_entry.getName()) && fd.getPkgFlag())
				{									
					weight+=2;
				}
				else if (!zip_entry.isDirectory()) 
					weight++;						
					/*
					// Check if file is DB script and needs ti be installed
					if (isDBScript(zip_entry.getName()) && fd.getDBScript())
					{
						if (isSelectedScript(fd.jdbc,zip_entry.getName()))
						{
							
						}
					}*/
				
			} // END OF WHILE
			
			enum1 = null;			
			zipFileObj.close();
			zipFileObj = null;
			zip_entry = null;
			source_file = null;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			try 
			{						
				zipFileObj.close();
				zipFileObj = null;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}	
		return weight;
		
	}
	
	private int getFixInstalledWeight(FixData fd)
	{		
		ZipFile zipFileObj = null;
		int weight = 0;
		try
		{
			String pkg = FixWmUtil.getTargetPackageName(fd.getFileName());
			File source_file = new File(fd.getFileName());
			zipFileObj = new ZipFile(source_file);
			Enumeration enum1 = zipFileObj.entries();
			ZipEntry zip_entry = null;
			while (enum1.hasMoreElements())
			{
				zip_entry = (ZipEntry)enum1.nextElement();
				
				//String file_name = FixUtil.cleanPath(fd.getTargetDir() + File.separator + zip_entry.getName());
				String file_name = getSerchFileName(fd, zip_entry.getName(),pkg);
				if (FixUtil.isJarFile(zip_entry.getName()))
				{	
					String root = getRootInstallPath(FixUtil.cleanPath(fd.getTargetDir()));
					String jar_file = "";
					if (pkg != null && pkg.length() > 0)
						jar_file = FixUtil.cleanPath(root+"packages"+ File.separator +pkg+ File.separator + zip_entry.getName());
					else
						jar_file = FixUtil.cleanPath(root+File.separator+zip_entry.getName());
					if (FixUtil.isFileExist(jar_file))
					{
						weight++;
						//System.out.println(jar_file + " JAR Exists weight="+Integer.toString(weight));
					}
				}
				else if (FixUtil.isClassFile(zip_entry.getName()))
				{
					String root = getRootInstallPath(FixUtil.cleanPath(fd.getTargetDir()));
					String class_file = makeClassFilePath(root, pkg, zip_entry.getName());
					//String class_file = FixUtil.cleanPath(root+"packages"+ File.separator +pkg+ File.separator + zip_entry.getName());
					if (FixUtil.isFileExist(class_file))
					{
						weight++;
						//System.out.println(class_file + " Class Exists weight="+Integer.toString(weight));
					}
				}
				// Check if new file is package and if its installed
				// into IS directory
				else if (FixUtil.isZipEntryPackage(zip_entry.getName()) && fd.getPkgFlag())
				{					
					if (FixUtil.isFileExist(file_name))
					{
						weight++;
						//System.out.println(file_name + " Exists weight="+Integer.toString(weight));
					}
					if (isPackageInstalled(file_name,fd))
					{
						weight++;
						//System.out.println(file_name + " Pkg installed weight="+Integer.toString(weight));
					}
				}
				else if (!zip_entry.isDirectory())
				{
					if (FixUtil.isFileExist(file_name))
					{
						//if (!isFileCanBeIgnored(file_name))
							weight++;
					}
				}
					/*
					// Check if file is DB script and needs ti be installed
					if (isDBScript(zip_entry.getName()) && fd.getDBScript())
					{
						if (isSelectedScript(fd.jdbc,zip_entry.getName()))
						{
							
						}
					}*/
				
			} // END OF WHILE
			
			enum1 = null;			
			zipFileObj.close();
			zipFileObj = null;
			zip_entry = null;
			source_file = null;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			try 
			{						
				zipFileObj.close();
				zipFileObj = null;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}	
		return weight;
	}

	private String makeClassFilePath(String root, String pkg_name, String zip_entry_name)
	{
		String class_file = null;
		boolean pk = false;
		if (pkg_name != null)
		{
			if (pkg_name.length() > 0)
				pk = true;
			else
				pk = false;
		}
		if (root != null)
		{
			if (root.endsWith(File.separator) && !zip_entry_name.startsWith("packages") && pk)				
				class_file = FixUtil.cleanPath(root+"packages"+ File.separator + pkg_name + File.separator + zip_entry_name);
			else if (!root.endsWith(File.separator) && !zip_entry_name.startsWith("packages") && pk)
				class_file = FixUtil.cleanPath(root + File.separator + "packages"+ File.separator + pkg_name + File.separator + zip_entry_name);
			else if (!root.endsWith(File.separator) && zip_entry_name.startsWith("packages"))
				class_file = FixUtil.cleanPath(root + File.separator + zip_entry_name);
			else if (!root.endsWith(File.separator) && !zip_entry_name.startsWith(File.separator))
				class_file = FixUtil.cleanPath(root + File.separator + zip_entry_name);
			else	
				class_file = FixUtil.cleanPath(root + zip_entry_name); // default liekly will not work
		}	
		
		return class_file;
	}
	/**
	 * isFileCanBeIgnored methjod used to filter package files that
	 * are not important and can be ignored during install/uninstall phases
	 * and should not be counted by analyzer.
	 * @param file
	 * @return boolean true/false if file can be ignored false outherwise
	 */
/*	private boolean isFileCanBeIgnored(String file)
	{
		boolean rc = false;
		File ff = new File(file);
		if (ff.getName().equalsIgnoreCase("updates.dsp"))
			rc = true;
		
		return rc;
	}
	*/
	private boolean isManifestIncludeFix(FixData fd)
	{		
		ZipFile zipFileObj = null;
		
		try
		{
			String pkg = FixWmUtil.getTargetPackageName(fd.getFileName());
			File source_file = new File(fd.getFileName());
			zipFileObj = new ZipFile(source_file);
			Enumeration enum1 = zipFileObj.entries();
			ZipEntry zip_entry = null;
			while (enum1.hasMoreElements())
			{
				zip_entry = (ZipEntry)enum1.nextElement();
				
				//String file_name = FixUtil.cleanPath(fd.getTargetDir() + File.separator + zip_entry.getName());
				//String file_name = getSerchFileName(fd, zip_entry.getName(),pkg);
				if (!zip_entry.isDirectory())
				{
					if (zip_entry.getName().equals("manifest.rel"))
					{								
						String root = getRootInstallPath(FixUtil.cleanPath(fd.getTargetDir()));
						if (pkg != null && pkg.length() > 0)
						{
							String man_file = FixUtil.cleanPath(root+"packages"+ File.separator +pkg+ File.separator + "manifest.v3");
							InputStream read_file = zipFileObj.getInputStream(zip_entry);
							// Fix name des not match so it has to be found in zip manifest
							String fix_name = FixWmUtil.getFixNameFromZipManifest(read_file);
							if (FixWmUtil.isManifestContaineFix(man_file,fix_name))
							{
								
								zipFileObj.close();
								return true;
							}
						}
						else
						{
							zipFileObj.close();
							return false;
						}
					}
				}
			} // END OF WHILE
			
			enum1 = null;			
			zipFileObj.close();
			zipFileObj = null;
			zip_entry = null;
			source_file = null;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();			
			try 
			{						
				zipFileObj.close();
				zipFileObj = null;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}	
		return false;
	}
	
	/**
	 * getFixNameFromZipManifest
	 * @param read_file
	 * @return
	 */
	/*** MOVE TO FixWmUtil class
	 
	 
	private String getFixNameFromZipManifest(InputStream read_file)
	{
		String name = "";
		if (read_file == null)
			return name;	
		try 
		{
			XMLCoder xd = new XMLCoder();		
			IData id = xd.decode(read_file);
				
			IDataCursor idc = id.getCursor();
			String ida = IDataUtil.getString(idc,"name");
			if (ida != null)
			{
				name = ida;
			}
			idc.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return name;
		}				
		return name;
	}
	**********/
	
	private String getSerchFileName(FixData fd, String zip_file, String pkg)
	{
		String str = FixUtil.cleanPath(fd.getTargetDir() + File.separator + zip_file);
		if (fd.getPkgFlag() && fd.getCopyFlag() && pkg != null)
		{
			String tmp = "/replicate/inbound";
			String dir = fd.getTargetDir();
			String trg = dir.substring(0,dir.length() - tmp.length());
			str = FixUtil.cleanPath(trg + File.separator+ "packages" + File.separator + pkg + File.separator+ zip_file);
		}
		return str;
	}
	
	private String getRootInstallPath(String trg)
	{
		String root = trg;
		String rep = "replicate"+File.separator+"inbound";
		if (trg.length() > rep.length())
		{
			String tmp = trg.substring(trg.length()-rep.length(),trg.length());
			if (rep.equals(tmp))
				root = trg.substring(0,trg.length()-rep.length());
		}
		return root;
	}
	
	private boolean isPackageInstalled(String file, FixData fxd)
	{
		boolean rc = false;
		
		String pkg = FixWmUtil.getTargetPackageName(file);
		//System.out.println("isPackageInstalled: "+file + " check pkg-"+pkg);
		if (pkg == null || pkg.length()==0)
			return rc;
		String is_pkg_dir = FixUtil.cleanPath(fxd.getTargetDir()+File.separator+"packages"+File.separator+pkg);
		String manifest = is_pkg_dir + File.separator + "manifest.v3";
		//System.out.println("isPackageInstalled: "+manifest + " dir-"+is_pkg_dir+" Fix aname: "+fxd.getName());
		if (FixWmUtil.isManifestContaineFix(manifest,fxd.getName()))
			rc = true;
		
		return rc;
	}

	/***********MOVED to FixWmUtil class ******************************************
	private boolean isManifestContaineFix(String manifest,String fix)
	{
		boolean rc = false;
		if (FixUtil.isFileExist(manifest))
		{
			try 
			{
				XMLCoder xd = new XMLCoder();
				//IDataXMLCoder xd = new IDataXMLCoder();
				InputStream in = (InputStream)new FileInputStream(manifest);
				IData id = xd.decode(in);
				in.close();
				
				IDataCursor idc = id.getCursor();
				IData[] ida = IDataUtil.getIDataArray(idc,"patch_history");
				if (ida != null)
				{
					rc = isPatchExists(ida,fix);
				}
				idc.destroy();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}			
		}
		return rc;
	}

	
	 
	private boolean isPatchExists(IData[] ida,String fix)
	{
		boolean rc = false;
		
		if (ida == null)
			return rc;
		
		for (int i=0; i < ida.length; i++)
		{
			IDataCursor idc = ida[i].getCursor();
			String str = IDataUtil.getString(idc,"name");
			//System.out.println("Manifest loop: "+str+" fix name:"+fix);
			if (str.trim().equals(fix.trim()))
			{
				//System.out.println("FIX FOUND return");
				rc = true;
				return rc;
			}
		}
		
		return rc;
	}
*************************/
}
