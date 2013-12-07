package wm.fix.manager.util;

import java.io.File;
import java.util.zip.ZipFile;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import wm.fix.archive.util.FileUtils;
import wm.fix.manager.data.FixDataJDBC;


public class FixUtil 
{
	/**
	 * isZipFile static method test if file name matches a zip file
	 * @param f  -File object
	 * @return true if file name designated by File object is matching .zip pattern false otehrwise 
	 */
	public static boolean isZipFile(File f)
	{
		if (f == null)
			return false;
		
		return isZipFile(f.getName());
	}
	  /**
	   * isZipFile method to test if given file name matches zip file name.
	   * @param str
	   * @return - boolean true if file is zip type and false if it is not. 
	   */
	  public static boolean isZipFile(String str)
	  {
		  if (str == null)
			  return false;
		  if (str.length() < 3)
			  return false;
		  
		  String tmp = str.substring(str.length() - 3, str.length());
		  
		  if (tmp.toLowerCase().equals("zip"))
			  return true;
		  else
			  return false;
	  }

		/**
		 * isDBScript check file name if it is SQL script included in
		 * fix package
		 * @param str - full script file name path
		 * @return - true if it is file with extension of .sql false for all others
		 */
		public static boolean isDBScript(String str)
		{
			if (str == null)
				return false;
			String sql = str.substring(str.length() - 4, str.length());
			if (sql.equals(".sql"))
				return true;
			else
				return false;
		}
	  
	public static int findMatch(String str, char ch)
	{
		int pos = 0;
		for (int i = 0; i < str.length(); i++)
		{
			if (str.charAt(i)== ch)
				return i;
		}
		return pos;
	}
	  /**
	   * isZipJarFile method to test if given file name matches zip/jar file name.
	   * @param str
	   * @return - boolean true if file is zip/jar type and false if it is not. 
	   */
	  public static boolean isZipJarFile(String str)
	  {
		  if (str == null)
			  return false;
		  if (str.length()< 3)
			  return false;
		  
		  String tmp = str.substring(str.length() - 3, str.length());
		  
		  if (tmp.toLowerCase().equals("zip") || tmp.toLowerCase().equals("jar"))
			  return true;
		  else
			  return false;
	  }
	  
	  /**
	   * isJarFile method check if file has extention of .jar
	   * 
	   * @param str full path file name
	   * @return return true if it is jar file
	   */
	  public static boolean isJarFile(String str)
	  {
			  if (str == null)
				  return false;
			  if (str.length() < 3)
				  return false;
			  
			  String tmp = str.substring(str.length() - 3, str.length());
			  
			  if (tmp.toLowerCase().equals("jar"))
				  return true;
			  else
				  return false;			
	  }

	  public static boolean isClassFile(String str)
	  {
			  if (str == null)
				  return false;
			  if (str.length() < 5)
				  return false;
			  
			  String tmp = str.substring(str.length() - 5, str.length());
			  
			  if (tmp.toLowerCase().equals("class"))
				  return true;
			  else
				  return false;			
	  }
	  
	  /**
	   * isFileExist method check file existance
	   * 
	   * @param file - full path file name
	   * @return true/false if file exists or does not exist
	   */
	  public static boolean isFileExist(String file)
	  {
		  File f = new File(file);
		  if (f == null)
			  return false;
		  if (f.exists() && !f.isDirectory())
			  return true;
		  else
			  return false;
	  }
	  /**
	   * isDirEmpty static method testing if directory is empty
	   * @param dir - path to directory
	   * @return true if directory designated by path is empty false if otherwise.
	   */
	  public static boolean isDirEmpty(String dir)
	  {
		  File f = new File(dir);
		  if (f == null)
			  return false;
		  if (f.isDirectory())
		  {
			  String list[] = f.list();
			  if (list == null)
				  return true;
			  if (list.length > 0)
				  return false;
			  else
				  return true;
		  }
		  else
			  return false;
	  }
	  /**
	   * cleanPath static method replaces all path separators with system dependednt value.
	   * For UNIX it is / for Windows - "\\"
	   * @param path - string with directory/file path
	   * @return String path with system related path separators
	   */
	  public static String cleanPath(String path)
	  {
		  String str = path.replace('/',File.separatorChar);
		  String final_str = str.replace('\\',File.separatorChar);
		  return final_str;
	  }
	  
	  public static boolean isZipEntryPackage(String str)
	  {
			if (str.length() < 18)
				return false;
			String t = cleanPath(str);
			String tmp = t.substring(0, 18);
			String test = "replicate"+File.separator+"inbound"+File.separator;
			if (tmp.equals(test))
			{
				// Check for zip file
				tmp = str.substring(str.length()-4,str.length());
				if (tmp.equals(".zip"))
					return true;
				else
					return false;
			}
			else return false;
	  }
	  
