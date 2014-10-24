package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.CombatLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings({"unchecked","rawtypes"})
public class Kill extends StdCommand
{
	public Kill(){}

	private final String[] access=I(new String[]{"KILL","K","ATTACK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands==null)
		{
			if(mob.isInCombat())
			{
				CMLib.combat().postAttack(mob,mob.getVictim(),mob.fetchWieldedItem());
				return true;
			}
			return false;
		}

		MOB target=null;
		if(commands.size()<2)
		{
			if(!mob.isInCombat())
			{
				mob.tell(L("Kill whom?"));
				return false;
			}
			else
			if(CMProps.getIntVar(CMProps.Int.COMBATSYSTEM)==CombatLibrary.COMBAT_DEFAULT)
				return false;
			else
				target=mob.getVictim();
		}

		boolean reallyKill=false;
		String whomToKill=CMParms.combine(commands,1);
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.KILLDEAD)&&(!mob.isMonster()))
		{
			if(((String)commands.lastElement()).equalsIgnoreCase("DEAD"))
			{
				commands.removeElementAt(commands.size()-1);
				whomToKill=CMParms.combine(commands,1);
				reallyKill=true;
			}
		}

		if(target==null)
		{
			target=mob.location().fetchInhabitant(whomToKill);
			if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
			{
				mob.tell(L("I don't see '@x1' here.",whomToKill));
				return false;
			}
		}

		if(reallyKill)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_OK_ACTION,L("^F^<FIGHT^><S-NAME> touch(es) <T-NAMESELF>.^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.curState().setHitPoints(0);
				CMLib.combat().postDeath(mob,target,null);
			}
			return false;
		}

		if(mob.isInCombat())
		{
			final MOB oldVictim=mob.getVictim();
			if(((oldVictim!=null)&&(oldVictim==target)
			&&(CMProps.getIntVar(CMProps.Int.COMBATSYSTEM)==CombatLibrary.COMBAT_DEFAULT)))
			{
				mob.tell(L("^f^<FIGHT^>You are already fighting @x1.^</FIGHT^>^?",mob.getVictim().name()));
				return false;
			}

			if((mob.location().okMessage(mob,CMClass.getMsg(mob,target,CMMsg.MSG_WEAPONATTACK,null)))
			&&(oldVictim!=target))
			{
				if((oldVictim!=null)
				&&(target.getVictim()==oldVictim.getVictim())
				&&(target.rangeToTarget()>=0)
				&&(oldVictim.rangeToTarget()>=0))
				{
					int range=target.rangeToTarget()-oldVictim.rangeToTarget();
					if(mob.rangeToTarget()>=0)
						range+=mob.rangeToTarget();
					if(range>=0)
						mob.setAtRange(range);
				}
				mob.tell(L("^f^<FIGHT^>You are now targeting @x1.^</FIGHT^>^?",target.name(mob)));
				mob.setVictim(target);
				return false;
			}
		}

		if(!mob.mayPhysicallyAttack(target))
		{
			// some properties may be protecting the target -- give them a chance to complain
			final CMMsg msg=CMClass.getMsg(mob,target,CMMsg.MSG_NOISYMOVEMENT|CMMsg.MASK_MALICIOUS,null);
			final Room R=target.location();
			if((R==null)||(R.okMessage(mob, msg)))
				mob.tell(L("You are not allowed to attack @x1.",target.name(mob)));
		}
		else
		{
			final Item weapon=mob.fetchWieldedItem();
			if(weapon==null)
			{
				final Item possibleOtherWeapon=mob.fetchHeldItem();
				if((possibleOtherWeapon!=null)
				&&(possibleOtherWeapon instanceof Weapon)
				&&possibleOtherWeapon.fitsOn(Wearable.WORN_WIELD)
				&&(CMLib.flags().canBeSeenBy(possibleOtherWeapon,mob))
				&&(CMLib.flags().isRemovable(possibleOtherWeapon)))
				{
					CMLib.commands().postRemove(mob,possibleOtherWeapon,false);
					if(possibleOtherWeapon.amWearingAt(Wearable.IN_INVENTORY))
					{
						final Command C=CMClass.getCommand("Wield");
						if(C!=null) C.execute(mob,new XVector("WIELD",possibleOtherWeapon),metaFlags);
					}
				}
			}
			CMLib.combat().postAttack(mob,target,mob.fetchWieldedItem());
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
