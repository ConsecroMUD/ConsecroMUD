package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_Sweep extends FighterSkill
{
	@Override public String ID() { return "Fighter_Sweep"; }
	private final static String localizedName = CMLib.lang().L("Sweep");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SWEEP"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{

		final float f=(float)CMath.mul(0.1,(float)getXLEVELLevel(invoker()));
		affectableStats.setAttackAdjustment((int)Math.round(CMath.div(affectableStats.attackAdjustment(),2.0-f)));
		affectableStats.setDamage((int)Math.round(CMath.div(affectableStats.damage(),3.0-f)));
	}
	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			final Set<MOB> h=properTargets(mob,target,false);
			if(h.size()<2) return Ability.QUALITY_INDIFFERENT;
			for(final MOB M : h)
				if((M.rangeToTarget()<0)||(M.rangeToTarget()>0))
					h.remove(M);
			if(h.size()<2) return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to sweep!"));
			return false;
		}
		if(!mob.isInCombat())
		{
			mob.tell(L("You must be in combat to sweep!"));
			return false;
		}
		final Set<MOB> h=properTargets(mob,givenTarget,false);
		for(final MOB M : h)
			if((M.rangeToTarget()<0)||(M.rangeToTarget()>0))
				h.remove(M);

		if(h.size()==0)
		{
			mob.tell(L("There aren't enough enough targets in range!"));
			return false;
		}

		final Item w=mob.fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon)))
		{
			mob.tell(L("You need a weapon to sweep!"));
			return false;
		}
		final Weapon wp=(Weapon)w;
		if(wp.weaponType()!=Weapon.TYPE_SLASHING)
		{
			mob.tell(L("You cannot sweep with @x1!",wp.name()));
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
			CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_NOISYMOVEMENT,L("^F^<FIGHT^><S-NAME> sweep(s)!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				mob.addEffect(this);
				mob.recoverPhyStats();
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_OK_ACTION|(auto?CMMsg.MASK_ALWAYS:0),null);
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						CMLib.combat().postAttack(mob,target,w);
					}
				}
				mob.delEffect(this);
				mob.recoverPhyStats();
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> fail(s) to sweep."));

		// return whether it worked
		return success;
	}
}