		/**
		 * getTargetPackageName method search inside a zip file
		 * package to find manifest.rel file from what the target
		 * package name is derived.
		 * 
		 * @param zip_file_pkg - full name of package zip file
		 * @return - String representing a target Package name
		 */
/*		public static String getTargetPackageName(String zip_file_pkg)
		{
			String pkg = "";

			InputStream read_file = null;
			ZipFile zipFileObj = null;

			try
			{
				File source_file = new File(zip_file_pkg);
				if (source_file != null && source_file.exists() && !source_file.isDirectory())
				{	
					zipFileObj = new ZipFile(source_file);
					Enumeration enum1 = zipFileObj.entries();
					ZipEntry zip_entry = null;
					while (enum1.hasMoreElements())
					{
						zip_entry = (ZipEntry)enum1.nextElement();
						//System.out.println(zip_entry.getName());
						
						if (zip_entry.getName().toLowerCase().equals("manifest.rel"))
						{
							read_file = zipFileObj.getInputStream(zip_entry);
							pkg = getTargetPkgFromManifest(read_file);							
							if (read_file != null)
								read_file.close();
							
							if (pkg != null)
							{
								zipFileObj.close();								
								return pkg;
							}						
						} // end of IF
					} // end of WHILE
					enum1 = null;				
					zipFileObj.close();
					zipFileObj = null;				
					zip_entry = null;
					source_file = null;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				try
				{				
					read_file.close();				
					zipFileObj.close();
					zipFileObj = null;
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
			
			return pkg;
		}
	*/	
		/**
		 * getTargetPkgFromManifest method uses IO INput STream object to
		 * read manifest file and parse its XML content to find target 
		 * package name.
		 * 
		 * @param in - InputStream of zip fule entry
		 * @return String object package name if found and null otherwize.
		 */
/*		private static String getTargetPkgFromManifest(InputStream in)
		{
			String pkg = null;
			XMLCoder xd = new XMLCoder();
			IData rel_id = null;
			try
			{				
				rel_id = xd.decode(in);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
	    	IDataCursor rel_idc = rel_id.getCursor();
	    	pkg = IDataUtil.getString(rel_idc,"target_pkg_name");
	    	rel_idc.destroy();
			return pkg;
		}
*/	  
		/**
		 * createImage method crfeates Image object from
		 * relative path provided
		 * @param d Display object
		 * @param path - path to image resource
		 * @return Image object built form resource
		 */
		public static Image createImage(Display d, String path)
		{
			try
			{				
				//URL imgURL = FixUtil.class.getResource(path);
				/*URL imgURL = FixManagerView.class.getClassLoader().getResource(path);	
				if (imgURL != null)
				{  		
					return new Image(d,imgURL.getFile());	
				}
				else
				{
					return null;
				}
				*/
				return new Image(d,path);
			}
			catch(Exception e)
			{
				return null;
			}
		}

		/**
		 * isPackage method check the target directory and file name
		 * If both mathch .zip and replicate/inbound it is package
		 * @param file_name - full file name path for fix
		 * @param trg - target dorectory
		 * @return - true is it is matched to package false otherwise
		 */
		public static boolean isPackage(String file_name, String trg)
		{
			boolean rc = false;
			if (FixUtil.isZipFile(file_name) && isReplicateDir(trg))
				rc = true;
			
			return rc;
		}
		
		private static boolean isReplicateDir(String trg)
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
		/**
		 * getZipFileTask method calculates steps of zip file
		 * to install/uninstall
		 * @param zip - path of zip file
		 * @return int value represents steps for Zip file
		 */
		public static int getZipFileTask(String zip)
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
		
		public static void removeFile(String file)
		{
			File f = new File(file);
			if (f.exists())
				if (f.isDirectory())
					FileUtils.deleteDir(f);
				else if (f.isFile())
						f.delete();
		}
	
		public static int findStart(String str)
		{
			for (int i = str.length()-1; i > 0; i--)
			{
				char ch = str.charAt(i);
				if (ch == '_')
					return i+1;
			}
			return 0;
		}
		/**
		 * getScriptName method return a name of DB script derived from full
		 * path file name
		 * @param file - full path file name of DB script
		 * @return - String name of DB script
		 */
		public static String getScriptName(String file)
		{
			String str = file;
			File f = new File(file);
			if (f.exists())
				str = f.getName();
			return str;
		}
	
		public static void copy_dir(String src, String trg)
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
		public static boolean isDirectory(String trg, String str)
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

		/**
		 * isSelectedScript check if found DB script file is of select DB type
		 * match with configuration DB Type
		 * @param jdbc_cnf
		 * @param dbscript
		 * @return
		 */
		public static boolean isSelectedScript(FixDataJDBC jdbc_cnf,String dbscript, String file)
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
					
		private static boolean isScriptInstall(String file)
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
		
}
