package wm.fix.manager.install;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.ui.FixInstallProgress;

public class InstallerTask 
{
     private int m_lengthOfTask;   
     private boolean done = false;
     private boolean canceled = false;   
     //private boolean m_install = true;
     private FixData m_fd = null;
     private FixProfile m_fp = null;
     private FixInstaller m_fi = null;
     
     public InstallerTask(FixProfile fp,FixInstallProgress fip) 
     {
         //Compute length of task...
         //Get task size from FixData object
         //based on requested fix install options.
     	 m_fi = new FixInstaller(fip);
         m_lengthOfTask = m_fi.getFixInstallTaskSize(fp); 
         m_fi.setLengthOfTask(m_lengthOfTask);
         m_fp = fp;        
     }
     
     public InstallerTask(FixData fd,FixInstallProgress fip) 
     {
         //Compute length of task...
         //Get task size from FixData object
         //based on requested fix install options.
     	 m_fi = new FixInstaller(fip);
         m_lengthOfTask = m_fi.getFixInstallTaskSize(fd);   
         m_fi.setLengthOfTask(m_lengthOfTask);
         m_fd = fd;        
     }
     
     /**
      * Called from FixInstallProgress to start the task.
      */
     //public void go(boolean install)
     public void go()
     {
    	 //m_install = install;        
         done = false;
         canceled = false;
                 
         if (m_fd != null)
        	 new FixInstallerTask(m_fd);
         else if (m_fp != null)
             new FixInstallerTask(m_fp);        
     }

     /**
      * Called from ProgressBarDemo to find out how much work needs
      * to be done.
      */
     public int getLengthOfTask() 
     {
         //return m_lengthOfTask;
         return m_fi.getLengthOfTask();
     }

     /**
      * Called from ProgressBarDemo to find out how much has been done.
      */
     public int getCurrent() 
     {
         //return current;
     	return m_fi.getCurrent();
     }

     public void stop() 
     {
         canceled = true;
         //statMessage = null;
         m_fi.setMessage(null);
     }

     /**
      * Called from ProgressBarDemo to find out if the task has completed.
      */
     public boolean isDone() 
     {
         return done;
     }

     public boolean isCanceled()
     {
     	return canceled;
     }
     
     /**
      * Returns the most recent status message, or null
      * if there is no current status message.
      */
     public String getMessage() 
     {
     	return m_fi.getMessage();
     }

     /**
      * The actual installer running task.  
      * This runs in a SwingWorker thread.
      * 
      * @author xiandros
      *
      */
     public class FixInstallerTask 
     {    	
     	FixInstallerTask(FixData fd) 
     	{
     		//Perform installation task for fix.
     		if (!canceled && !done)
     		{
     			if (m_fi != null)
     			{
     				//if (m_install)
     					m_fi.installFix(fd,false);
     				//else
     					//m_fi.uninstallFix(fd,false);
     			}
     			done = true;
     			m_fi.setCurrent(m_fi.getLengthOfTask());
     		}	
     	}
     	
     	FixInstallerTask(FixProfile fp) 
     	{
     		try
     		{
     			//Perform installation task for fix.  
     			if (!canceled && !done)
     			{
     				if (m_fi != null)
     				{
     					//if (m_install)
     						m_fi.installFixProfile(fp);
     				}
         			done = true;
         			m_fi.setCurrent(m_fi.getLengthOfTask());
     				
     			}    			
     		}
     		catch(Exception e)
     		{
     			
     		}
         }   	  	
     	
     }
}
