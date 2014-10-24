package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class WimpyAggressive extends Aggressive
{
	@Override public String ID(){return "WimpyAggressive";}
	@Override public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE|Behavior.FLAG_TROUBLEMAKING;}

	public WimpyAggressive()
	{
		super();

		tickWait = 0;
		tickDown = 0;
	}

	@Override
	public String accountForYourself()
	{
		if(getParms().trim().length()>0)
			return "wimpy aggression against "+CMLib.masking().maskDesc(getParms(),true).toLowerCase();
		else
			return "wimpy aggressiveness";
	}

	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		return ((M!=null)&&(CMLib.flags().isSleeping(M)))&&
			CMLib.masking().maskCheck(getParms(),M,false);
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		tickDown=tickWait;
	}

	public static void pickAWimpyFight(MOB observer, boolean mobKiller, boolean misBehave, String attackMsg, String zapStr)
	{
		if(!canFreelyBehaveNormal(observer)) return;
		final Room R=observer.location();
		if(R!=null)
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB mob=R.fetchInhabitant(i);
			if((mob!=null)
			&&(mob!=observer)
			&&(CMLib.flags().isSleeping(mob))
			&&(CMLib.masking().maskCheck(zapStr,observer,false)))
			{
				startFight(observer,mob,mobKiller,misBehave,attackMsg);
				if(observer.isInCombat()) break;
			}
		}
	}

	public static void tickWimpyAggressively(Tickable ticking, boolean mobKiller, boolean misBehave, int tickID, String attackMsg, String zapStr)
	{
		if(tickID!=Tickable.TICKID_MOB) return;
		if(ticking==null) return;
		if(!(ticking instanceof MOB)) return;

		pickAWimpyFight((MOB)ticking,mobKiller,misBehave,attackMsg,zapStr);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID!=Tickable.TICKID_MOB) return true;
		if((--tickDown)<0)
		{
			tickDown=tickWait;
			tickWimpyAggressively(ticking,mobkill,misbehave,tickID,attackMessage,getParms());
		}
		return true;
	}
}
