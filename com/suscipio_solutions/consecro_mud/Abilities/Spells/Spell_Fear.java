package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
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
public class Spell_Fear extends Spell
{
	@Override public String ID() { return "Spell_Fear"; }
	private final static String localizedName = CMLib.lang().L("Fear");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Afraid)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}

	@Override
	public void unInvoke()
	{
		MOB M=null;
		final MOB oldI=invoker;
		if(affected instanceof MOB) M=(MOB)affected;
		super.unInvoke();
		if(M!=null)
		{
			if(!M.isMonster())
				CMLib.commands().postStand(M,true);
			if((oldI!=M)&&(oldI!=null))
				M.tell(M,oldI,null,L("You are no longer afraid of <T-NAMESELF>."));
			else
				M.tell(L("You are no longer afraid."));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected instanceof MOB)
		&&(invoker!=null)
		&&(invoker!=affected)
		&&((((MOB)affected).location()==null)
			||(!((MOB)affected).location().isInhabitant(invoker))))
		{
			unInvoke();
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void affectPhyStats(Physical E, PhyStats stats)
	{
		if((affected instanceof MOB)&&(invoker!=null)&&(invoker!=affected)&&(((MOB)affected).getVictim()==invoker))
		{
			final int xlvl=super.getXLEVELLevel(invoker());
			final float f=(float)0.05*xlvl;
			stats.setArmor(stats.armor()+30+(3*xlvl));
			stats.setAttackAdjustment((int)Math.round(CMath.mul(stats.attackAdjustment(),0.90-f)));
			stats.setDamage((int)Math.round(CMath.mul(stats.damage(),0.90-f)));
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			if(!auto)
				mob.tell(L("There doesn't appear to be anyone here worth scaring."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			for (final Object element : h)
			{
				final MOB target=(MOB)element;

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> scare(s) <T-NAMESELF>.^?"));
				final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
				if(((text().toUpperCase().indexOf("WEAK")<0)||((mob.phyStats().level()/2)>target.phyStats().level()))
				&&((mob.location().okMessage(mob,msg))&&((mob.location().okMessage(mob,msg2)))))
				{
					mob.location().send(mob,msg);
					if(msg.value()<=0)
					{
						mob.location().send(mob,msg2);
						if(msg2.value()<=0)
						{
							invoker=mob;
							CMLib.commands().postFlee(target,"");
						}
					}
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> attempt(s) a frightening spell, but completely flub(s) it."));


		// return whether it worked
		return success;
	}
}
