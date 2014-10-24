package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class GoodGuardian extends StdBehavior
{
	@Override public String ID(){return "GoodGuardian";}

	protected long deepBreath=System.currentTimeMillis();

	@Override
	public String accountForYourself()
	{
		return "protective against aggression, evilness, or thieflyness";
	}

	public static MOB anyPeaceToMake(Room room, MOB observer)
	{
		if(room==null) return null;
		MOB victim=null;
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB inhab=room.fetchInhabitant(i);
			if((inhab!=null)&&(inhab.isInCombat()))
			{
				if(inhab.isMonster())
					for(final Enumeration<Behavior> e=inhab.behaviors();e.hasMoreElements();)
					{
						final Behavior B=e.nextElement();
						if((B!=null)&&(B.grantsAggressivenessTo(inhab.getVictim())))
							return inhab;
					}

				if((BrotherHelper.isBrother(inhab,observer,false))&&(victim==null))
					victim=inhab.getVictim();

				if((CMLib.flags().isEvil(inhab))
				||(inhab.charStats().getCurrentClass().baseClass().equalsIgnoreCase("Thief")))
					victim=inhab;
			}
		}
		return victim;
	}

	public static void keepPeace(MOB observer, MOB victim)
	{
		if(!canFreelyBehaveNormal(observer)) return;

		if(victim!=null)
		{
			final MOB victimVictim=victim.getVictim();
			if((!BrotherHelper.isBrother(victim,observer,false))
			&&(victimVictim!=null)
			&&(!victim.amDead())
			&&(victim.isInCombat())
			&&(!victimVictim.amDead())
			&&(victimVictim.isInCombat()))
			{
				Aggressive.startFight(observer,victim,true,false,"PROTECT THE INNOCENT!");
			}
		}
		else
		{
			final Room room=observer.location();
			for(int i=0;i<room.numInhabitants();i++)
			{
				final MOB inhab=room.fetchInhabitant(i);
				if((inhab!=null)
				&&(inhab.isInCombat())
				&&(inhab.getVictim().isInCombat())
				&&((observer.phyStats().level()>(inhab.phyStats().level()+5))))
				{
					final String msg="<S-NAME> stop(s) <T-NAME> from fighting with "+inhab.getVictim().name();
					final CMMsg msgs=CMClass.getMsg(observer,inhab,CMMsg.MSG_NOISYMOVEMENT,msg);
					if(observer.location().okMessage(observer,msgs))
					{
						observer.location().send(observer,msgs);
						final MOB ivictim=inhab.getVictim();
						if(ivictim!=null) ivictim.makePeace();
						inhab.makePeace();
					}
				}
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(!canFreelyBehaveNormal(ticking))
		{
			deepBreath=System.currentTimeMillis();
			return true;
		}
		if((deepBreath==0)||(System.currentTimeMillis()-deepBreath)>6000)
		{
			deepBreath=0;
			final MOB mob=(MOB)ticking;
			final MOB victim=anyPeaceToMake(mob.location(),mob);
			keepPeace(mob,victim);
		}
		return true;
	}
}
