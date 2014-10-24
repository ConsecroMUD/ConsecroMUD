package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Undress extends StdCommand
{
	public Undress(){}

	private final String[] access=I(new String[]{"UNDRESS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("Undress whom? What would you like to remove?"));
			return false;
		}
		if(mob.isInCombat())
		{
			mob.tell(L("Not while you are in combat!"));
			return false;
		}
		commands.removeElementAt(0);
		final String what=(String)commands.lastElement();
		commands.removeElement(what);
		final String whom=CMParms.combine(commands,0);
		final MOB target=mob.location().fetchInhabitant(whom);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("I don't see @x1 here.",whom));
			return false;
		}
		if(target.willFollowOrdersOf(mob)||(CMLib.flags().isBoundOrHeld(target)))
		{
			final Item item=target.findItem(null,what);
			if((item==null)
			   ||(!CMLib.flags().canBeSeenBy(item,mob))
			   ||(item.amWearingAt(Wearable.IN_INVENTORY)))
			{
				mob.tell(L("@x1 doesn't seem to be equipped with '@x2'.",target.name(mob),what));
				return false;
			}
			if(target.isInCombat())
			{
				mob.tell(L("Not while @x1 is in combat!",target.name(mob)));
				return false;
			}
			CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_QUIETMOVEMENT,null);
			if(mob.location().okMessage(mob,msg))
			{
				msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_REMOVE,CMMsg.MSG_REMOVE,CMMsg.MSG_REMOVE,null);
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_DROP,CMMsg.MSG_DROP,CMMsg.MSG_DROP,null);
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						if(CMLib.commands().postGet(mob,null,item,true))
							mob.location().show(mob,target,item,CMMsg.MASK_ALWAYS|CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> take(s) <O-NAME> off <T-NAMESELF>."));
					}
					else
						mob.tell(L("You cannot seem to get @x1 off @x2.",item.name(),target.name(mob)));
				}
				else
					mob.tell(L("You cannot seem to get @x1 off of @x2.",item.name(),target.name(mob)));
			}
		}
		else
			mob.tell(L("@x1 won't let you.",target.name(mob)));
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
