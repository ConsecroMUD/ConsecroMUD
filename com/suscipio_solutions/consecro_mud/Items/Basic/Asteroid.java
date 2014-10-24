package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class Asteroid extends GenSpaceBody
{
	@Override public String ID(){	return "Asteroid";}
	
	public Asteroid()
	{
		super();
		setName("an asteroid");
		setDisplayText("an asteroid is here");
		setDescription("it`s a big rock");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=(5*SpaceObject.Distance.Kilometer.dm) + (random.nextLong() % (4*SpaceObject.Distance.Kilometer.dm));
		recoverPhyStats();
	}
}
