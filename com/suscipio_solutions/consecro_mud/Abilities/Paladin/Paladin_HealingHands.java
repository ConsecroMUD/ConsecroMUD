package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Paladin_HealingHands extends StdAbility
{
	@Override public String ID() { return "Paladin_HealingHands"; }
	private final static String localizedName = CMLib.lang().L("Healing Hands");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"HANDS"});
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HEALING;}
	@Override public long flags(){return Ability.FLAG_HEALINGMAGIC;}
	@Override protected long minCastWaitTime(){return CMProps.getTickMillis();}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;

		if((!auto)&&(!(CMLib.flags().isGood(mob))))
		{
			mob.tell(L("Your alignment has alienated your god from you."));
			return false;
		}

		final int healing=1+((int)Math.round(CMath.div(adjustedLevel(mob,asLevel),4.0)));
		if(mob.curState().getMana()<healing)
		{
			mob.tell(L("You don't have enough mana to do that."));
			return false;
		}

		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		helpProficiency(mob, 0);

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_CAST_SOMANTIC_SPELL,auto?L("A pair of celestial hands surround <T-NAME>"):L("^S<S-NAME> lay(s) <S-HIS-HER> healing hands onto <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				int manaLost=healing;
				if(manaLost>0) manaLost=manaLost*-1;
				mob.curState().adjMana(manaLost,mob.maxState());
				CMLib.combat().postHealing(mob,target,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
				target.tell(L("You feel a little better!"));
				lastCastHelp=System.currentTimeMillis();
			}
		}
		else
			return beneficialVisualFizzle(mob,mob,L("<S-NAME> lay(s) <S-HIS-HER> healing hands onto <T-NAMESELF>, but <S-HIS-HER> god does not heed."));


		// return whether it worked
		return success;
	}

}
