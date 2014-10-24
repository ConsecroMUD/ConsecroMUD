package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings({"unchecked","rawtypes"})
public class Reply extends StdCommand
{
	public Reply(){}

	private final String[] access=I(new String[]{"REPLY","REP","RE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob==null) return false;
		final PlayerStats pstats=mob.playerStats();
		if(pstats==null) return false;
		if(pstats.getReplyToMOB()==null)
		{
			mob.tell(L("No one has told you anything yet!"));
			return false;
		}
		if((pstats.getReplyToMOB().Name().indexOf('@')<0)
		&&((CMLib.players().getPlayer(pstats.getReplyToMOB().Name())==null)
			||(pstats.getReplyToMOB().isMonster())
			||(!CMLib.flags().isInTheGame(pstats.getReplyToMOB(),true))))
		{
			mob.tell(L("@x1 is no longer logged in.",pstats.getReplyToMOB().Name()));
			return false;
		}
		if(CMParms.combine(commands,1).length()==0)
		{
			mob.tell(L("Tell '@x1' what?",pstats.getReplyToMOB().Name()));
			return false;
		}
		final int replyType=pstats.getReplyType();

		switch(replyType)
		{
		case PlayerStats.REPLY_SAY:
			if((pstats.getReplyToMOB().Name().indexOf('@')<0)
			&&((mob.location()==null)||(!mob.location().isInhabitant(pstats.getReplyToMOB()))))
			{
				mob.tell(L("@x1 is no longer in the room.",pstats.getReplyToMOB().Name()));
				return false;
			}
			CMLib.commands().postSay(mob,pstats.getReplyToMOB(),CMParms.combine(commands,1),false,false);
			break;
		case PlayerStats.REPLY_TELL:
		{
			final Session S=pstats.getReplyToMOB().session();
			if(S!=null) S.snoopSuspension(1);
			CMLib.commands().postSay(mob,pstats.getReplyToMOB(),CMParms.combine(commands,1),true,true);
			if(S!=null) S.snoopSuspension(-11);
			break;
		}
		case PlayerStats.REPLY_YELL:
			{
				final Command C=CMClass.getCommand("Say");
				if((C!=null)&&(C.securityCheck(mob)))
				{
					commands.setElementAt("Yell",0);
					C.execute(mob, commands,metaFlags);
				}
				break;
			}
		}
		if((pstats.getReplyToMOB().session()!=null)
		&&(pstats.getReplyToMOB().session().isAfk()))
			mob.tell(pstats.getReplyToMOB().session().getAfkMessage());
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
