package com.suscipio_solutions.consecro_mud.Areas;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class Moon extends StdThinPlanet
{
	@Override public String ID(){ return "Moon";}
	public Moon()
	{
		super();

		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.MoonRadius.dm + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.MoonRadius.dm,0.20)));
		//TODO: need a behavior or something that "fills it out" first time it's hit.
	}
}
