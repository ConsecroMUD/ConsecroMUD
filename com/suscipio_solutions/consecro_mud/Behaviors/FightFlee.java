package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class FightFlee extends ActiveTicker
{
	@Override public String ID(){return "FightFlee";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	public FightFlee()
	{
		super();
		minTicks=1;maxTicks=1;chance=33;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "cowardly fighting and fleeing";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			final MOB mob=(MOB)ticking;
			if(mob.isInCombat()
			   &&(mob.getVictim()!=null)
			   &&(mob.getVictim().getVictim()==mob))
				CMLib.commands().postFlee(mob,"");
		}
		return true;
	}
}
