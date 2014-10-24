package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Aggressive extends StdBehavior
{
	@Override public String ID(){return "Aggressive";}
	@Override public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE|Behavior.FLAG_TROUBLEMAKING;}
	protected int tickWait=0;
	protected int tickDown=0;
	protected boolean wander=false;
	protected boolean mobkill=false;
	protected boolean misbehave=false;
	protected String attackMessage=null;
	protected Room lastRoom=null;
	protected int lastRoomInhabCount=0;

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting, msg);
		if((msg.sourceMinor()==CMMsg.TYP_ENTER)||(msg.sourceMinor()==CMMsg.TYP_LEAVE))
			lastRoomInhabCount=-1;
	}

	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		if(M==null) return true;
		return CMLib.masking().maskCheck(getParms(),M,false);
	}

	@Override
	public String accountForYourself()
	{
		if(getParms().trim().length()>0)
			return "aggression against "+CMLib.masking().maskDesc(getParms(),true).toLowerCase();
		else
			return "aggressiveness";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		attackMessage=CMParms.getParmStr(newParms,"MESSAGE",null);
		final Vector<String> V=CMParms.parse(newParms.toUpperCase());
		wander=V.contains("WANDER");
		mobkill=V.contains("MOBKILL")||(V.contains("MOBKILLER"));
		misbehave=V.contains("MISBEHAVE");
		tickDown=tickWait;
	}

	public static boolean startFight(MOB monster, MOB mob, boolean fightMOBs, boolean misBehave, String attackMsg)
	{
		if((mob!=null)&&(monster!=null)&&(mob!=monster))
		{
			final Room R=monster.location();
			if((R!=null)
			&&((!mob.isMonster())||(fightMOBs))
			&&(R.isInhabitant(mob))
			&&(R.getArea().getAreaState()==Area.State.ACTIVE)
			&&((misBehave&&(!monster.isInCombat()))||canFreelyBehaveNormal(monster))
			&&(CMLib.flags().canBeSeenBy(mob,monster))
			&&(!CMSecurity.isAllowed(mob,R,CMSecurity.SecFlag.ORDER))
			&&(!CMSecurity.isAllowed(mob,R,CMSecurity.SecFlag.CMDROOMS))
			&&(!CMLib.flags().isATrackingMonster(mob))
			&&(!CMLib.flags().isATrackingMonster(monster))
			&&(!monster.getGroupMembers(new HashSet<MOB>()).contains(mob)))
			{
				// special backstab sneak attack!
				if(CMLib.flags().isHidden(monster))
				{
					final Ability A=monster.fetchAbility("Thief_BackStab");
					if(A!=null)
					{
						A.setProficiency(CMLib.dice().roll(1,50,A.adjustedLevel(mob,0)*15));
						monster.enqueCommand(new XVector(A.triggerStrings()[0],R.getContextName(mob)),Command.METAFLAG_FORCED,0);
					}
				}
				if((attackMsg!=null)&&(monster.getVictim()!=mob))
					monster.enqueCommand(new XVector("SAY",attackMsg),Command.METAFLAG_FORCED,0);
				// normal attack
				monster.enqueCommand(new XVector("KILL",R.getContextName(mob)),Command.METAFLAG_FORCED,0);
				return true;
			}
		}
		return false;
	}
	public boolean pickAFight(MOB observer, String zapStr, boolean mobKiller, boolean misBehave, String attackMsg)
	{
		if(!canFreelyBehaveNormal(observer)) return false;
		final Room R=observer.location();
		if((R!=null)&&(R.getArea().getAreaState()==Area.State.ACTIVE))
		{
			if((R!=lastRoom)||(lastRoomInhabCount!=R.numInhabitants()))
			{
				lastRoom=R;
				lastRoomInhabCount=R.numInhabitants();
				final Set<MOB> groupMembers=observer.getGroupMembers(new HashSet<MOB>());
				for(int i=0;i<R.numInhabitants();i++)
				{
					final MOB mob=R.fetchInhabitant(i);
					if((mob!=null)
					&&(mob!=observer)
					&&((!mob.isMonster())||(mobKiller))
					&&(CMLib.masking().maskCheck(zapStr,mob,false))
					&&(!groupMembers.contains(mob))
					&&(startFight(observer,mob,mobKiller,misBehave,attackMsg)))
						return true;
				}
			}
		}
		return false;
	}

	public void tickAggressively(Tickable ticking, int tickID, boolean mobKiller, boolean misBehave, String zapStr, String attackMsg)
	{
		if(tickID!=Tickable.TICKID_MOB) return;
		if(ticking==null) return;
		if(!(ticking instanceof MOB)) return;
		pickAFight((MOB)ticking,zapStr,mobKiller,misBehave,attackMsg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Tickable.TICKID_MOB) return true;
		if((--tickDown)<0)
		{
			tickDown=tickWait;
			tickAggressively(ticking,
							 tickID,
							 mobkill,
							 misbehave,
							 getParms(),
							 attackMessage);
		}
		return true;
	}
}
