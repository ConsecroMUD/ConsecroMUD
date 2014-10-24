package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Fighter_Charge extends FighterSkill
{
	@Override public String ID() { return "Fighter_Charge"; }
	private final static String localizedName = CMLib.lang().L("Charge");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"CHARGE"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Charging!!)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public int minRange(){return 1;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}
	protected int code=0;
	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int c){code=c;}

	public boolean done=false;

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK))
			done=true;
		super.executeMsg(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
			if(done) unInvoke();
		return super.tick(ticking,tickID);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		final int xlvl=getXLEVELLevel(invoker());
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(2*(affected.phyStats().level()+xlvl)));
		affectableStats.setDamage(affectableStats.damage()+(affected.phyStats().level())+abilityCode()+xlvl);
		affectableStats.setArmor(affectableStats.armor()+(2*(xlvl+affected.phyStats().level())));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if((mob.isInCombat())&&(mob.rangeToTarget()<=0))
				return Ability.QUALITY_INDIFFERENT;
			if((CMLib.flags().isSitting(mob))||(mob.riding()!=null))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final boolean notInCombat=!mob.isInCombat();
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if((mob.isInCombat())
		&&(mob.rangeToTarget()<=0))
		{
			mob.tell(L("You can not charge while in melee!"));
			return false;
		}
		if((CMLib.flags().isSitting(mob))||(mob.riding()!=null))
		{
			mob.tell(L("You must be on your feet to charge!"));
			return false;
		}

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
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.MSG_ADVANCE,L("^F^<FIGHT^><S-NAME> charge(s) at <T-NAMESELF>!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(mob.getVictim()==target)
				{
					mob.setAtRange(0);
					target.setAtRange(0);
					beneficialAffect(mob,mob,asLevel,2);
					mob.recoverPhyStats();
					if(notInCombat)
					{
						done=true;
						CMLib.combat().postAttack(mob,target,mob.fetchWieldedItem());
					}
					else
						done=false;
					if(mob.getVictim()==null) mob.setVictim(null); // correct range
					if(target.getVictim()==null) target.setVictim(null); // correct range
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to charge <T-NAME>, but then give(s) up."));

		// return whether it worked
		return success;
	}
}
