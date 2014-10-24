package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_GravitySlam extends Spell
{
	@Override public String ID() { return "Spell_GravitySlam"; }
	private final static String localizedName = CMLib.lang().L("Gravity Slam");
	@Override public String name() { return localizedName; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override public long flags(){return Ability.FLAG_MOVING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"":"^S<S-NAME> incant(s) and point(s) at <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;

				int damage = 0;
				int maxDie =  (int)Math.round((adjustedLevel(mob,asLevel)+(2.0*super.getX1Level(mob)))/2.0);
				if(!CMLib.flags().isInFlight(target))
					maxDie=maxDie/2;
				final Room R=mob.location();
				if((R.domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER))
					maxDie=maxDie/6;
				if((R.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
				||(R.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE))
					maxDie=maxDie/4;

				damage += CMLib.dice().roll(maxDie,20,6+maxDie);
				if(msg.value()>0)
					damage = (int)Math.round(CMath.div(damage,2.0));
				if(!CMLib.flags().isInFlight(target))
					mob.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> <S-IS-ARE> hurled up into the air and **SLAMMED** back down!"));
				else
					mob.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> <S-IS-ARE> hurled even higher into the air and **SLAMMED** back down!"));

				if(target.location()==mob.location())
					CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BASHING,"The fall <DAMAGE> <T-NAME>!");
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> incant(s) and point(s) at <T-NAMESELF>, but flub(s) the spell."));


		// return whether it worked
		return success;
	}
}
