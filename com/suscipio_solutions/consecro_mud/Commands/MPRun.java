package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class MPRun extends StdCommand
{
	public MPRun(){}

	private final String[] access=I(new String[]{"MPRUN"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.findTattoo("SYSTEM_MPRUNDOWN")!=null)
			return CMLib.commands().handleUnknownCommand(mob, commands);
		MOB checkMOB=mob;
		if(commands.size()>1)
		{
			final String firstParm=(String)commands.elementAt(1);
			final int x=firstParm.indexOf(':');
			if(x>0)
			{
				checkMOB=CMLib.players().getLoadPlayer(firstParm.substring(0,x));
				if(checkMOB==null)
				{
					mob.addTattoo(new MOB.Tattoo("SYSTEM_MPRUNDOWN",(int)CMProps.getTicksPerMinute()));
					return CMLib.commands().handleUnknownCommand(mob, commands);
				}
				final String pw=firstParm.substring(x+1);
				if(!checkMOB.playerStats().matchesPassword(pw))
				{
					mob.addTattoo(new MOB.Tattoo("SYSTEM_MPRUNDOWN",(int)(2 * CMProps.getTicksPerMinute())));
					return CMLib.commands().handleUnknownCommand(mob, commands);
				}
				commands.removeElementAt(1);
			}
		}
		if(!CMSecurity.isAllowed(checkMOB,mob.location(),CMSecurity.SecFlag.JSCRIPTS))
			return CMLib.commands().handleUnknownCommand(mob, commands);
		if(commands.size()<2)
		{
			mob.tell(L("mprun (user:password) [script]"));
			return false;
		}
		commands.removeElementAt(0);

		final String cmd = CMParms.combineQuoted(commands, 0);
		executeScript(mob, cmd);
		mob.tell(L("Completed."));
		return false;
	}

	private void executeScript(MOB mob, String script)
	{
		final ScriptingEngine S=(ScriptingEngine)CMClass.getCommon("DefaultScriptingEngine");
		S.setSavable(false);
		S.setVarScope("*");
		S.setScript(script);
		final CMMsg msg2=CMClass.getMsg(mob,mob,null,CMMsg.MSG_OK_VISUAL,null,null,L("MPRUN"));
		S.executeMsg(mob, msg2);
		S.dequeResponses();
		S.tick(mob,Tickable.TICKID_MOB);
	}

	@Override public boolean canBeOrdered(){return false;}
	@Override public boolean securityCheck(MOB mob){return true; }
	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
