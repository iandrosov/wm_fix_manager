package wm.fix.manager.opt.install;

import wm.fix.manager.data.FixData;
import wm.fix.manager.data.FixProfile;
import wm.fix.manager.opt.ui.FixUninstallProgress;

public class UninstallerTask 
{
    private int m_lengthOfTask;   
    private boolean done = false;
    private boolean canceled = false;   
    //private boolean m_install = true;
    private FixData m_fd = null;
    private FixProfile m_fp = null;
    private FixUninstaller m_fu = null;
    

    public UninstallerTask(FixProfile fp,FixUninstallProgress fup) 
    {
        //Compute length of task...
        //Get task size from FixData object
        //based on requested fix install options.
    	m_fu = new FixUninstaller(fup);
        m_lengthOfTask = m_fu.getFixUninstallTaskSize(fp); 
        m_fu.setLengthOfTask(m_lengthOfTask);
        m_fp = fp;        
    }
    
    public UninstallerTask(FixData fd,FixUninstallProgress fup) 
    {
        //Compute length of task...
        //Get task size from FixData object
        //based on requested fix install options.
    	m_fu = new FixUninstaller(fup);
        m_lengthOfTask = m_fu.getFixUninstallTaskSize(fd);   
        m_fu.setLengthOfTask(m_lengthOfTask);
        m_fd = fd;        
    }
    
    /**
     * Called from FixUninstallProgress to start the task.
     */
    public void go() 
    {
   	 	//m_install = install;        
        done = false;
        canceled = false;
                
        if (m_fd != null)
       	 	new FixUninstallerTask(m_fd);
        else if (m_fp != null)
            new FixUninstallerTask(m_fp);        
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() 
    {
        //return m_lengthOfTask;
        return m_fu.getLengthOfTask();
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() 
    {
        //return current;
    	return m_fu.getCurrent();
    }

    public void stop() 
    {
        canceled = true;
        //statMessage = null;
        m_fu.setMessage(null);
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
    	return m_fu.getMessage();
    }

    /**
     * The actual installer running task.  
     * This runs in a SwingWorker thread.
     * 
     * @author xiandros
     *
     */
    public class FixUninstallerTask 
    {    	
    	FixUninstallerTask(FixData fd) 
    	{
    		try
    		{
    			//Perform uninstallation task for fix.
    			if (!canceled && !done)
    			{
    				if (m_fu != null)
    				{
   						m_fu.uninstallFix(fd,false);
    				}
    				done = true;
    				m_fu.setCurrent(m_fu.getLengthOfTask());
    			}
    		}
    		catch (Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	FixUninstallerTask(FixProfile fp) 
    	{
    		try
    		{
    			//Perform uninstallation task for fix.  
    			if (!canceled && !done)
    			{
    				if (m_fu != null)
    				{    				
    					m_fu.uninstallFixProfile(fp);
    				}
        			done = true;
        			m_fu.setCurrent(m_fu.getLengthOfTask());
    				
    			}    			
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
        }   	  	
    	
    } // END of FixUninstallerTask class
}
