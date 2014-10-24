package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_StinkingCloud extends Spell
{
	@Override public String ID() { return "Spell_StinkingCloud"; }
	private final static String localizedName = CMLib.lang().L("Stinking Cloud");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(In the Stinking Cloud)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(3);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}

	Room castingLocation=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB M=(MOB)affected;
			if((M.location()!=castingLocation)||(M.amDead()))
				unInvoke();
			else
			if((!M.amDead())
			&&(M.location()!=null)
			&&(CMLib.flags().canSmell(M)))
			{
				final int damage= (M.phyStats().level()/2) + super.getXLEVELLevel(invoker);
				if((M.curState().getHunger()<=0))
					CMLib.combat().postDamage(invoker,M,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_GAS,-1,"<T-NAME> heave(s) in the stinking cloud.");
				else
				{
					CMLib.combat().postDamage(invoker,M,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_GAS,-1,"<T-NAME> heave(s) all over the place!");
					M.curState().adjHunger(-500,M.maxState().maxHunger(M.baseWeight()));
				}
				if((!M.isInCombat())&&(M.isMonster())&&(M!=invoker)&&(invoker!=null)&&(M.location()==invoker.location())&&(M.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,M)))
					CMLib.combat().postAttack(M,invoker,M.fetchWieldedItem());
			}
			else
				unInvoke();
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		   &&(affected instanceof MOB)
		   &&(msg.amISource((MOB)affected)))
		{
			final MOB mob=(MOB)affected;
			if(CMLib.flags().canSmell(mob))
				switch(msg.sourceMinor())
				{
				case CMMsg.TYP_ADVANCE:
					if(CMLib.dice().rollPercentage()>(mob.charStats().getSave(CharStats.STAT_SAVE_GAS)))
					{
						mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> double(s) over from the sickening gas."));
						return false;
					}
					break;
				}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)))
		{
			switch(msg.sourceMinor())
			{
			case CMMsg.TYP_ADVANCE:
				unInvoke();
				break;
			}
		}
		else
		if((msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_SNIFF)
		&&(CMLib.flags().canSmell(msg.source())))
			msg.source().tell(msg.source(),affected,null,L("<T-NAME> smell(s) nauseatingly stinky!"));
		super.executeMsg(myHost,msg);
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
		{
			if((!mob.amDead())&&(mob.location()!=null))
				mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> manage(s) to escape the stinking cloud!"));
		}
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Set<MOB> h=null;
		if(givenTarget instanceof MOB)
		{
			h=new HashSet();
			h.add((MOB)givenTarget);
		}
		else
			h=CMLib.combat().properTargets(this,mob,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth casting this on."));
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
			if(mob.location().show(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> incant(s) and wave(s) <S-HIS-HER> arms around.  A horrendous cloud of green and orange gas appears!^?")))
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
					final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_GAS|(auto?CMMsg.MASK_ALWAYS:0),null);
					if((mob.location().okMessage(mob,msg))
					   &&(mob.location().okMessage(mob,msg2))
					   &&(target.fetchEffect(this.ID())==null))
					{
						mob.location().send(mob,msg);
						mob.location().send(mob,msg2);
						if((msg.value()<=0)&&(msg2.value()<=0)&&(target.location()==mob.location()))
						{
							castingLocation=mob.location();
							success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
							target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> become(s) enveloped in the stinking cloud!"));
						}
					}
				}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> incant(s), but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
