package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_ShockingGrasp extends Spell
{
	@Override public String ID() { return "Spell_ShockingGrasp"; }
	private final static String localizedName = CMLib.lang().L("Shocking Grasp");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	@Override public long flags(){return Ability.FLAG_AIRBASED;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_HANDS|verbalCastCode(mob,target,auto),L(auto?"":"^S<S-NAME> grab(s) at <T-NAMESELF>.^?")+CMLib.protocol().msp("shock.wav",40));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_ELECTRIC|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((target.location().okMessage(mob,msg))&&((target.location().okMessage(mob,msg2))))
			{
				target.location().send(mob,msg);
				if(msg.value()<=0)
				{
					target.location().send(mob,msg2);
					if(msg2.value()<=0)
					{
						invoker=mob;
						final int damage = CMLib.dice().roll(1,8,(adjustedLevel(mob,asLevel)+(2*super.getX1Level(mob)))/2);
						CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_ELECTRIC,Weapon.TYPE_STRIKING,auto?"<T-NAME> gasp(s) in shock and pain!":"The shocking grasp <DAMAGES> <T-NAME>!");
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> grab(s) at <T-NAMESELF>, but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}
