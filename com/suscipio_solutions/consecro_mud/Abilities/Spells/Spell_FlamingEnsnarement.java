package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Set;
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
public class Spell_FlamingEnsnarement extends Spell
{
	@Override public String ID() { return "Spell_FlamingEnsnarement"; }
	private final static String localizedName = CMLib.lang().L("Flaming Ensnarement");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Ensnared in Fire)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(5);}
	@Override public int minRange(){return 1;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_BINDING|Ability.FLAG_FIREBASED|Ability.FLAG_HEATING;}

	public int amountRemaining=0;
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if(msg.amISource(mob))
		{
			switch(msg.sourceMinor())
			{
			case CMMsg.TYP_ENTER:
			case CMMsg.TYP_ADVANCE:
			case CMMsg.TYP_LEAVE:
			case CMMsg.TYP_FLEE:
				if(mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> struggle(s) against the flaming ensnarement.")))
				{
					amountRemaining-=mob.phyStats().level();
					if(amountRemaining<0)
						unInvoke();
				}
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition((int)(affectableStats.disposition()&(PhyStats.ALLMASK-PhyStats.IS_FLYING)));
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
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to break <S-HIS-HER> way free of the burning ensnarement."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB M=(MOB)affected;
			if((!M.amDead())&&(M.location()!=null))
			{
				CMLib.combat().postDamage(invoker,M,this,CMLib.dice().roll(2,4+super.getXLEVELLevel(invoker())+(2*super.getX1Level(invoker())),0),CMMsg.TYP_FIRE,-1,"<T-NAME> get(s) singed from <T-HIS-HER> flaming ensnarement!");
				if((!M.isInCombat())&&(M!=invoker)&&(M.location()!=null)&&(M.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,M)))
					CMLib.combat().postAttack(M,invoker,M.fetchWieldedItem());
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth ensnaring."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers at the ground.^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					if((mob.location().okMessage(mob,msg))&&(target.fetchEffect(this.ID())==null))
					{
						mob.location().send(mob,msg);
						if(msg.value()<=0)
						{
							amountRemaining=60;
							if(target.location()==mob.location())
							{
								success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
								target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) ensnared in the flaming tendrils erupting from the ground, and is unable to move <S-HIS-HER> feet!"));
							}
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
