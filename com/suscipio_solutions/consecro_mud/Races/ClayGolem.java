package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class ClayGolem extends StoneGolem
{
	@Override public String ID(){	return "ClayGolem"; }
	@Override public String name(){ return "Clay Golem"; }
	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
					("a pound of clay",RawMaterial.RESOURCE_CLAY));
				resources.addElement(makeResource
					("essence of golem",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}

}
