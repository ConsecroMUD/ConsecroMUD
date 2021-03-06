package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Struggle extends BardSkill
{
	@Override public String ID() { return "Skill_Struggle"; }
	private final static String localizedName = CMLib.lang().L("Struggle");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"STRUGGLE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	@Override public int usageType(){return USAGE_MANA;}


	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if(!CMLib.flags().isBound(mob))
			unInvoke();
		else
			stats.setStat(CharStats.STAT_STRENGTH,stats.getStat(CharStats.STAT_STRENGTH)+stats.getStat(CharStats.STAT_DEXTERITY)+mob.phyStats().level());
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if((CMLib.flags().isBound(target))&&(target==mob))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!CMLib.flags().isBound(mob))
		{
			mob.tell(L("You don't seem to be bound by anything you can struggle against!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> attempt(s) to struggle with <S-HIS-HER> bonds."));
			if(mob.location().okMessage(mob,msg))
			{
				mob.addEffect(this);
				mob.recoverCharStats();
				mob.location().send(mob,msg);
				mob.delEffect(this);
				mob.recoverCharStats();
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> fumble(s) <S-HIS-HER> attempt to struggle."));

		return success;
	}

}
