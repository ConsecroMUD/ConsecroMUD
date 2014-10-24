package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class GasGiant extends GenSpaceBody
{
	@Override public String ID(){	return "GasGiant";}
	
	public GasGiant()
	{
		super();
		setName("unknown gas giant");
		setDisplayText("an unknown gas giant is floating here");
		setDescription("it`s pretty");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.SaturnRadius.dm + (random.nextLong() % (SpaceObject.Distance.SaturnRadius.dm / 2));
		this.setMaterial(RawMaterial.RESOURCE_HYDROGEN);
	}
}
