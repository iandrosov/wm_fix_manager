package wm.fix.archive.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
  
  
  /*** Utility methods for manipulating files and directories.
   *
   * @author John Erik Halse
   */

public class FileUtils {
    private static final Logger LOGGER =
    	          Logger.getLogger(FileUtils.class.getName());
    	      /***
    	       * Constructor made private because all methods of this class are static.
    	       */
    	      private FileUtils() {
    	          super();
    	      }
    	      
    	      public static void copyFiles(final File srcDir, Set srcFile,
    	              final File dest)
    	      throws IOException {
    	          for (Iterator i = srcFile.iterator(); i.hasNext();) {
    	              String name = (String)i.next();
    	              copyFiles(new File(srcDir, name), new File(dest, name));
    	          }
    	      }
    	  
    	      /*** Recursively copy all files from one directory to another.
    	       *
    	       * @param src file or directory to copy from.
    	       * @param dest file or directory to copy to.
    	       * @throws IOException
    	       */
    	      public static void copyFiles(File src, File dest)
    	      throws IOException {
    	          copyFiles(src, null, dest, false);
    	      }
    	          
    	      /***
    	       * Recursively copy all files from one directory to another.
    	       * 
    	       * @param src File or directory to copy from.
    	       * @param filter Filename filter to apply to src. May be null if no
    	       * filtering wanted.
    	       * @param dest File or directory to copy to.
    	       * @param inSortedOrder Copy in order of natural sort.
    	       * @throws IOException
    	       */
    	      public static void copyFiles(final File src, final FilenameFilter filter,
    	              final File dest, final boolean inSortedOrder)
    	      throws IOException {
    	          // TODO: handle failures at any step
    	          if (!src.exists()) {
    	              return;
    	          }
    	  
    	          if (src.isDirectory()) {
    	              // Create destination directory
    	              dest.mkdirs();
    	             // Go through the contents of the directory
    	             String list[] = (filter == null)? src.list(): src.list(filter);
    	             if (inSortedOrder) {
    	                 Arrays.sort(list);
    	             }
    	             for (int i = 0; i < list.length; i++) {
    	                 copyFiles(new File(src, list[i]), filter,
    	                     new File(dest, list[i]), inSortedOrder);
    	             }
    	         } else {
    	             copyFile(src, dest);
    	         }
    	     }
    	 
    	     /***
    	      * Copy the src file to the destination.
    	      * 
    	      * @param src
    	      * @param dest
    	      * @return True if the extent was greater than actual bytes copied.
    	      * @throws FileNotFoundException
    	      * @throws IOException
    	      */
    	     public static boolean copyFile(File src, File dest)
    	             throws FileNotFoundException, IOException {
    	         return copyFile(src, dest, -1);
    	     }
    	 
    	 	/***
    	      * Copy up to extent bytes of the source file to the destination
    	      *
    	      * @param src
    	      * @param dest
    	      * @param extent Maximum number of bytes to copy
    	 	 * @return True if the extent was greater than actual bytes copied.
    	      * @throws FileNotFoundException
    	      * @throws IOException
    	      */
    	     public static boolean copyFile(File src, File dest, long extent)
    	             throws FileNotFoundException, IOException {
    	         boolean result = false;
    	         if (dest.exists()) {
    	             dest.delete();
    	         }
    	         FileInputStream fis = null;
    	         FileOutputStream fos = null;
    	         FileChannel fcin = null;
    	         FileChannel fcout = null;
    	         try {
    	             // Get channels
    	             fis = new FileInputStream(src);
    	             fos = new FileOutputStream(dest);
    	             fcin = fis.getChannel();
    	             fcout = fos.getChannel();
    	             if (extent < 0) {
    	                 extent = fcin.size();
    	             }
    	 
    	             // Do the file copy
    	             long trans = fcin.transferTo(0, extent, fcout);
    	             if (trans < extent) {
    	                 result = false;
    	             }
    	             result = true; 
    	         } catch (IOException e) {
    	             // Add more info to the exception. Preserve old stacktrace.
    	             // We get 'Invalid argument' on some file copies. See
    	             // http://intellij.net/forums/thread.jsp?forum=13&thread=63027&message=853123
    	             // for related issue.
    	             String message = "Copying " + src.getAbsolutePath() + " to " +
    	                 dest.getAbsolutePath() + " with extent " + extent +
    	                 " got IOE: " + e.getMessage();
    	             if (e.getMessage().equals("Invalid argument")) {
    	                 LOGGER.severe("Failed copy, trying workaround: " + message);
    	                 workaroundCopyFile(src, dest);
    	             } else {
    	                 IOException newE = new IOException(message);
    	                 newE.setStackTrace(e.getStackTrace());
    	                 throw newE;
    	             }
    	         } finally {
    	             // finish up
    	             if (fcin != null) {
    	                 fcin.close();
    	             }
    	             if (fcout != null) {
    	                 fcout.close();
    	             }
    	             if (fis != null) {
    	                 fis.close();
    	             }
    	             if (fos != null) {
    	                 fos.close();
    	             }
    	         }
    	         return result;
    	     }
    	     
