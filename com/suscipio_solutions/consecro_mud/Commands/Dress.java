package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings("rawtypes")
public class Dress extends StdCommand
{
	public Dress(){}

	private final String[] access=I(new String[]{"DRESS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("Dress whom in what?"));
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
		if((target.willFollowOrdersOf(mob))||(CMLib.flags().isBoundOrHeld(target)))
		{
			final Item item=mob.findItem(null,what);
			if((item==null)||(!CMLib.flags().canBeSeenBy(item,mob)))
			{
				mob.tell(L("I don't see @x1 here.",what));
				return false;
			}
			if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.ORDER)
			||(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS)&&(target.isMonster()))
			||(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDMOBS)&&(target.isMonster())))
			{
				mob.location().show(mob,target,item,CMMsg.MASK_ALWAYS|CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> mystically put(s) <O-NAME> on <T-NAMESELF>."));
				item.unWear();
				target.moveItemTo(item);
				item.wearIfPossible(target);
				if((item.rawProperLocationBitmap()!=0)&&(item.amWearingAt(Wearable.IN_INVENTORY))&&(target.isMonster()))
				{
					if(item.rawLogicalAnd())
						item.wearAt(item.rawProperLocationBitmap());
					else
					{
						for(final long wornCode : Wearable.CODES.ALL())
							if(wornCode != Wearable.IN_INVENTORY)
							{
								if(item.fitsOn(wornCode)&&(wornCode!=Wearable.WORN_HELD))
								{ item.wearAt(wornCode); break;}
							}
						if(item.amWearingAt(Wearable.IN_INVENTORY))
							item.wearAt(Wearable.WORN_HELD);
					}
				}
				target.location().recoverRoomStats();
			}
			else
			{
				if(!item.amWearingAt(Wearable.IN_INVENTORY))
				{
					mob.tell(L("You might want to remove that first."));
					return false;
				}
				if(item instanceof Coins)
				{
					mob.tell(L("I don't think you want to dress someone in @x1.",item.name()));
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
					if(CMLib.commands().postDrop(mob,item,true,false,false))
					{
						msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_GET,CMMsg.MSG_GET,CMMsg.MSG_GET,null);
						if(mob.location().okMessage(mob,msg))
						{
							mob.location().send(mob,msg);
							msg=CMClass.getMsg(target,item,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_WEAR,CMMsg.MSG_WEAR,CMMsg.MSG_WEAR,null);
							if(mob.location().okMessage(mob,msg))
							{
								mob.location().send(mob,msg);
								mob.location().show(mob,target,item,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> put(s) <O-NAME> on <T-NAMESELF>."));
							}
							else
								mob.tell(L("You cannot seem to get @x1 on @x2.",item.name(),target.name(mob)));
						}
						else
							mob.tell(L("You cannot seem to get @x1 to @x2.",item.name(),target.name(mob)));
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
