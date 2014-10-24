package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class MassiveStar extends GenSpaceBody
{
	@Override public String ID(){	return "MassiveStar";}
	
	public MassiveStar()
	{
		super();
		setName("unknown massive star");
		setDisplayText("an unknown massive star is shining here");
		setDescription("it`s very very bright");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.StarBRadius.dm + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.StarBRadius.dm,0.30)));
		basePhyStats().setDisposition(PhyStats.IS_LIGHTSOURCE|PhyStats.IS_GLOWING);
		this.setMaterial(RawMaterial.RESOURCE_HYDROGEN);
		recoverPhyStats();
	}
}
