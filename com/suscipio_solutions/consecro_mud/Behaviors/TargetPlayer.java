package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.HashSet;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class TargetPlayer extends ActiveTicker
{
	@Override public String ID(){return "TargetPlayer";}
	@Override protected int canImproveCode() {return Behavior.CAN_MOBS;}

	public TargetPlayer()
	{
		super();
		minTicks=3; maxTicks=12; chance=100;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "hero targeting";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(canAct(ticking,tickID))
		{
			final MOB mob = (MOB) ticking;
			if (mob.getVictim() != null)
			{
				final Set<MOB> theBadGuys = mob.getVictim().getGroupMembers(new HashSet<MOB>());
				MOB shouldFight = null;
				for (final Object element : theBadGuys)
				{
					final MOB consider = (MOB) element;
					if (consider.isMonster())
						continue;
					if (shouldFight == null)
					{
						shouldFight = consider;
					}
					else
					{
						if (((shouldFight.phyStats()!=null)&&(consider.phyStats()!=null))
						&&(shouldFight.phyStats().level() > consider.phyStats().level()))
							shouldFight = consider;
					}
				}
				if(shouldFight!=null)
				{
					if(shouldFight.equals(mob.getVictim()))
						return true;
					else
					if(CMLib.flags().canBeSeenBy(shouldFight,mob))
					{
						mob.setVictim(shouldFight);
					}
				}
			}
			return true;
		}
		return true;
	}
}
