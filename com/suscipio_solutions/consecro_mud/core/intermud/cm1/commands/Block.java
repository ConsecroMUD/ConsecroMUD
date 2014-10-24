package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Block extends CM1Command
{
	@Override public String getCommandWord(){ return "BLOCK";}
	public Block(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			final String eob="/BLOCK:"+Math.random();
			req.sendMsg("[OK "+eob+"]");
			req.setEndOfLine(eob);
		}
		catch(final Exception ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
	@Override public boolean passesSecurityCheck(MOB user, PhysicalAgent target){return true;}
	@Override
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: BLOCK: Changes the end-of-line for user input, returning a new end-of-line string.";
	}
}
