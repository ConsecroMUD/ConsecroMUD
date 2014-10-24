package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class BrotherHelper extends StdBehavior
{
	@Override public String ID(){return "BrotherHelper";}

	//protected boolean mobKiller=false;
	protected boolean nameOnly = true;
	protected int num=-1;

	@Override
	public String accountForYourself()
	{
		return "neighbor protecting";
	}

	@Override
	public void setParms(String parms)
	{
		super.setParms(parms);
		nameOnly=parms.toUpperCase().indexOf("NAMEONLY")>=0;
		num=-1;
	}

	public int numAllowed()
	{
		if(num<0)
		{
			num=0;
			final Vector<String> V=CMParms.parse(getParms());
			for(int v=0;v<V.size();v++)
				if(CMath.isInteger(V.elementAt(v)))
					num=CMath.s_int(V.elementAt(v));

		}
		return num;
	}

	public static boolean isBrother(MOB target, MOB observer, boolean nameOnly)
	{
		if((observer==null)||(target==null)) return false;
		if(!nameOnly)
		{
			if((observer.getStartRoom()!=null)&&(target.getStartRoom()!=null))
			{
				if (observer.getStartRoom() == target.getStartRoom())
					return true;
			}
		}
		if((observer.ID().equals(target.ID()))&&(observer.name().equals(target.name())))
			return true;
		return false;
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB source=msg.source();
		final MOB observer=(MOB)affecting;
		final MOB target=(MOB)msg.target();

		final Room R=source.location();
		if((source!=observer)
		&&(target!=observer)
		&&(source!=target)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(!observer.isInCombat())
		&&(CMLib.flags().canBeSeenBy(source,observer))
		&&(CMLib.flags().canBeSeenBy(target,observer))
		&&(isBrother(target,observer,nameOnly))
		&&(!isBrother(source,observer,nameOnly))
		&&(R!=null))
		{
			int numInFray=0;
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB M=R.fetchInhabitant(m);
				if((M!=null)&&(M.getVictim()==source))
					numInFray++;
			}
			boolean yep=true;
			if(CMLib.law().isLegalOfficerHere(observer))
			{
				yep=false;
				if(CMLib.law().isLegalOfficialHere(target))
					yep=true;
				else
				if(!CMLib.flags().isAggressiveTo(target,source))
					yep=true;
			}
			if(yep&&((numAllowed()==0)||(numInFray<numAllowed())))
			{
				yep=Aggressive.startFight(observer,source,true,false,"DON'T HURT MY FRIEND!");
			}
		}
	}

}
