package wm.fix.manager.data;

import java.io.FileWriter;
import java.io.StringWriter;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigWriter 
{
	public ConfigWriter()
	{
		
	}
	
	public void generateLangConfXML(String file, LanguageSelector ls)
	{
        try {
            Document doc = new DocumentImpl();
            Element root = doc.createElement("root");     // Create Root Element

            if (ls != null)
            {
            	LanguageCnf lc[] = ls.getLanguageList();
            	if (lc != null)
            	{
            		for (int i=0; i < lc.length; i++)
            		{
            			//FixProfile fp = (FixProfile)tree_profile_node[i].getData();
            			Element item = doc.createElement("language"); 
            			fillLanguageNode(lc[i], item, doc);
            			root.appendChild( item );
            		}
            	}
            }
            doc.appendChild( root );                        // Add Root to Document


            OutputFormat    format  = new OutputFormat( doc );   //Serialize DOM
            StringWriter  stringOut = new StringWriter();        //Writer will be a String
            FileWriter  fw = new FileWriter(file);
            XMLSerializer    serial = new XMLSerializer( stringOut, format );
            serial.asDOMSerializer();                            // As a DOM Serializer

            serial.serialize( doc.getDocumentElement() );
            fw.write(stringOut.toString());
            fw.close();
            //System.out.println( "STRXML = " + stringOut.toString() ); //Spit out DOM as a String
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
		
	}
	
	private void fillLanguageNode(LanguageCnf lc, Element parent, Document doc)
	{
        Element item = doc.createElement("name");       // Create element
        item.appendChild( doc.createTextNode(lc.getName()) );
        parent.appendChild( item );
        
        item = doc.createElement("desc");       // Create element
        item.appendChild( doc.createTextNode(lc.getDesc()) );
        parent.appendChild( item );
        
        item = doc.createElement("active");       // Create element
        if (lc.isActive())
        	item.appendChild( doc.createTextNode("true") );
        else
        	item.appendChild( doc.createTextNode("false") );
        parent.appendChild( item );

        item = doc.createElement("cnffile");       // Create element
        item.appendChild( doc.createTextNode(lc.getLangFile()) );
        parent.appendChild( item );
        		
	}
	
	public void generateConfigXML(String file, Tree tree)
	{
        try {
            Document doc = new DocumentImpl();
            Element root = doc.createElement("root");     // Create Root Element

            TreeItem root_tree_node[] = tree.getItems();
            if (root_tree_node[0] != null)
            {
            	TreeItem tree_profile_node[] = root_tree_node[0].getItems();
            	if (tree_profile_node != null)
            	{
            		for (int i=0; i<tree_profile_node.length; i++)
            		{
            			FixProfile fp = (FixProfile)tree_profile_node[i].getData();
            			Element item = doc.createElement("profile"); 
            			fillProfileNode(fp, item, doc);
            			root.appendChild( item );
            		}
            	}
            }
            doc.appendChild( root );                        // Add Root to Document


            OutputFormat    format  = new OutputFormat( doc );   //Serialize DOM
            StringWriter  stringOut = new StringWriter();        //Writer will be a String
            FileWriter  fw = new FileWriter(file);
            XMLSerializer    serial = new XMLSerializer( stringOut, format );
            serial.asDOMSerializer();                            // As a DOM Serializer

            serial.serialize( doc.getDocumentElement() );
            fw.write(stringOut.toString());
            fw.close();
            //System.out.println( "STRXML = " + stringOut.toString() ); //Spit out DOM as a String
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
		
	}
	
	private void fillProfileNode(FixProfile fp, Element parent, Document doc)
	{
        Element item = doc.createElement("name");       // Create element
        item.appendChild( doc.createTextNode(fp.getName()) );
        parent.appendChild( item );
        
        item = doc.createElement("desc");       // Create element
        item.appendChild( doc.createTextNode(fp.getDesc()) );
        parent.appendChild( item );
        
        item = doc.createElement("sourcedir");       // Create element
        item.appendChild( doc.createTextNode(fp.getSourceDir()) );
        parent.appendChild( item );

        item = doc.createElement("trg");       // Create element
        item.appendChild( doc.createTextNode(fp.getTargetDir()) );
        parent.appendChild( item );
        
        item = doc.createElement("trg_dev");       // Create element
        item.appendChild( doc.createTextNode(fp.getDeveloperDir()) );
        parent.appendChild( item );

        item = doc.createElement("trg_broker");       // Create element
        item.appendChild( doc.createTextNode(fp.getBrokerDir()) );
        parent.appendChild( item );

        item = doc.createElement("trg_is");       // Create element
        item.appendChild( doc.createTextNode(fp.getISDir()) );
        parent.appendChild( item );
        
        item = doc.createElement("trg_common");       // Create element
        item.appendChild( doc.createTextNode(fp.getCommonDir()) );
        parent.appendChild( item );
        
         
        FixData fd[] = fp.getFixList();
        for (int i = 0; i < fd.length; i++)
        {
        	Element fix = doc.createElement("fix");
        	createFixNode(fd[i],fix, doc);
        	parent.appendChild( fix );
        }       
	}
	private void createFixNode(FixData fd, Element fix, Document doc)
	{
        Element item = doc.createElement("name");       // Create element
        item.appendChild( doc.createTextNode(fd.getName()) );
        fix.appendChild( item );
        
        item = doc.createElement("file_name");       // Create element
        item.appendChild( doc.createTextNode(fd.getFileName()) );
        fix.appendChild( item );

        item = doc.createElement("src");       // Create element
        item.appendChild( doc.createTextNode(fd.getSourceDir()) );
        fix.appendChild( item );

        item = doc.createElement("trg");       // Create element
        item.appendChild( doc.createTextNode(fd.getTargetDir()) );
        fix.appendChild( item );

        item = doc.createElement("copy");       // Create element
        item.appendChild( doc.createTextNode(String.valueOf(fd.getCopyFlag())) );
        fix.appendChild( item );
       
        item = doc.createElement("unzip");       // Create element
        item.appendChild( doc.createTextNode(String.valueOf(fd.getUnzipFlag())) );
        fix.appendChild( item );

        item = doc.createElement("pkg_install");       // Create element
        item.appendChild( doc.createTextNode(String.valueOf(fd.getPkgFlag())) );
        fix.appendChild( item );
        if (fd.getPkgFlag())
        {
        	// Add optional package name
            item = doc.createElement("pkg_name");       // Create element
            item.appendChild( doc.createTextNode(fd.getPackageName()) );
            fix.appendChild( item );        	
        }
        
        item = doc.createElement("db_script");       // Create element
        item.appendChild( doc.createTextNode(String.valueOf(fd.getDBScript())) );
        fix.appendChild( item );
        // Create JDBC settings
        if (fd.jdbc != null && fd.getDBScript())
        {
            item = doc.createElement("db_type");       // Create element
            item.appendChild( doc.createTextNode(fd.jdbc.getDBType()) );
            fix.appendChild( item );
            item = doc.createElement("jdbc_driver");       // Create element
            item.appendChild( doc.createTextNode(fd.jdbc.getJDBCDriver()));
            fix.appendChild( item );
            item = doc.createElement("jdbc_url");       // Create element
            item.appendChild( doc.createTextNode(fd.jdbc.getURL()) );
            fix.appendChild( item );
            item = doc.createElement("db_user");       // Create element
            item.appendChild( doc.createTextNode(fd.jdbc.getDBUser()) );
            fix.appendChild( item );
            item = doc.createElement("db_password");       // Create element
            item.appendChild( doc.createTextNode(fd.jdbc.getDBPassword()) );
            fix.appendChild( item );       	
        }
	}

}
