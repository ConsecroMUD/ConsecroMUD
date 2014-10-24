package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SeeAura extends Spell
{
	@Override public String ID() { return "Spell_SeeAura"; }
	private final static String localizedName = CMLib.lang().L("See Aura");
	@Override public String name() { return localizedName; }
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(target==mob)
		{
			mob.tell(L("Um, you could just enter SCORE."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^SYou draw out <T-NAME>s aura, seeing <T-HIM-HER> from the inside out...^?"),verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> draw(s) out your aura.^?"),verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> draws out <T-NAME>s aura.^?"));
		if(success)
		{
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuilder str=CMLib.commands().getScore(target);
				if(!mob.isMonster())
					mob.session().wraplessPrintln(str.toString());
			}
		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> examine(s) <T-NAME>, incanting, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
