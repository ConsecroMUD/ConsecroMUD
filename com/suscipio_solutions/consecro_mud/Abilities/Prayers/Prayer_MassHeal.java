package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_MassHeal extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_MassHeal"; }
	private final static String localizedName = CMLib.lang().L("Mass Heal");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_HEALINGMAGIC;}

	@Override
	public boolean supportsMending(Physical item)
	{
		return (item instanceof MOB)
				&&((((MOB)item).curState()).getHitPoints()<(((MOB)item).maxState()).getHitPoints());
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!supportsMending(target))
					return Ability.QUALITY_INDIFFERENT;
				if(((MOB)target).charStats().getMyRace().racialCategory().equals("Undead"))
					return Ability.QUALITY_MALICIOUS;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;
		for (final Object element : h)
		{
			final MOB target=(MOB)element;
			final boolean undead=target.charStats().getMyRace().racialCategory().equals("Undead");
			if(success)
			{
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				final CMMsg msg=CMClass.getMsg(mob,target,this,(!undead?0:CMMsg.MASK_MALICIOUS)|verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) surrounded by a white light."):L("^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>.^?"));
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					final int healing=CMLib.dice().roll(adjustedLevel(mob,asLevel),5,adjustedLevel(mob,asLevel));
					CMLib.combat().postHealing(mob,target,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
					target.tell(L("You feel tons better!"));
				}
			}
			else
				beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, but @x1 does not heed.",hisHerDiety(mob)));
		}

		// return whether it worked
		return success;
	}
}
