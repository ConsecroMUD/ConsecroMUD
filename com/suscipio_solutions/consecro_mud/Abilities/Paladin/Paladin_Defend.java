package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Paladin_Defend extends StdAbility
{
	@Override public String ID() { return "Paladin_Defend"; }
	private final static String localizedName = CMLib.lang().L("All Defence");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"DEFENCE"});
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_EVASIVE;}
	public boolean fullRound=false;
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof MOB))||(invoker==null))
			return true;

		final MOB mob=(MOB)affected;
		if(invoker.location()!=mob.location())
			unInvoke();
		else
		{
			// preventing distracting player from doin anything else
			if(msg.amISource(invoker)
			&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK))
			{
				invoker.location().show((MOB)affected,msg.target(),CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> defend(s) <S-HIM-HERSELF> against <T-NAME>."));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected==null)||(!(affected instanceof MOB))||(invoker==null))
			return;
		if((msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon))
			fullRound=false;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setArmor(affectableStats.armor() - 20 -(4*getXLEVELLevel(invoker())));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==Tickable.TICKID_MOB)
		{
			if(fullRound)
			{
				final MOB mob=(MOB)affected;
				if(!mob.isInCombat())
					unInvoke();
				if(mob.location()!=null)
				{
					if(mob.location().show(mob,null,this,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> successful defence <S-HAS-HAVE> allowed <S-HIM-HER> to disengage.")))
					{
						final MOB victim=mob.getVictim();
						if((victim!=null)&&(victim.getVictim()==mob))
							victim.makePeace();
						mob.makePeace();
						unInvoke();
					}
				}
			}
			fullRound=true;
		}
		return true;
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!CMLib.flags().aliveAwakeMobile(mob,false))
			return false;

		final Ability A=mob.fetchEffect(ID());
		if(A!=null)
		{
			A.unInvoke();
			mob.tell(L("You end your all-out defensive posture."));
			return true;
		}
		if(!mob.isInCombat())
		{
			mob.tell(L("You must be in combat to defend!"));
			return false;
		}

		if((!auto)&&(!(CMLib.flags().isGood(mob))))
		{
			mob.tell(L("You don't feel worthy of a good defence."));
			return false;
		}
		if(!super.invoke(mob,commands,mob,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_CAST_SOMANTIC_SPELL,L("^S<S-NAME> assume(s) an all-out defensive posture.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				fullRound=false;
				beneficialAffect(mob,mob,asLevel,Ability.TICKS_FOREVER);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to assume an all-out defensive posture, but fail(s)."));


		// return whether it worked
		return success;
	}
}
