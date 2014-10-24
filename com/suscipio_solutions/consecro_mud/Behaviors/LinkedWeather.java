package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class LinkedWeather extends StdBehavior
{
	@Override public String ID(){return "LinkedWeather";}
	@Override protected int canImproveCode(){return Behavior.CAN_AREAS;}

	protected long lastWeather=-1;
	protected long lastPending=-1;
	protected String areaName=null;
	protected boolean rolling=false;

	@Override
	public String accountForYourself()
	{
		return "weather event linking";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Tickable.TICKID_AREA) return true;
		if(!(ticking instanceof Area)) return true;
		if(areaName==null)
		{
			if(getParms().length()==0)
				return true;
			String s=getParms();
			final int x=s.indexOf(';');
			rolling=false;
			if(x>=0)
			{
				if(s.indexOf("ROLL",x+1)>=0)
				   rolling=true;
				s=s.substring(0,x);
			}
			final Area A=CMLib.map().getArea(s);
			if(A!=null) areaName=A.Name();
		}

		final Area A=(Area)ticking;
		final Area linkedA=CMLib.map().getArea(areaName);
		if(linkedA!=null)
		{
			if(rolling)
				A.getClimateObj().setNextWeatherType(linkedA.getClimateObj().weatherType(null));
			else
			{
				A.getClimateObj().setCurrentWeatherType(linkedA.getClimateObj().weatherType(null));
				A.getClimateObj().setNextWeatherType(linkedA.getClimateObj().nextWeatherType(null));
			}
		}
		return true;
	}
}
