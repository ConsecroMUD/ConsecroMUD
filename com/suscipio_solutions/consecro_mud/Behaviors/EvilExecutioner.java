package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class EvilExecutioner  extends StdBehavior
{
	@Override public String ID(){return "EvilExecutioner";}
	@Override public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE;}
	protected boolean doPlayers=false;
	protected long deepBreath=System.currentTimeMillis();
	protected boolean noRecurse=true;

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		newParms=newParms.toUpperCase();
		final Vector<String> V=CMParms.parse(newParms);
		doPlayers=V.contains("PLAYERS")||V.contains("PLAYER");
	}

	@Override
	public String accountForYourself()
	{
		return "aggression to goodness and paladins";
	}

	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		if(M==null) return false;
		if(CMLib.flags().isBoundOrHeld(M)) return false;
		if((!M.isMonster())&&(!doPlayers))
			return false;
		for(final Enumeration<Behavior> e=M.behaviors();e.hasMoreElements();)
		{
			final Behavior B=e.nextElement();
			if((B!=null)&&(B!=this)&&(B.grantsAggressivenessTo(M)))
				return true;
		}
		return ((CMLib.flags().isGood(M))||(M.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Paladin")));
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		final MOB source=msg.source();
		if(!canFreelyBehaveNormal(affecting))
		{
			deepBreath=System.currentTimeMillis();
			return;
		}
		if((deepBreath==0)||((System.currentTimeMillis()-deepBreath)>60000)&&(!noRecurse))
		{
			noRecurse=true;
			deepBreath=0;
			final MOB observer=(MOB)affecting;
			// base 90% chance not to be executed
			if((source.isMonster()||doPlayers)&&(source!=observer)&&(grantsAggressivenessTo(source)))
			{
				String reason="GOOD";
				if(source.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Paladin"))
					reason="A PALADIN";
				final MOB oldFollowing=source.amFollowing();
				source.setFollowing(null);
				final boolean yep=Aggressive.startFight(observer,source,true,false,source.name().toUpperCase()+" IS "+reason+", AND MUST BE DESTROYED!");
				if(!yep)
				if(oldFollowing!=null)
					source.setFollowing(oldFollowing);
			}
			noRecurse=false;
		}
	}
}
