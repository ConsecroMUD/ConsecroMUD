package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_SparringRoom extends Property
{
	@Override public String ID() { return "Prop_SparringRoom"; }
	@Override public String name(){ return "Player Death Neutralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_MOBS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(msg.tool() instanceof Amputator)
			return false;
		if(msg.sourceMinor()==CMMsg.TYP_FLEE)
			return false;
		if(msg.sourceMinor()==CMMsg.TYP_RECALL)
		{
			msg.source().tell(L("Noone hears your plea."));
			return false;
		}
		if((msg.sourceMinor()==CMMsg.TYP_DEATH)
		&&(!msg.source().isMonster())
		&&(msg.source().isInCombat()))
		{
			MOB source=null;
			if((msg.tool()!=null)&&(msg.tool() instanceof MOB))
				source=(MOB)msg.tool();
			final MOB target=msg.source();
			final Room deathRoom=target.location();
			deathRoom.show(source,source,CMMsg.MSG_OK_VISUAL,msg.sourceMessage());
			if(source!=null)
			{
				final CharClass combatCharClass=CMLib.combat().getCombatDominantClass(source,target);
				final Set<MOB> beneficiaries=CMLib.combat().getCombatBeneficiaries(source,target,combatCharClass);
				final Set<MOB> dividers=CMLib.combat().getCombatDividers(source,target,combatCharClass);
				CMLib.combat().dispenseExperience(beneficiaries,dividers,target);
			}
			target.makePeace();
			target.setRiding(null);
			for(int a=target.numEffects()-1;a>=0;a--) // personal effects
			{
				final Ability A=target.fetchEffect(a);
				if(A!=null) A.unInvoke();
			}
			target.setLocation(null);
			while(target.numFollowers()>0)
			{
				final MOB follower=target.fetchFollower(0);
				if(follower!=null)
				{
					follower.setFollowing(null);
					target.delFollower(follower);
				}
			}
			target.setFollowing(null);
			Room R=null;
			if(text().trim().length()>0)
				R=CMLib.map().getRoom(text().trim());
			if(R==null) R=target.getStartRoom();
			R.bringMobHere(target,false);
			target.bringToLife(R,true);
			target.location().showOthers(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
			deathRoom.recoverRoomStats();
			return false;
		}
		return true;
	}
}
