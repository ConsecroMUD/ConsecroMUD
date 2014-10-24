package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class Moonlet extends GenSpaceBody
{
	@Override public String ID(){	return "Moonlet";}
	
	public Moonlet()
	{
		super();
		setName("a small moon");
		setDisplayText("a small moon is floating here");
		setDescription("it`s not a space station");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=(SpaceObject.Distance.MoonRadius.dm/10) + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.MoonRadius.dm,0.099)));
		recoverPhyStats();
	}
}
