package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_CalmAnimal extends Chant
{
	@Override public String ID() { return "Chant_CalmAnimal"; }
	private final static String localizedName = CMLib.lang().L("Calm Animal");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(!CMLib.flags().isAnimalIntelligence(target))
		{
			mob.tell(L("@x1 is not an animal!",target.name(mob)));
			return false;
		}

		if(!target.isInCombat())
		{
			mob.tell(L("@x1 doesn't seem particularly enraged at the moment.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) surrounded by a natural light."):L("^S<S-NAME> chant(s) to <T-NAMESELF> for calm.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,3);
				for(int i=0;i<mob.location().numInhabitants();i++)
				{
					final MOB mob2=mob.location().fetchInhabitant(i);
					if((mob2.getVictim()==target)||(mob2==target))
						mob2.makePeace();
				}
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));
		// return whether it worked
		return success;
	}
}
