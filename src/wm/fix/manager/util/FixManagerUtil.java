package wm.fix.manager.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import wm.fix.manager.ui.FixManagerConstants;
import wm.fix.manager.ui.FixManagerUIResourceBundle;

public class FixManagerUtil 
{
	public static void DefaultNotAvailableMessage(String title, FixManagerUIResourceBundle res, Shell shell)
	{
		String s_msg = res.getString(FixManagerConstants.FIX_MGR_MSG_NOT_AVAILABLE);
		MessageBox msg = new MessageBox(shell,SWT.OK|SWT.CANCEL|SWT.ICON_ERROR);
		msg.setText(title);
		msg.setMessage(s_msg);
		msg.open();		
	}
}
