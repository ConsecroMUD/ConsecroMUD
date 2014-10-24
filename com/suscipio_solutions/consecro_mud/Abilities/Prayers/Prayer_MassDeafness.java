package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_MassDeafness extends Prayer
{
	@Override public String ID() { return "Prayer_MassDeafness"; }
	private final static String localizedName = CMLib.lang().L("Mass Deafness");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Deafness)");
	@Override public String displayText() { return localizedStaticDisplay; }

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if(!(affected instanceof MOB)) return;

		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_HEAR);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!CMLib.flags().canHear((MOB)target))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if((canBeUninvoked())&&(CMLib.flags().canHear(mob)))
			mob.tell(L("Your hearing returns."));
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;

		boolean success=proficiencyCheck(mob,0,auto);
		boolean nothingDone=true;
		if(success)
		{
			for (final Object element : h)
			{
				final MOB target=(MOB)element;
				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":L("^S<S-NAME> @x1 an unholy deafness upon <T-NAMESELF>.^?",prayForWord(mob)));
				if((target!=mob)&&(mob.location().okMessage(mob,msg)))
				{
					mob.location().send(mob,msg);
					if(msg.value()<=0)
					{
						success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> go(es) deaf!!"));
					}
					nothingDone=false;
				}
			}
		}

		if(nothingDone)
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to deafen everyone, but flub(s) it."));


		// return whether it worked
		return success;
	}
}
