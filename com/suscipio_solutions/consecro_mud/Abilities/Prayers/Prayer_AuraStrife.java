package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Prayer_AuraStrife extends Prayer
{
	@Override public String ID() { return "Prayer_AuraStrife"; }
	private final static String localizedName = CMLib.lang().L("Aura of Strife");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Aura of Strife)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if((invoker()!=null)&&(affected!=invoker())&&(CMLib.flags().isEvil(invoker())))
		{
			affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)-(adjustedLevel(invoker(),0)/5));
			if(affectableStats.getStat(CharStats.STAT_CHARISMA)<=0)
				affectableStats.setStat(CharStats.STAT_CHARISMA,1);
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.sourceMinor()==CMMsg.TYP_QUIT))
			unInvoke();
		else
		if(msg.sourceMinor()==CMMsg.TYP_SHUTDOWN)
			unInvoke();
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB M=(MOB)affected;

		super.unInvoke();

		if((canBeUninvoked())&&(M!=null)&&(!M.amDead())&&(M.location()!=null))
			M.location().show(M,null,CMMsg.MSG_OK_VISUAL,L("The aura of strife around <S-NAME> fades."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((tickID==Tickable.TICKID_MOB)
		&&(invoker()!=null)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			final Set<MOB> invokerGroup=invoker().getGroupMembers(new HashSet<MOB>());
			if(mob!=invoker())
			{
				if(mob.location()!=invoker().location())
					unInvoke();
				else
				{
					if(invokerGroup.contains(mob))
						unInvoke();
					else
					if(mob.isInCombat())
					{
						int levels=invoker().charStats().getClassLevel("Templar");
						if(levels<0) levels=invoker().phyStats().level();
						if(CMLib.dice().rollPercentage()>=levels)
						{
							final MOB newvictim=mob.location().fetchRandomInhabitant();
							if(newvictim!=mob) mob.setVictim(newvictim);
						}
					}
				}
			}
			else
			if((mob.location()!=null)&&(CMLib.flags().isEvil(invoker())))
			for(int m=0;m<mob.location().numInhabitants();m++)
			{
				final MOB M=mob.location().fetchInhabitant(m);
				if((M!=null)&&(M!=invoker())&&(!invokerGroup.contains(M))&&(!M.Name().equals(mob.getLiegeID())))
					beneficialAffect(invoker,M,0,Ability.TICKS_FOREVER);
			}
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		final Room targetRoom=target.location();
		if(targetRoom==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			beneficialAffect(mob,target,asLevel,0);
			target.recoverPhyStats();
			targetRoom.recoverRoomStats();
		}
		// return whether it worked
		return success;
	}

	@Override
	public boolean autoInvocation(MOB mob)
	{
		if(mob.charStats().getCurrentClass().ID().equals("Immortal"))
			return false;
		return super.autoInvocation(mob);
	}
}
