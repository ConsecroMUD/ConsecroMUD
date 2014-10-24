package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_MassFly extends Spell
{
	@Override public String ID() { return "Spell_MassFly"; }
	private final static String localizedName = CMLib.lang().L("Mass Fly");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,false);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth making fly."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> arms and speak(s).^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						if(mob.location()==target.location())
							target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> start(s) to fly around!"));
						final Spell_Fly fly=new Spell_Fly();
						fly.setProficiency(proficiency());
						fly.beneficialAffect(mob,target,asLevel,0);
					}
				}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> wave(s) <S-HIS-HER> arms and speak(s), but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
