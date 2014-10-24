package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;

public class StdFactoryMOB extends StdMOB
{
	@Override public String ID(){return "StdFactoryMOB";}

	@Override
	public CMObject newInstance()
	{
		try
		{
			return this.getClass().newInstance();
		}
		catch(final Exception e)
		{
			Log.errOut(ID(),e);
		}
		return new StdFactoryMOB();
	}

	@Override
	protected void finalize() throws Throwable
	{
		if(!amDestroyed)
			destroy();
		amDestroyed=false;
		if(!CMClass.returnMob(this))
		{
			amDestroyed=true;
			super.finalize();
		}
	}

	@Override
	public void destroy()
	{
		try
		{
			CharStats savedCStats=charStats;
			if(charStats==baseCharStats)
				savedCStats=(CharStats)CMClass.getCommon("DefaultCharStats");
			PhyStats savedPStats=phyStats;
			if(phyStats==basePhyStats)
				savedPStats=(PhyStats)CMClass.getCommon("DefaultPhyStats");
			final CharState savedCState=curState;
			if((curState==baseState)||(curState==maxState))
				curState=(CharState)CMClass.getCommon("DefaultCharState");
			super.destroy();
			removeFromGame=false;
			charStats=savedCStats;
			phyStats=savedPStats;
			curState=savedCState;
			baseCharStats.reset();
			basePhyStats.reset();
			baseState.reset();
			maxState.reset();
			curState.reset();
			phyStats.reset();
			charStats.reset();
			finalize();
		}
		catch(final Throwable t)
		{
			Log.errOut(ID(),t);
		}
	}
}
