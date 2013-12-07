package wm.fix.manager.opt.install;

import wm.fix.manager.data.FixProfile;
import wm.fix.manager.opt.ui.RestoreProgress;

public class RestoreTask 
{
    private int m_lengthOfTask;   
    private boolean done = false;
    private boolean canceled = false;   
    private FixProfile m_fp = null;
    
    private ProfileRestoreProcess m_prp = null;
    
    public RestoreTask(FixProfile fp, RestoreProgress rp) 
    {
        //Compute length of task...
        //Get task size from FixProfile object
        //based on configured webMethods IS environment.
    	m_prp = new ProfileRestoreProcess(rp);
        m_lengthOfTask = m_prp.getRestoreTaskSize(fp); 
        m_prp.setLengthOfTask(m_lengthOfTask);
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
            new ProfileRestoreTask(m_fp);        
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() 
    {
        //return m_lengthOfTask;
        return m_prp.getLengthOfTask();
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() 
    {
        //return current;
    	return m_prp.getCurrent();
    }

    public void stop() 
    {
        canceled = true;
        //statMessage = null;
        m_prp.setMessage(null);
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
    	return m_prp.getMessage();
    }

    /**
     * The actual installer running task.  
     * This runs in a SwingWorker thread.
     * 
     * @author xiandros
     *
     */
    public class ProfileRestoreTask 
    {    	    	
    	ProfileRestoreTask(FixProfile fp) 
    	{
    		try
    		{
    			//Perform installation task for fix.  
    			if (!canceled && !done)
    			{
    				if (m_prp != null)
    				{  				
    					m_prp.restoreProfile(fp);
    					
    				}
        			done = true;
        			m_prp.setCurrent(m_prp.getLengthOfTask());
    				
    			}    			
    		}
    		catch(Exception e)
    		{
    			
    		}
        }   	  	    	
    }

}
