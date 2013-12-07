package wm.fix.manager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.util.Values;
import com.wm.util.coder.XMLCoder;

public class FixWmUtil 
{
	/**
	 * getTargetPackageName method search inside a zip file
	 * package to find manifest.rel file from what the target
	 * package name is derived.
	 * 
	 * @param zip_file_pkg - full name of package zip file
	 * @return - String representing a target Package name
	 */
	public static String getTargetPackageName(String zip_file_pkg)
	{
		String pkg = ""; //$NON-NLS-1$

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
					
					if (zip_entry.getName().toLowerCase().equals("manifest.rel")) //$NON-NLS-1$
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
	
	/**
	 * getTargetPkgFromManifest method uses IO INput STream object to
	 * read manifest file and parse its XML content to find target 
	 * package name.
	 * 
	 * @param in - InputStream of zip fule entry
	 * @return String object package name if found and null otherwize.
	 */
	private static String getTargetPkgFromManifest(InputStream in)
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
    	pkg = IDataUtil.getString(rel_idc,"target_pkg_name"); //$NON-NLS-1$
    	rel_idc.destroy();
		return pkg;
	}
	/**
	 * update_manifest method updates package manifest file with 
	 * new fix release information.
	 * 
	 * @param manifest_file - full path to package manifest file
	 * @param rel - full path to package release manifest to be added
	 */
	public static void update_manifest(String manifest_file, String rel)
	{
		try 
		{
			XMLCoder xd = new XMLCoder();
			//IDataXMLCoder xd = new IDataXMLCoder();
			InputStream in = (InputStream)new FileInputStream(manifest_file);
			IData id = xd.decode(in);
			in.close();
			IData man = add_patch(id, rel);
    	
			OutputStream out = (OutputStream)new FileOutputStream(manifest_file);
			xd.encode(out,(Values)man);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * isManifestContaineFix method
	 * @param manifest
	 * @param fix
	 * @return boolean - true if manifest has fix name false otherwise
	 */
	public static boolean isManifestContaineFix(String manifest,String fix)
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
	
	/**
	 * getFixNameFromZipManifest
	 * @param read_file
	 * @return
	 */
	public static String getFixNameFromZipManifest(InputStream read_file)
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

	private static IData add_patch(IData id, String rel_file)
	{
		XMLCoder xd = new XMLCoder();
		IData rel_id = null;
		try
		{
			InputStream in = (InputStream)new FileInputStream(rel_file);
			rel_id = xd.decode(in);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		IData new_patch = IDataFactory.create();
		IDataCursor idc_patch = null;
    	IDataCursor rel_idc = rel_id.getCursor();
    	String patch_key = IDataUtil.getString(rel_idc,"name"); //$NON-NLS-1$
    	if (patch_key != null)
    	{
			
			idc_patch = new_patch.getCursor();
		
			IDataUtil.put(idc_patch,"name",IDataUtil.getString(rel_idc,"name")); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"version",IDataUtil.getString(rel_idc,"version")); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"build",IDataUtil.getString(rel_idc,"build")); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"description",IDataUtil.getString(rel_idc,"description")); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"time",IDataUtil.getString(rel_idc,"time"));//"2005-08-31 23:03:37 GMT-05:00"); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"jvm_version",IDataUtil.getString(rel_idc,"jvm_version"));//"1.3.1"); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"publisher",IDataUtil.getString(rel_idc,"publisher"));//"webMethods, Inc"); //$NON-NLS-1$ //$NON-NLS-2$
			IDataUtil.put(idc_patch,"patch_nums",""); //$NON-NLS-1$ //$NON-NLS-2$
			
			
    	}
    	
		
		//name = IS_6-1_SP1_Fix46
		//version = 6.1
		//build = 
		//description = 
		//time = 2005-08-31 23:03:37 GMT-05:00
		//jvm_version = 1.3.1
		//publisher = webMethods, Inc.
		//patch_nums = 
		
		IDataCursor idc = id.getCursor();
		IData[] ida = IDataUtil.getIDataArray(idc,"patch_history"); //$NON-NLS-1$
		if (ida != null)
		{
			IData[] add_array = new IData[ida.length + 1];
			// check if patch exists
			if (check_patch(ida,patch_key))
				return id;
		
			int i = 0;
			for (i = 0; i < ida.length; i++)
			{
				add_array[i] = ida[i];
			}	

			add_array[i] = new_patch;
			idc_patch.destroy();		
			IDataUtil.remove(idc,"patch_history"); //$NON-NLS-1$
			IDataUtil.put(idc,"patch_history",add_array); //$NON-NLS-1$
			idc.destroy();
		}
		else // no patches found put new one
		{
			IData[] add_array = new IData[1];		
			add_array[0] = new_patch;
			idc_patch.destroy();
			IDataUtil.put(idc,"patch_history",add_array); //$NON-NLS-1$
			idc.destroy();

		}
		return id;
	}

	private static boolean check_patch(IData[] ida, String patch_key)
	{
		
		if (ida == null)
			return false;
		
		for (int i=0; i < ida.length; i++)
		{
			IDataCursor idc = ida[i].getCursor();
			String str = IDataUtil.getString(idc,"name"); //$NON-NLS-1$
			if (str.equals(patch_key))
				return true;
		}
		return false;
		
	}

	
	private static boolean isPatchExists(IData[] ida, String fix)
	{
		boolean rc = false;
		
		
		if (ida == null)
			return rc;
		
		for (int i=0; i < ida.length; i++)
		{
			IDataCursor idc = ida[i].getCursor();
			String str = IDataUtil.getString(idc,"name");
			
			if (str.trim().equals(fix.trim()))
			{
				rc = true;
				return rc;
			}
		}
		
		return rc;
		
	}

}
