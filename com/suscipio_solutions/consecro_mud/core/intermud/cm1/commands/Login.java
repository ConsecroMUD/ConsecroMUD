package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Login extends CM1Command
{
	@Override public String getCommandWord(){ return "LOGIN";}
	public Login(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			final int x=parameters.indexOf(' ');
			if(x<0)
				req.sendMsg("[FAIL "+getHelp(req.getUser(), null, null)+"]");
			else
			{
				final String user=parameters.substring(0,x);
				final String pass=parameters.substring(x+1);
				final MOB M=CMLib.players().getLoadPlayer(user);
				if((M==null) || (M.playerStats()==null) || (!M.playerStats().matchesPassword(pass)))
				{
					Thread.sleep(5000);
					req.sendMsg("[FAIL]");
				}
				else
				{
					req.login(M);
					req.sendMsg("[OK]");
				}
			}
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
		return "USAGE: LOGIN <CHARACTER NAME> <PASSWORD>: Logs in a new character to act as the authorizing user.";
	}
}
