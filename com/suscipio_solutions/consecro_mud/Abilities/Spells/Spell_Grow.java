package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Grow extends Spell
{
	@Override public String ID() { return "Spell_Grow"; }
	private final static String localizedName = CMLib.lang().L("Grow");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Grow)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}

	protected int getOldWeight()
	{
		if(!CMath.isInteger(text()))
		{
			if(affected!=null)
				super.setMiscText(Integer.toString(affected.basePhyStats().weight()));
			return 0;
		}
		return CMath.s_int(text());
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			final double aff=1.0 + CMath.mul(0.1,(invoker().phyStats().level()+(2*getXLEVELLevel(invoker()))));
			affectableStats.setHeight((int)Math.round(CMath.mul(affectableStats.height(),aff)));
		}
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)/2);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)+((invoker().phyStats().level()+(2*getXLEVELLevel(invoker())))/5));
	}

	@Override
	public void unInvoke()
	{
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if(getOldWeight()<1)
				mob.baseCharStats().getMyRace().setHeightWeight(mob.basePhyStats(),(char)mob.baseCharStats().getStat(CharStats.STAT_GENDER));
			else
				mob.basePhyStats().setWeight(getOldWeight());
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> shrink(s) back down to size."));
			CMLib.utensils().confirmWearability(mob);
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(L("@x1 is already HUGE!",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Ability A=target.fetchEffect("Spell_Shrink");
				if((A!=null)&&(A.canBeUninvoked()))
					A.unInvoke();
				else
				{
					double aff=1.0 + CMath.mul(0.1,(target.phyStats().level()));
					aff=aff*aff;
					beneficialAffect(mob,target,asLevel,0);

					A=target.fetchEffect(ID());
					if(A!=null)
					{
						mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> grow(s) to an enormous size!"));
						setMiscText(Integer.toString(target.basePhyStats().weight()));
						A.setMiscText(Integer.toString(target.basePhyStats().weight()));
						long newWeight=Math.round(CMath.mul(target.basePhyStats().weight(),aff));
						if(newWeight>Short.MAX_VALUE)
							newWeight=Short.MAX_VALUE;
						target.basePhyStats().setWeight((int)newWeight);
						CMLib.utensils().confirmWearability(target);
					}
				}
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting but nothing happens."));


		// return whether it worked
		return success;
	}
}
