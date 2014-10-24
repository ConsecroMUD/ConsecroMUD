package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class ActiveTicker extends StdBehavior
{
	@Override public String ID(){return "ActiveTicker";}
	@Override protected int canImproveCode(){return Behavior.CAN_ITEMS|Behavior.CAN_MOBS|Behavior.CAN_ROOMS|Behavior.CAN_EXITS|Behavior.CAN_AREAS;}

	protected int minTicks=10;
	protected int maxTicks=30;
	protected int chance=100;
	//protected short speed=1;
	protected int tickDown=(int)Math.round(Math.random()*(maxTicks-minTicks))+minTicks;

	protected void tickReset()
	{
		tickDown=(int)Math.round(Math.random()*(maxTicks-minTicks))+minTicks;
	}

	@Override
	public void setParms(String newParms)
	{
		parms=newParms;
		minTicks=CMParms.getParmInt(parms,"min",minTicks);
		maxTicks=CMParms.getParmInt(parms,"max",maxTicks);
		chance=CMParms.getParmInt(parms,"chance",chance);
		tickReset();
	}

	public String rebuildParms()
	{
		final StringBuffer rebuilt=new StringBuffer("");
		rebuilt.append(" min="+minTicks);
		rebuilt.append(" max="+maxTicks);
		rebuilt.append(" chance="+chance);
		return rebuilt.toString();
	}

	public String getParmsNoTicks()
	{
		String parms=getParms();
		char c=';';
		int x=parms.indexOf(c);
		if(x<0){ c='/'; x=parms.indexOf(c);}
		if(x>0)
		{
			if((x+1)>parms.length())
				return "";
			parms=parms.substring(x+1);
		}
		else
		{
			return "";
		}
		return parms;
	}

	protected boolean canAct(Tickable ticking, int tickID)
	{
		switch(tickID)
		{
		case Tickable.TICKID_AREA:
			if(!(ticking instanceof Area))
				break;
		//$FALL-THROUGH$
		case Tickable.TICKID_MOB:
		case Tickable.TICKID_ITEM_BEHAVIOR:
		case Tickable.TICKID_ROOM_BEHAVIOR:
		{
			if((--tickDown)<1)
			{
				tickReset();
				if((ticking instanceof MOB)&&(!canActAtAll(ticking)))
					return false;
				final int a=CMLib.dice().rollPercentage();
				if(a>chance)
					return false;
				return true;
			}
			break;
		}
		default:
			break;
		}
		return false;
	}
}
