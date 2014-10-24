package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB.Tattoo;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings({"unchecked","rawtypes"})
public class Duel extends StdCommand
{
	public Duel(){}

	private final String[] access=I(new String[]{"DUEL"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		MOB target=null;
		if(commands.size()<2)
		{
			mob.tell(L("Duel whom?"));
			return false;
		}

		final String whomToKill=CMParms.combine(commands,1);
		target=mob.location().fetchInhabitant(whomToKill);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("I don't see '@x1' here.",whomToKill));
			return false;
		}

		if(mob==target)
			mob.tell(L("You may not duel yourself."));
		else
		if((mob.isMonster()))
			mob.tell(L("You are not allowed to duel @x1.",target.name(mob)));
		else
		{
			final Tattoo uiT=target.findTattoo("IDUEL");
			final Tattoo uuT=target.findTattoo("UDUEL");
			final Tattoo iiT=mob.findTattoo("IDUEL");
			final Tattoo iuT=mob.findTattoo("UDUEL");
			if((uiT==null)&&(iiT==null)&&(uuT==null)&&(iuT==null))
			{
				final int duelTicks=CMProps.getIntVar(CMProps.Int.DUELTICKDOWN);
				mob.addTattoo(new Tattoo("IDUEL",duelTicks));
				target.addTattoo(new Tattoo("UDUEL",duelTicks));
				final long time = CMProps.getTickMillis() * duelTicks;
				mob.location().show(mob, target, CMMsg.MSG_DUELCHALLENGE, L("^X<S-NAME> <S-HAS-HAVE> challenged <T-NAME> to a duel, which <T-HE-SHE> <T-HAS-HAVE> @x1 seconds to consider.^.^N",""+(time/1000)));
				target.tell(L("^NEnter ^HDUEL @x1^N to accept this challenge and begin fighting.",mob.name(target)));
				return true;
			}
			else
			if((uiT != null)&&(iuT != null))
			{
				target.tell(mob,target,null,L("^X<T-NAME> <T-HAS-HAVE> ACCEPTED <T-YOUPOSS> CHALLENGE!^.^N"));
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
				final Ability A=CMClass.getAbility("Dueler");
				if(A!=null) A.invoke(target, mob, true, 0);
			}
			else
			if(uiT!=null)
			{
				mob.tell(mob,target,null,L("<T-NAME> is awaiting a response to a previous challenge and cannot be challenged at this time."));
				return false;
			}
			else
			if(uuT!=null)
			{
				mob.tell(mob,target,null,L("<T-NAME> is considering a response to a previous challenger and cannot be challenged at this time."));
				return false;
			}
			else
			if((iuT!=null)||(iiT!=null))
			{
				final int duelTicks=CMProps.getIntVar(CMProps.Int.DUELTICKDOWN);
				final long time = CMProps.getTickMillis() * duelTicks;
				mob.tell(mob,target,null,L("Your previous challenge has not yet expired.  Please wait @x1 seconds longer and try again.",""+(time/1000)));
				return false;
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
