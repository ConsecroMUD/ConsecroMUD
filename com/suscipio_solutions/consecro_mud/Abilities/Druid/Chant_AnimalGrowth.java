package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_AnimalGrowth extends Chant
{
	@Override public String ID() { return "Chant_AnimalGrowth"; }
	private final static String localizedName = CMLib.lang().L("Animal Growth");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Animal Growth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if((canBeUninvoked())&&(mob.location()!=null)&&(!mob.amDead()))
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> shrink(s) back down to size."));
	}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		affectedStats.setStat(CharStats.STAT_STRENGTH,affectedStats.getStat(CharStats.STAT_STRENGTH)+5);
		affectedStats.setStat(CharStats.STAT_DEXTERITY,affectedStats.getStat(CharStats.STAT_DEXTERITY)-3);
		affectedStats.setStat(CharStats.STAT_WEIGHTADJ,
				affectedStats.getStat(CharStats.STAT_WEIGHTADJ)
				+(affectedMOB.basePhyStats().weight()*3));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectedStats)
	{
		super.affectPhyStats(affected,affectedStats);
		affectedStats.setHeight(affectedStats.height()*2);
		String oldName=affected.Name().toUpperCase();
		if(oldName.startsWith("A "))
			oldName=affected.Name().substring(2).trim();
		else
		if(oldName.startsWith("AN "))
			oldName=affected.Name().substring(3).trim();
		else
		if(oldName.startsWith("THE "))
			oldName=affected.Name().substring(4).trim();
		else
			oldName=affected.Name();
		affectedStats.setName(L("An ENORMOUS @x1",oldName));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!CMLib.flags().isAnimalIntelligence((MOB)target))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(!CMLib.flags().isAnimalIntelligence(target))
		{
			mob.tell(L("This chant only works on animals."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> grow(s) to an ENORMOUS size!"));
				beneficialAffect(mob,target,asLevel,0);
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
