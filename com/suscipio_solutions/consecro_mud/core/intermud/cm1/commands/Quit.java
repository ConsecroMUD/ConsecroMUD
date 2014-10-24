package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Quit extends CM1Command
{
	@Override public String getCommandWord(){ return "QUIT";}
	public Quit(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			req.sendMsg("[OK]");
			req.close();
		}
		catch(final java.io.IOException ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
	@Override public boolean passesSecurityCheck(MOB user, PhysicalAgent target){return true;}
	@Override
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: QUIT: Ends the current user session and disconnects the user.";
	}
}
