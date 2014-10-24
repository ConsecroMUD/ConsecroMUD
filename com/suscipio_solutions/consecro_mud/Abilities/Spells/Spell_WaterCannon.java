package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_WaterCannon extends Spell
{
	@Override public String ID() { return "Spell_WaterCannon"; }
	private final static String localizedName = CMLib.lang().L("Water Cannon");
	@Override public String name() { return localizedName; }
	@Override public int minRange(){return 2;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(3);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

   @Override
public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SITTING);
	}

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

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("<S-NAME> incant(s) at <T-NAMESELF> and geyser of water blasts towards <T-HIM-HER>."));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_WATER|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				invoker=mob;

				int damage = 0;
				final int maxDie =  (adjustedLevel( mob, asLevel )+(2*super.getX1Level(mob))) / 2;
				damage += CMLib.dice().roll(maxDie,8,15);
				mob.location().send(mob,msg2);
				if((msg2.value()>0)||(msg.value()>0))
					damage = (int)Math.round(CMath.div(damage,2.0));

				if(target.location()==mob.location())
					CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_WATER,Weapon.TYPE_BASHING,"The water blast <DAMAGE> <T-NAME>!");

				final int percentage = CMLib.dice().roll(1, 100, 0);
				if(percentage < 10)
				{
	  				final CMMsg msg3=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("<T-NAME> is knocked down by the water cannon."));
					if(mob.location().okMessage(mob,msg3))
					{
					   mob.location().send(mob, msg3);
					}
					affectPhyStats(target, target.phyStats());
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> incant(s) and point(s) at <T-NAMESELF>, but flub(s) the spell."));

		// return whether it worked
		return success;
	}
}
