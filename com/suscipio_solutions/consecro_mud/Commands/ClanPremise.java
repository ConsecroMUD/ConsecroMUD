package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Authority;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings({"unchecked","rawtypes"})
public class ClanPremise extends StdCommand
{
	public ClanPremise(){}

	private final String[] access=I(new String[]{"CLANPREMISE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String clanName=(commands.size()>1)?CMParms.combine(commands,1,commands.size()):"";

		Clan chkC=null;
		final boolean skipChecks=mob.getClanRole(mob.Name())!=null;
		if(skipChecks) chkC=mob.getClanRole(mob.Name()).first;

		if(chkC==null)
		for(final Pair<Clan,Integer> c : mob.clans())
			if((clanName.length()==0)||(CMLib.english().containsString(c.first.getName(), clanName))
			&&(c.first.getAuthority(c.second.intValue(), Clan.Function.PREMISE)!=Authority.CAN_NOT_DO))
			{	chkC=c.first; break; }

		commands.setElementAt(getAccessWords()[0],0);

		final Clan C=chkC;
		if(C==null)
		{
			mob.tell(L("You aren't allowed to set a premise for @x1.",((clanName.length()==0)?"anything":clanName)));
			return false;
		}

		if((!skipChecks)&&(!CMLib.clans().goForward(mob,C,commands,Clan.Function.PREMISE,false)))
		{
			mob.tell(L("You aren't in the right position to set the premise to your @x1.",C.getGovernmentName()));
			return false;
		}
		if((skipChecks)&&(commands.size()>1))
		{
			setClanPremise(mob,C,CMParms.combine(commands,1));
			return false;
		}
		final Session session=mob.session();
		if(session==null)
		{
			return false;
		}
		session.prompt(new InputCallback(InputCallback.Type.PROMPT,"",0)
		{
			@Override public void showPrompt() { session.promptPrint(L("Describe your @x1's Premise\n\r: ",C.getGovernmentName()));}
			@Override public void timedOut() { }
			@Override public void callBack()
			{
				final String premise=this.input;
				if(premise.length()==0)
				{
					return;
				}
				final Vector cmds=new Vector();
				cmds.addElement(getAccessWords()[0]);
				cmds.addElement(premise);
				if(skipChecks||CMLib.clans().goForward(mob,C,cmds,Clan.Function.PREMISE,true))
				{
					setClanPremise(mob,C,premise);
				}
			}
		});
		return false;
	}

	public void setClanPremise(MOB mob, Clan C, String premise)
	{
		C.setPremise(premise);
		C.update();
		CMLib.clans().clanAnnounce(mob,L("The premise of @x1 @x2 has been changed.",C.getGovernmentName(),C.clanID()));
	}


	@Override public boolean canBeOrdered(){return false;}


}
