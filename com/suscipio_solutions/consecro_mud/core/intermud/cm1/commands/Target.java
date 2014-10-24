package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Target extends CM1Command
{
	@Override public String getCommandWord(){ return "TARGET";}
	public Target(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			final PhysicalAgent P=getTarget(parameters);
			if(P!=null)
			{
				req.setTarget(P);
				req.sendMsg("[OK]");
				return;
			}
			req.sendMsg("[FAIL]");
		}
		catch(final Exception ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
	@Override public boolean passesSecurityCheck(MOB user, PhysicalAgent target){return true;} // anybody can target, its harmless
	@Override
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: TARGET USER, <NAME>, <NAME>@<LOCATION>, @<LOCATION>";
	}
}
