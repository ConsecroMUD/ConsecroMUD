package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ScriptingEngine;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class ClanResign extends StdCommand
{
	public ClanResign(){}

	private final String[] access=I(new String[]{"CLANRESIGN"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String clanName=(commands.size()>1)?CMParms.combine(commands,1,commands.size()):"";

		Clan chkC=null;
		for(final Pair<Clan,Integer> c : mob.clans())
			if((clanName.length()==0)||(CMLib.english().containsString(c.first.getName(), clanName)))
			{	chkC=c.first; break; }

		final Session S=mob.session();
		final Clan C=chkC;
		if(C==null)
		{
			mob.tell(L("You can't resign from @x1.",((clanName.length()==0)?"anything":clanName)));
		}
		else
		if(S!=null)
		{
			S.prompt(new InputCallback(InputCallback.Type.CHOOSE,"N","YN",0)
			{
				@Override public void showPrompt() { S.promptPrint(L("Resign from @x1.  Are you absolutely SURE (y/N)?",C.getName()));}
				@Override public void timedOut() { }
				@Override public void callBack()
				{
					final String check=this.input;
					if(check.equalsIgnoreCase("Y"))
					{
						if(C.getGovernment().getExitScript().trim().length()>0)
						{
							final Pair<Clan,Integer> curClanRole=mob.getClanRole(C.clanID());
							if(curClanRole!=null)
								mob.setClan(C.clanID(), curClanRole.second.intValue());
							final ScriptingEngine S=(ScriptingEngine)CMClass.getCommon("DefaultScriptingEngine");
							S.setSavable(false);
							S.setVarScope("*");
							S.setScript(C.getGovernment().getExitScript());
							final CMMsg msg2=CMClass.getMsg(mob,mob,null,CMMsg.MSG_OK_VISUAL,null,null,L("CLANEXIT"));
							S.executeMsg(mob, msg2);
							S.dequeResponses();
							S.tick(mob,Tickable.TICKID_MOB);
						}
						CMLib.clans().clanAnnounce(mob,L("Member resigned from @x1 @x2: @x3",C.getGovernmentName(),C.name(),mob.Name()));
						C.delMember(mob);
					}
				}
			});
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
