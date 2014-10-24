package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class MUDInfo extends CM1Command
{
	@Override public String getCommandWord(){ return "MUDINFO";}
	public MUDInfo(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	public void run()
	{
		try
		{
			if((parameters.length()==0)||(parameters.equalsIgnoreCase("STATUS")))
				req.sendMsg("[OK "+CMProps.getVar(CMProps.Str.MUDSTATUS)+"]");
			else
			if(parameters.equalsIgnoreCase("PORTS"))
				req.sendMsg("[OK "+CMProps.getVar(CMProps.Str.MUDPORTS)+"]");
			else
			if(parameters.equalsIgnoreCase("VERSION"))
				req.sendMsg("[OK "+CMProps.getVar(CMProps.Str.MUDVER)+"]");
			else
			if(parameters.equalsIgnoreCase("DOMAIN"))
				req.sendMsg("[OK "+CMProps.getVar(CMProps.Str.MUDDOMAIN)+"]");
			else
			if(parameters.equalsIgnoreCase("NAME"))
				req.sendMsg("[OK "+CMProps.getVar(CMProps.Str.MUDNAME)+"]");
			else
				req.sendMsg("[FAIL "+getHelp(req.getUser(), null, null)+"]");
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
		return "USAGE: MUDINFO STATUS, PORTS, VERSION, DOMAIN, NAME";
	}
}
