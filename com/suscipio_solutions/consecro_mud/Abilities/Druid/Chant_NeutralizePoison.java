package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_NeutralizePoison extends Chant implements MendingSkill
{
	@Override public String ID() { return "Chant_NeutralizePoison"; }
	private final static String localizedName = CMLib.lang().L("Neutralize Poison");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}

	@Override
	public boolean supportsMending(Physical item)
	{
		if(!(item instanceof MOB)) return false;
		final boolean canMend=returnOffensiveAffects(item).size()>0;
		return canMend;
	}

	public List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final Vector offenders=new Vector();

		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_POISON))
				offenders.addElement(A);
		}
		return offenders;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				final List<Ability> offensiveAffects=returnOffensiveAffects(target);
				if(offensiveAffects.size()==0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final List<Ability> offensiveAffects=returnOffensiveAffects(target);

		if((success)&&((offensiveAffects.size()>0)
					   ||((target instanceof Drink)&&(((Drink)target).liquidType()==RawMaterial.RESOURCE_POISON))))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> look(s) cleansed of any poisons."):L("^S<S-NAME> chant(s) for <T-NAME> to be cleansed of poisons.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					offensiveAffects.get(a).unInvoke();
				if((target instanceof Drink)&&(((Drink)target).liquidType()==RawMaterial.RESOURCE_POISON))
				{
					((Drink)target).setLiquidType(RawMaterial.RESOURCE_FRESHWATER);
					target.basePhyStats().setAbility(0);
				}
				if((!CMLib.flags().stillAffectedBy(target,offensiveAffects,false))
				&&(target instanceof MOB))
				{
					((MOB)target).tell(L("You feel much better!"));
					((MOB)target).recoverCharStats();
					((MOB)target).recoverMaxState();
				}
				target.recoverPhyStats();
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> chant(s) for <T-NAME>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
