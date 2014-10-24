package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;


@SuppressWarnings("rawtypes")
public class Feed extends StdCommand
{
	public Feed(){}

	private final String[] access=I(new String[]{"FEED"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("Feed who what?"));
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
		if(mob.isInCombat())
		{
			mob.tell(L("Not while you are in combat!"));
			return false;
		}
		if(target.willFollowOrdersOf(mob)||(CMLib.flags().isBoundOrHeld(target)))
		{
			final Item item=mob.findItem(null,what);
			if((item==null)||(!CMLib.flags().canBeSeenBy(item,mob)))
			{
				mob.tell(L("I don't see @x1 here.",what));
				return false;
			}
			if(!item.amWearingAt(Wearable.IN_INVENTORY))
			{
				mob.tell(L("You might want to remove that first."));
				return false;
			}
			if((!(item instanceof Food))&&(!(item instanceof Drink)))
			{
				mob.tell(L("You might want to try feeding them something edibile or drinkable."));
				return false;
			}
			if(target.isInCombat())
			{
				mob.tell(L("Not while @x1 is in combat!",target.name(mob)));
				return false;
			}
			CMMsg msg=CMClass.getMsg(mob,target,item,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> feed(s) @x1 to <T-NAMESELF>.",item.name()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if((CMLib.commands().postDrop(mob,item,true,false,false))
				   &&(mob.location().isContent(item)))
				{
					msg=CMClass.getMsg(target,item,CMMsg.MASK_ALWAYS|CMMsg.MSG_GET,null);
					target.location().send(target,msg);
					if(target.isMine(item))
					{
						if(item instanceof Food)
							msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_EAT,CMMsg.MSG_EAT,CMMsg.MSG_EAT,null);
						else
							msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_DRINK,CMMsg.MSG_DRINK,CMMsg.MSG_DRINK,null);
						if(target.location().okMessage(target,msg))
							target.location().send(target,msg);
						if(target.isMine(item))
						{
							msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_DROP,CMMsg.MSG_DROP,CMMsg.MSG_DROP,null);
							if(mob.location().okMessage(mob,msg))
							{
								mob.location().send(mob,msg);
								CMLib.commands().postGet(mob,null,item,true);
							}
						}
					}
				}
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
