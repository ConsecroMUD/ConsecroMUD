package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class Star extends GenSpaceBody
{
	@Override public String ID(){	return "Star";}
	
	public Star()
	{
		super();
		setName("unknown star");
		setDisplayText("an unknown star is shining here");
		setDescription("it`s very bright");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.StarGRadius.dm + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.StarGRadius.dm,0.30)));
		basePhyStats().setDisposition(PhyStats.IS_LIGHTSOURCE|PhyStats.IS_GLOWING);
		recoverPhyStats();
		this.setMaterial(RawMaterial.RESOURCE_HYDROGEN);
	}
}
