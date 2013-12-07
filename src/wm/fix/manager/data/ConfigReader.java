package wm.fix.manager.data;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigReader 
{
	   protected String mNodeName;
	   protected String mNodeValue;
	   protected Vector mNodes = new Vector();
	   protected Vector mAttributes = new Vector();
	   
	   public LanguageSelector loadLanguageConfig(String xml_doc)throws Exception, SAXException, IOException
	   {
		   LanguageSelector lang_list = null;
	       try
	       {
	    	   URL url = createURL(xml_doc);
	    	   lang_list = getLanguageList(url.toString());
	    	   
	       }
	       catch (Exception e)
	       {
	          e.printStackTrace();
	       }
		   return lang_list;
	   }

	   public FixProfileData loadXml(String xml_doc)throws Exception, SAXException, IOException
	   {
		   FixProfileData fp = null;
	       try
	       {
	    	   URL url = createURL(xml_doc);
	    	   fp = getFixProfile(url.toString());
	    	   
	       }
	       catch (Exception e)
	       {
	          e.printStackTrace();
	       }
		   return fp;
	   }
/*	   
	   public void loadXMLAnalyze(String xml_doc, DynamicTreeDemo tree_v)throws Exception, SAXException, IOException
	   {
	       try
	       {
	    	   URL url = createURL(xml_doc);
	    	   processConfigAnalyze(url.toString(),tree_v);
	       }
	       catch (Exception e)
	       {
	          e.printStackTrace();
	       }
	   }
	*/   
	   private LanguageSelector getLanguageList(String url) throws Exception
	   {
		   LanguageSelector lang_list = null;
		   DOMParser parser = new DOMParser();
		   parser.parse(url);
	       Document doc = parser.getDocument();
	       
	       if ( doc == null )
	    	   return null;

	       lang_list = new LanguageSelector();
	       // Process Current Node
	       Node node = null;
	       //String foo;
	       NodeList node_list = doc.getChildNodes();
	       for (int i = 0; i <  node_list.getLength(); i++)
	       {
	            node = node_list.item(i);
	            int typ = node.getNodeType();
	            if (typ == Node.ELEMENT_NODE)
	            {
	                node_list = node.getChildNodes();
	                for (int j = 0; j <  node_list.getLength(); j++)
	                {
	                	 Node profile_node = node_list.item(j);
	                     if (profile_node.getNodeType() == Node.ELEMENT_NODE)
	                     {
	                	     //System.out.println("Elm1 = " +profile_node.getNodeName() + " VAL - " + profile_node.getNodeValue());          	   
	                	     if (profile_node.getNodeName().equals("language"))
	                	     {
	                		     LanguageCnf lc = new LanguageCnf();
	                		     NodeList pro_list = profile_node.getChildNodes();
	                		     for (int k = 0; k < pro_list.getLength(); k++)
	                		     {     			   
	                			      Node chl = pro_list.item(k);
	                			      if (chl.getNodeType() == Node.ELEMENT_NODE)
	                			      {
	                				      if (chl.getNodeValue() == null)
	                				      {
	                				    	  //System.out.println("Elm2 = " +chl.getNodeName() + " VAL - " + chl.getNodeValue());
	                				    	  if (chl.getNodeName().equals("name"))
	                				    	  {	 
	                				    		  String str = getChiledNodeName(chl);                						   
	                				    		  lc.setName(str);
	                				    	  }
	                				    	  if (chl.getNodeName().equals("desc"))
	                				    		  lc.setDesc(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("cnffile"))
	                				    		  lc.setLangFile(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("active"))
	                				    	  {
	                							   if (Boolean.valueOf(getChiledNodeName(chl))==Boolean.TRUE)
	                								   lc.setActive(true);
	                							   else
	                								   lc.setActive(false);				   
	                				    	  }	                				 	  
	                				      }
	                			      }
	                		     } //end for k
	                		     lang_list.addLanguage(lc);
	                	     } // End of Profile IF	                	  	                	   
	                     } // End of ELEMENT IF
	                } // End of FOR j
	            } // End of NODE IF
	       } // End of FOR i
	       	       
		   return lang_list;
	   }
	   
	   public FixProfileData getFixProfile(String url) throws Exception
	   {
		   DOMParser parser = new DOMParser();
		   parser.parse(url);
	       Document doc = parser.getDocument();

	       if ( doc == null )
	    	   return null;
	       FixProfileData fpd = new FixProfileData();
	       // Process Current Node
	       Node node = null;
	       //String foo;
	       NodeList node_list = doc.getChildNodes();
	       for (int i = 0; i <  node_list.getLength(); i++)
	       {
	            node = node_list.item(i);
	            int typ = node.getNodeType();
	            if (typ == Node.ELEMENT_NODE)
	            {
	                node_list = node.getChildNodes();
	                for (int j = 0; j <  node_list.getLength(); j++)
	                {
	                	 Node profile_node = node_list.item(j);
	                     if (profile_node.getNodeType() == Node.ELEMENT_NODE)
	                     {
	                	     //System.out.println("Elm1 = " +profile_node.getNodeName() + " VAL - " + profile_node.getNodeValue());          	   
	                	     if (profile_node.getNodeName().equals("profile"))
	                	     {
	                		     FixProfile fp = new FixProfile();
	                		     NodeList pro_list = profile_node.getChildNodes();
	                		     for (int k = 0; k < pro_list.getLength(); k++)
	                		     {     			   
	                			      Node chl = pro_list.item(k);
	                			      if (chl.getNodeType() == Node.ELEMENT_NODE)
	                			      {
	                				      if (chl.getNodeValue() == null)
	                				      {
	                				    	  //System.out.println("Elm2 = " +chl.getNodeName() + " VAL - " + chl.getNodeValue());
	                				    	  if (chl.getNodeName().equals("name"))
	                				    	  {	 
	                				    		  String str = getChiledNodeName(chl);                						   
	                				    		  fp.setName(str);
	                				    	  }
	                				    	  if (chl.getNodeName().equals("fix"))
	                				    	  {
	                				    		  //System.out.println("Fix name = "+getFixName(chl));	  			   
	                				    		  FixData fxd = buildFixData(chl);
	                				    		  //fxd.print();
	                				    		  fp.addFixData(fxd);	                						   					   
	                				    	  }
	                				    	  if (chl.getNodeName().equals("desc"))
	                				    		  fp.setDesc(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("sourcedir"))
	                				    		  fp.setSourceDir(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("trg"))
	                				    		  fp.setTargeDir(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("trg_dev"))
	                				    		  fp.setDeveloperDir(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("trg_is"))
	                				    		  fp.setISDir(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("trg_broker"))
	                				    		  fp.setBrokerDir(getChiledNodeName(chl));
	                				    	  if (chl.getNodeName().equals("trg_common"))
	                				    		  fp.setCommonDir(getChiledNodeName(chl));
	                				      }
	                			      }
	                		     } //end for k
	                		     fpd.addFixProfile(fp);
	                	     } // End of Profile IF	                	  	                	   
	                     } // End of ELEMENT IF
	                } // End of FOR j
	            } // End of NODE IF
	       } // End of FOR i
		   return fpd;
	   } // End of method getFixProfile
	   
/*	   
	   public void processConfigAnalyze(String url, DynamicTreeDemo tree_v) throws Exception
	   {
	       DOMParser parser = new DOMParser();
	       parser.parse(url);
	       Document doc = parser.getDocument();

	          if ( doc == null )
	          {
	             return;
	          }
	          // Set top level node
	          //Node rootNode = doc.getFirstChild();

	          // Process Current Node
	          Node node = null;
	          DefaultMutableTreeNode tree_br = null;
	          DefaultMutableTreeNode tree_br_fix = null;
	          //String foo;
	          NodeList node_list = doc.getChildNodes();
	          for (int i = 0; i <  node_list.getLength(); i++)
	          {
	             node = node_list.item(i);
	             int typ = node.getNodeType();
	             if (typ == Node.ELEMENT_NODE)
	             {
	                 node_list = node.getChildNodes();
	                 for (int j = 0; j <  node_list.getLength(); j++)
	                 {
	                   Node profile_node = node_list.item(j);
	                   if (profile_node.getNodeType() == Node.ELEMENT_NODE)
	                   {
	                	   //System.out.println("Elm1 = " +profile_node.getNodeName() + " VAL - " + profile_node.getNodeValue());          	   
	                	   if (profile_node.getNodeName().equals("profile"))
	                	   {
	                		   FixProfile fp = new FixProfile();
	                		   NodeList pro_list = profile_node.getChildNodes();
	                		   for (int k = 0; k < pro_list.getLength(); k++)
	                		   {     			   
	                			   Node chl = pro_list.item(k);
	                			   if (chl.getNodeType() == Node.ELEMENT_NODE)
	                				   if (chl.getNodeValue() == null)
	                				   {
	                					   //System.out.println("Elm2 = " +chl.getNodeName() + " VAL - " + chl.getNodeValue());
	                					   if (chl.getNodeName().equals("name"))
	                					   {	 
	                						   String str = getChiledNodeName(chl);
	                						   tree_br = tree_v.addFolder(str);
	                						   //tree_br = tree_v.treePanel.addObject(str);
	                						   fp.setName(str);
	                						   //System.out.println("Add Folder = " +str);
	                					   }
	                					   if (chl.getNodeName().equals("fix"))
	                					   {
	                						   //System.out.println("Fix name = "+getFixName(chl));
	                						   tree_br_fix = tree_v.addFix(tree_br,getFixName(chl));
	                						   //System.out.println("Add Fix = " +getFixName(chl));
	                						   FixData fxd = buildFixData(chl);
	                						   
	                						   
	                						   //////////////////////////////////////////////
	                						   FixAnalyzer fa = new FixAnalyzer();
	                						   int status = fa.analyzeFix(fxd);
	                						   fxd.setStatus(status);
	                						   if (status == FixDataConstants.FIX_INSTALL_OK)
	                							   fxd.setInstalled(true);
	                						   else
	                							   fxd.setInstalled(false);
	                						   /////////////////////////////////////////////
	                						   
	                						   
	                						   fp.addFixData(fxd);
	                						   tree_br_fix.setUserObject(fxd);					   
	                					   }
	                					   if (chl.getNodeName().equals("desc"))
	                						   fp.setDesc(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("sourcedir"))
	                						   fp.setSourceDir(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("trg"))
	                						   fp.setTargeDir(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("trg_dev"))
	                						   fp.setDeveloperDir(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("trg_is"))
	                						   fp.setISDir(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("trg_broker"))
	                						   fp.setBrokerDir(getChiledNodeName(chl));
	                					   if (chl.getNodeName().equals("trg_common"))
	                						   fp.setCommonDir(getChiledNodeName(chl));
	                				   }		                  
	                		   } //end for
	                		   // Add Profile object to tree
	                		   tree_br.setUserObject(fp);
	                	   }
	                	   
	                	   
	                	}
	                 }
	               }
	           }

	   }
	   */
	   private FixData buildFixData(Node nd)
	   {
		   FixData fxd = new FixData();
		   NodeList ch_list = nd.getChildNodes();
		   for (int i = 0; i < ch_list.getLength(); i++)
		   {
			   Node chl = ch_list.item(i);
			   if (chl.getNodeType() == Node.ELEMENT_NODE)
			   {
				   if (chl.getNodeValue() == null)
				   {
					   if (chl.getNodeName().equals("name"))	
						   fxd.setName(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("file_name"))
						   fxd.setFileName(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("src"))
						   fxd.setSourceDir(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("trg"))
						   fxd.setTargeDir(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("pkg_name"))
						   fxd.setPackageName(getChiledNodeName(chl));
					   
					   if (chl.getNodeName().equals("copy"))
					   {
						   if (Boolean.valueOf(getChiledNodeName(chl))==Boolean.TRUE)
							   fxd.setCopyFlag(true);
						   else
							   fxd.setCopyFlag(false);				   
					   }
					   if (chl.getNodeName().equals("unzip"))
					   {						   
						   if (Boolean.valueOf(getChiledNodeName(chl))==Boolean.TRUE)
							   fxd.setUnzipFlag(true);
						   else
							   fxd.setUnzipFlag(false); 
						   
					   }
					   if (chl.getNodeName().equals("pkg_install"))
					   {
						   if (Boolean.valueOf(getChiledNodeName(chl))==Boolean.TRUE)
							   fxd.setPkgFlag(true);
						   else
							   fxd.setPkgFlag(false);
					   }
					   if (chl.getNodeName().equals("db_script"))
					   {
						   if (Boolean.valueOf(getChiledNodeName(chl))==Boolean.TRUE)
							   fxd.setDBScript(true);
						   else
							   fxd.setDBScript(false);
					   }
					   if (chl.getNodeName().equals("db_type"))
					   	   fxd.jdbc.setDBType(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("jdbc_driver"))
					   	   fxd.jdbc.setJDBCDriver(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("jdbc_url"))
					   	   fxd.jdbc.setURL(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("db_user"))
					   	   fxd.jdbc.setDBUser(getChiledNodeName(chl));
					   if (chl.getNodeName().equals("db_password"))
					   	   fxd.jdbc.setDBPassword(getChiledNodeName(chl));						  					   
				   }
			   }
		   }
		   
		   return fxd;
	   }
	   /*
	   private String getFixName(Node nd)
	   {
		   NodeList ch_list = nd.getChildNodes();
		   for (int i = 0; i < ch_list.getLength(); i++)
		   {
			   Node chl = ch_list.item(i);
			   if (chl.getNodeType() == Node.ELEMENT_NODE)
			   {
				   if (chl.getNodeValue() == null)
				   {
					   if (chl.getNodeName().equals("name"))	
						   return getChiledNodeName(chl);
				   }
			   }
		   }
		   return null;
	   }
	   */
	   private String getChiledNodeName(Node nd)
	   {
		   String str = null;
		   Node ll = nd.getFirstChild();
		   if (ll != null)
			   if (ll.getNodeValue() != null)
				   if (ll.getNodeValue().length() > 0)
				   {
					   //System.out.println("Elm3 = " +ll.getNodeName() + " VAL - " + ll.getNodeValue());
					   str = ll.getNodeValue();
				   }
		   return str;
	   }
	   
	   private URL createURL(String fileName)
	   {
	      URL url = null;
	      try
	      {
	         url = new URL(fileName);
	      }
	      catch (MalformedURLException ex)
	      {
	         File f = new File(fileName);
	         try
	         {
	            String path = f.getAbsolutePath();
	            String fs = System.getProperty("file.separator");
	            if (fs.length() == 1)
	            {
	               char sep = fs.charAt(0);
	               if (sep != '/')
	                  path = path.replace(sep, '/');
	               if (path.charAt(0) != '/')
	                  path = '/' + path;
	            }
	            path = "file://" + path;
	            url = new URL(path);
	         }
	         catch (MalformedURLException e)
	         {
	            System.out.println("Cannot create url for: " + fileName);
	            System.exit(0);
	         }
	      }
	      return url;
	   }

}