    	     protected static void workaroundCopyFile(final File src,
    	             final File dest)
    	     throws IOException {
    	         FileInputStream from = null;
    	         FileOutputStream to = null;
    	         try {
    	             from = new FileInputStream(src);
    	             to = new FileOutputStream(dest);
    	             byte[] buffer = new byte[4096];
    	             int bytesRead;
    	             while ((bytesRead = from.read(buffer)) != -1) {
    	                 to.write(buffer, 0, bytesRead);
    	             }
    	         } finally {
    	             if (from != null) {
    	                 try {
    	                     from.close();
    	                 } catch (IOException e) {
    	                     e.printStackTrace();
    	                 }
    	             }
    	             if (to != null) {
    	                 try {
    	                     to.close();
    	                 } catch (IOException e) {
    	                    e.printStackTrace();
    	                }
    	             }
    	         }
    	     }
    	 
    	 	/*** Deletes all files and subdirectories under dir.
    	      * @param dir
    	      * @return true if all deletions were successful. If a deletion fails, the
    	      *          method stops attempting to delete and returns false.
    	      */
    	     public static boolean deleteDir(File dir) {
    	         if (dir.isDirectory()) {
    	             String[] children = dir.list();
    	             for (int i=0; i<children.length; i++) {
    	                 boolean success = deleteDir(new File(dir, children[i]));
    	                 if (!success) {
    	                     return false;
    	                 }
    	             }
    	         }
    	         // The directory is now empty so delete it
    	         return dir.delete();
    	     }
    	
    	 
    	 
    	     /***
    	      * Utility method to read an entire file as a String.
    	      *
    	      * @param file
    	      * @return File as String.
    	      * @throws IOException
    	      */
    	     public static String readFileAsString(File file) throws IOException {
    	         StringBuffer sb = new StringBuffer((int) file.length());
    	         String line;
    	         BufferedReader br = new BufferedReader(new InputStreamReader(
    	         		new FileInputStream(file)));
    	         try {
    	         	    line = br.readLine();
    	         	    while (line != null) {
    	         	    	    sb.append(line);
    	                         sb.append("\n");
    	         	    	    line = br.readLine();
    	         	    }
    	         } finally {
    	         	    br.close();
    	         }
    	         return sb.toString();
    	     }
    	 
    	     /***
    	      * Get a list of all files in directory that have passed prefix.
    	      *
    	      * @param dir Dir to look in.
    	      * @param prefix Basename of files to look for. Compare is case insensitive.
    	      *
    	      * @return List of files in dir that start w/ passed basename.
    	      */
    	     public static File [] getFilesWithPrefix(File dir, final String prefix) {
    	         FileFilter prefixFilter = new FileFilter() {
    	                 public boolean accept(File pathname)
    	                 {
    	                     return pathname.getName().toLowerCase().
    	                         startsWith(prefix.toLowerCase());
    	                 }
    	             };
    	         return dir.listFiles(prefixFilter);
    	     }
    	 
    	     /*** Get a @link java.io.FileFilter that filters files based on a regular
    	      * expression.
    	      *
    	      * @param regexp the regular expression the files must match.
    	      * @return the newly created filter.
    	     */
    	     public static FileFilter getRegexpFileFilter(String regexp) {
    	         // Inner class defining the RegexpFileFilter
    	         class RegexpFileFilter implements FileFilter {
    	             Pattern pattern;
    	 
    	             protected RegexpFileFilter(String re) {
    	                 pattern = Pattern.compile(re);
    	             }
    	 
    	             public boolean accept(File pathname) {
    	                 return pattern.matcher(pathname.getName()).matches();
    	             }
    	         }
    	 
    	         return new RegexpFileFilter(regexp);
    	     }
}