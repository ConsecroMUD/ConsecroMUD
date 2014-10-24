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
public class Spell_MassHold extends Spell
{
	@Override public String ID() { return "Spell_MassHold"; }
	private final static String localizedName = CMLib.lang().L("Mass Hold");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth putting to sleep."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,-20,auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> incant(s) and wave(s) <S-HIS-HER> arms.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// if they can't hear the sleep spell, it
					// won't happen
					if(CMLib.flags().canBeHeardSpeakingBy(mob,target))
					{
						// it worked, so build a copy of this ability,
						// and add it to the affects list of the
						// affected MOB.  Then tell everyone else
						// what happened.
						final MOB oldVictim=mob.getVictim();
						final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
						if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
						{
							mob.location().send(mob,msg);
							if(msg.value()<=0)
							{
								int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob)));
								if(levelDiff<0) levelDiff=0;
								if(levelDiff>6) levelDiff=6;

								final Spell_Hold spell=new Spell_Hold();
								spell.setProficiency(proficiency());
								success=spell.maliciousAffect(mob,target,asLevel,7-levelDiff,-1)!=null;
								if(success)
									if(target.location()==mob.location())
										target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) perfectly still!!"));
							}
						}
						if(oldVictim==null) mob.setVictim(null);
					}
					else
						maliciousFizzle(mob,target,L("<T-NAME> seem(s) unaffected by the spell from <S-NAME>."));
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> incant(s) a spell, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
