package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_MeteorStorm extends Spell
{
	@Override public String ID() { return "Spell_MeteorStorm"; }
	private final static String localizedName = CMLib.lang().L("Meteor Storm");
	@Override public String name() { return localizedName; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override public int minRange(){return 1;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth storming at."));
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

			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),L(auto?"A devastating meteor shower erupts!":"^S<S-NAME> conjur(s) up a devastating meteor shower!^?")+CMLib.protocol().msp("meteor.wav",40)))
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
						invoker=mob;

						int damage = 0;
						final int maxDie=(int)Math.round(CMath.div(adjustedLevel(mob,asLevel),3.0));
						damage = CMLib.dice().roll(maxDie,6,maxDie);
						if(msg.value()<=0)
							damage = (int)Math.round(CMath.div(damage,2.0));
						if(target.location()==mob.location())
							CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE,Weapon.TYPE_BASHING,"The meteors <DAMAGE> <T-NAME>!");
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to invoke a meteoric spell, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
