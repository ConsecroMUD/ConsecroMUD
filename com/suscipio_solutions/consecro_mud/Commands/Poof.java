package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Poof extends StdCommand
{
	public Poof(){}

	private final String[] access=I(new String[]{"POOF"});
	@Override public String[] getAccessWords(){return access;}

	public boolean errorOut(MOB mob)
	{
		mob.tell(L("You are not allowed to do that here."));
		return false;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		int showFlag=-1;
		if(CMProps.getIntVar(CMProps.Int.EDITORTYPE)>0)
			showFlag=-999;
		boolean ok=false;
		while((!ok)&&(mob.playerStats()!=null))
		{
			int showNumber=0;
			final String poofIn=CMLib.genEd().prompt(mob,mob.playerStats().getPoofIn(),++showNumber,showFlag,L("Poof-in"),true,true);
			final String poofOut=CMLib.genEd().prompt(mob,mob.playerStats().getPoofOut(),++showNumber,showFlag,L("Poof-out"),true,true);
			final String tranPoofIn=CMLib.genEd().prompt(mob,mob.playerStats().getTranPoofIn(),++showNumber,showFlag,L("Transfer-in"),true,true);
			final String tranPoofOut=CMLib.genEd().prompt(mob,mob.playerStats().getTranPoofOut(),++showNumber,showFlag,L("Transfer-out"),true,true);
			mob.playerStats().setPoofs(poofIn,poofOut,tranPoofIn,tranPoofOut);
			if(showFlag<-900){ ok=true; break;}
			if(showFlag>0){ showFlag=-1; continue;}
			showFlag=CMath.s_int(mob.session().prompt(L("Edit which? "),""));
			if(showFlag<=0)
			{
				showFlag=-1;
				ok=true;
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowedContainsAny(mob,mob.location(),CMSecurity.SECURITY_GOTO_GROUP);}


}
