package wm.fix.manager.opt.install;

import wm.fix.manager.data.FixProfile;
import wm.fix.manager.opt.ui.BackupProgress;

public class BackupTask 
{
    private int m_lengthOfTask;   
    private boolean done = false;
    private boolean canceled = false;   
    private FixProfile m_fp = null;
    
    private ProfileBackupProcess m_pbp = null;
    
    public BackupTask(FixProfile fp,BackupProgress bp) 
    {
        //Compute length of task...
        //Get task size from FixProfile object
        //based on configured webMethods IS environment.
    	m_pbp = new ProfileBackupProcess(bp);
        m_lengthOfTask = m_pbp.getBackupTaskSize(fp); 
        m_pbp.setLengthOfTask(m_lengthOfTask);
        m_fp = fp;        
    }
    /**
     * Called from FixInstallProgress to start the task.
     */
    public void go() 
    {      
        done = false;
        canceled = false;
                
        if (m_fp != null)
            new ProfileBackupTask(m_fp);        
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() 
    {
        //return m_lengthOfTask;
        return m_pbp.getLengthOfTask();
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() 
    {
        //return current;
    	return m_pbp.getCurrent();
    }

    public void stop() 
    {
        canceled = true;
        //statMessage = null;
        m_pbp.setMessage(null);
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
    	return m_pbp.getMessage();
    }

    /**
     * The actual installer running task.  
     * This runs in a SwingWorker thread.
     * 
     * @author xiandros
     *
     */
    public class ProfileBackupTask 
    {    	    	
    	ProfileBackupTask(FixProfile fp) 
    	{
    		try
    		{
    			//Perform installation task for fix.  
    			if (!canceled && !done)
    			{
    				if (m_pbp != null)
    				{  				
    					m_pbp.backupProfile(fp);
    					
    				}
        			done = true;
        			m_pbp.setCurrent(m_pbp.getLengthOfTask());
    				
    			}    			
    		}
    		catch(Exception e)
    		{
    			
    		}
        }   	  	    	
    }

}
