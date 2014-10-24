package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class DwarfStar extends GenSpaceBody
{
	@Override public String ID(){	return "DwarfStar";}
	
	public DwarfStar()
	{
		super();
		setName("unknown dwarf star");
		setDisplayText("an unknown dwarf star is shining here");
		setDescription("it`s somewhat bright");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.StarDRadius.dm + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.StarDRadius.dm,0.30)));
		basePhyStats().setDisposition(PhyStats.IS_LIGHTSOURCE|PhyStats.IS_GLOWING);
		this.setMaterial(RawMaterial.RESOURCE_HYDROGEN);
		recoverPhyStats();
	}
}
