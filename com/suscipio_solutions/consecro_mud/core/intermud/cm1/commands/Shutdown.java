package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Shutdown extends CM1Command
{
	@Override public String getCommandWord(){ return "SHUTDOWN";}
	public Shutdown(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			req.sendMsg("[OK]");
			com.suscipio_solutions.consecro_mud.application.MUD.globalShutdown(null,true,null);
			req.close();
		}
		catch(final java.io.IOException ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
	@Override public boolean passesSecurityCheck(MOB user, PhysicalAgent target){return (user!=null)&&CMSecurity.isAllowed(user,user.location(),CMSecurity.SecFlag.SHUTDOWN);}
	@Override
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: SHUTDOWN: Shuts down the mud.";
	}
}
