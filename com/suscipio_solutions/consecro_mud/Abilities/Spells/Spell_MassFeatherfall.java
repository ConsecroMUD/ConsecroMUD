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
public class Spell_MassFeatherfall extends Spell
{
	@Override public String ID() { return "Spell_MassFeatherfall"; }
	private final static String localizedName = CMLib.lang().L("Mass FeatherFall");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,false);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth floating."));
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
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) lightly.^?")))
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
						final Spell_FeatherFall fall=new Spell_FeatherFall();
						fall.setProficiency(proficiency());
						fall.beneficialAffect(mob,target,asLevel,0);
					}
				}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) lightly, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
