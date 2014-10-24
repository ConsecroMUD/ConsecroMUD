package com.suscipio_solutions.consecro_mud.Areas;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class StdTimeZone extends StdArea
{
	@Override public String ID(){	return "StdTimeZone";}

	public StdTimeZone()
	{
		super();

		myClock = (TimeClock)CMClass.getCommon("DefaultTimeClock");
	}

	@Override
	public CMObject copyOf()
	{
		final CMObject O=super.copyOf();
		if(O instanceof Area) ((Area)O).setTimeObj((TimeClock)CMClass.getCommon("DefaultTimeClock"));
		return O;
	}

	@Override public TimeClock getTimeObj(){return myClock;}
	@Override
	public void setName(String newName)
	{
		super.setName(newName);
		myClock.setLoadName(newName);
	}

	@Override
	public void addChild(Area area)
	{
		super.addChild(area);
		area.setTimeObj(getTimeObj());
		for(final Enumeration<Area> cA=area.getChildren();cA.hasMoreElements();)
			cA.nextElement().setTimeObj(getTimeObj());
	}
}
