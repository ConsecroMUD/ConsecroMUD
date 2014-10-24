package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_DetectWeaknesses extends Spell
{
	@Override public String ID() { return "Spell_DetectWeaknesses"; }
	private final static String localizedName = CMLib.lang().L("Detect Weaknesses");
	@Override public String name() { return localizedName; }
	@Override
	public String displayText()
	{
		return "(Know weaknesses of "+text()+")";
	}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	protected MOB spottedM=null;
	protected boolean activated=true;

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(target instanceof MOB)
		{
			final MOB M=(MOB)target;
			if(!M.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);

		if((affected instanceof MOB)&&(activated))
		{
			if(invoker==null) invoker=(MOB)affected;
			final int xlvl=super.getXLEVELLevel(invoker);
			final float f=(float)0.2*xlvl;
			affectableStats.setDamage(affectableStats.damage()+(int)Math.round(CMath.div(affectableStats.damage(),4.0-f)));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(int)Math.round(CMath.div(affectableStats.attackAdjustment(),4.0-f)));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(ticking instanceof MOB)
		{
			final MOB mob=(MOB)ticking;
			if(invoker()!=null)
			{
				if((!invoker().isInCombat())
				||((spottedM!=null) && (spottedM.amDead())))
				{
					unInvoke();
					return false;
				}
				if((invoker()!=mob)
				&&(invoker().location()!=mob.location()))
				{
					unInvoke();
					return false;
				}
			}
			final MOB victim=mob.getVictim();
			if((victim!=null)
			&&((victim==spottedM)||( (spottedM==null) && victim.Name().equalsIgnoreCase(text()))))
			{
				if(!activated)
				{
					activated=true;
					mob.recoverPhyStats();
				}
			}
			else
			{
				if(activated)
				{
					activated=false;
					mob.recoverPhyStats();
				}
			}
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.tell(mob,null,null,L("<S-YOUPOSS> knowledge of @x1 fades.",text()));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(!target.isInCombat())
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> not in combat."));
			return false;
		}

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already knowledgable about <S-HIS-HER> target."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> knowingly cast(s) a spell concerning <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(beneficialAffect(mob,target,asLevel,0)!=null)
				{
					final Spell_DetectWeaknesses A=(Spell_DetectWeaknesses)target.fetchEffect(ID());
					final MOB victim=target.getVictim();
					if(A!=null)
					{
						A.spottedM=victim;
						A.setMiscText(victim.Name());
						mob.location().show(target,victim,CMMsg.MSG_OK_VISUAL,L("<S-NAME> attain(s) knowledge of <T-YOUPOSS> weaknesses!"));
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> speak(s) knowingly about <T-NAMESELF>, but nothing more happens."));


		// return whether it worked
		return success;
	}
}
