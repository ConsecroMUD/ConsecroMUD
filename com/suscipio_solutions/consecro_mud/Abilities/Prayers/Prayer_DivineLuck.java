package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
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
public class Prayer_DivineLuck extends Prayer
{
	@Override public String ID() { return "Prayer_DivineLuck"; }
	private final static String localizedName = CMLib.lang().L("Divine Luck");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Divine Luck)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HOLYPROTECTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB,affectableStats);
		for(final int i : CharStats.CODES.SAVING_THROWS())
			affectableStats.setStat(i,
				affectableStats.getStat(i)
					+1+((affectedMOB.phyStats().level()+(2*super.getXLEVELLevel(invoker())))/5));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if(!(affected instanceof MOB)) return;
		final MOB mob=(MOB)affected;
		final int xlvl=super.getXLEVELLevel(invoker());
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+((affected.phyStats().level()+(2*xlvl))/5)+1);
		affectableStats.setDamage(affectableStats.damage()+((affected.phyStats().level()+(2*xlvl))/5)+1);

		if(mob.isInCombat())
		{
			final MOB victim=mob.getVictim();
			if(CMLib.flags().isEvil(victim))
				affectableStats.setArmor(affectableStats.armor()-5-xlvl);
		}
	}



	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your divine luck is over."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical target=mob;
		if((auto)&&(givenTarget!=null)) target=givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,target,null,L("<T-NAME> <T-IS-ARE> already affected by @x1.",name()));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> become(s) divinely lucky."):L("^S<S-NAME> @x1 for divine luck.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> @x1, but as luck would have it, there's no answer.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
