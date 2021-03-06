package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Rebuke extends StdCommand
{
	public Rebuke(){}

	private final String[] access=I(new String[]{"REBUKE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Rebuke whom?"));
			return false;
		}
		final String str=CMParms.combine(commands,1);
		MOB target=mob.location().fetchInhabitant(str);
		if((target==null)&&(mob.getWorshipCharID().length()>0)
		&&(CMLib.english().containsString(mob.getWorshipCharID(),str)))
			target=CMLib.map().getDeity(str);
		if((target==null)&&(mob.getLiegeID().length()>0)
		&&(CMLib.english().containsString(mob.getLiegeID(),str)))
			target=CMLib.players().getLoadPlayer(mob.getLiegeID());
		if((target==null)&&(mob.numFollowers()>0))
			target=mob.fetchFollower(str);

		if(target==null)
		{
			mob.tell(L("You don't see anybody called '@x1' or you aren't serving '@x2'.",CMParms.combine(commands,1),CMParms.combine(commands,1)));
			return false;
		}

		CMMsg msg=null;
		msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_REBUKE,L("<S-NAME> rebuke(s) @x1.",target.Name()));
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		if((target.amFollowing()==mob)&&(target.location()!=null))
		{
			final Room R=target.location();
			msg=CMClass.getMsg(target,target.amFollowing(),null,CMMsg.MSG_NOFOLLOW,L("<S-NAME> stop(s) following <T-NAMESELF>."));
			// no room OKaffects, since the damn leader may not be here.
			if(target.okMessage(mob,msg))
				R.send(mob,msg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
