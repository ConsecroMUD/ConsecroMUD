package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GoodExecutioner  extends StdBehavior
{
	@Override public String ID(){return "GoodExecutioner";}
	@Override public long flags(){return Behavior.FLAG_POTENTIALLYAGGRESSIVE;}
	private boolean doPlayers=false;
	private boolean norecurse=false;
	protected long deepBreath=System.currentTimeMillis();
	private final DVector protectedOnes = new DVector(2);

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
		return "aggression to evilness and thieves";
	}

	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		if(norecurse) return false;
		norecurse=true;
		try
		{
			if(M==null) return false;
			if(CMLib.flags().isBoundOrHeld(M)) return false;
			if(((!M.isMonster())&&(!doPlayers)))
				return false;
			final List<Behavior> V=CMLib.flags().flaggedBehaviors(M,Behavior.FLAG_POTENTIALLYAGGRESSIVE);
			if((V!=null)
			&&((V.size()>1)
				||((V.size()==1)&&(!V.get(0).ID().equals(ID())))))
					return true;
			
			return ((CMLib.flags().isEvil(M))||(M.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Thief")));
		}
		finally{ norecurse=false;}
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
		if(msg.sourceMinor()==CMMsg.TYP_LIFE)
		{
			final MOB observer=(MOB)affecting;
			if((observer.getVictim() == msg.source())
			||(msg.source().getVictim() == observer))
				observer.makePeace();
			synchronized(protectedOnes)
			{
				final int x = protectedOnes.indexOf(msg.source().Name());
				if(x>=0)
					protectedOnes.setElementAt(x, 2, Long.valueOf(System.currentTimeMillis()));
				else
					protectedOnes.addElement(msg.source().Name(),Long.valueOf(System.currentTimeMillis()));
			}
		}
		if((deepBreath==0)||(System.currentTimeMillis()-deepBreath)>6000)
		{
			synchronized(protectedOnes)
			{
				for(int p=protectedOnes.size()-1;p>=0;p--)
				{
					if((System.currentTimeMillis()-((Long)protectedOnes.elementAt(p, 2)).longValue())>(30 * 1000))
						protectedOnes.removeElementAt(p);
				}
				if(protectedOnes.contains(msg.source().Name()))
					return;
			}
			deepBreath=0;
			final MOB observer=(MOB)affecting;
			// base 90% chance not to be executed
			if((source.isMonster()||doPlayers)
			&&(source!=observer)
			&&(grantsAggressivenessTo(source)))
			{
				String reason="EVIL";
				if(source.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Thief"))
					reason="A THIEF";
				final MOB oldFollowing=source.amFollowing();
				source.setFollowing(null);
				final boolean yep=Aggressive.startFight(observer,source,true,false,source.name().toUpperCase()+" IS "+reason+", AND MUST BE DESTROYED!");
				if(!yep)
				if(oldFollowing!=null)
					source.setFollowing(oldFollowing);
			}
		}
	}
}
