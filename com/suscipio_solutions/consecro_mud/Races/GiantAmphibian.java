package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;


public class GiantAmphibian extends GreatAmphibian
{
	@Override public String ID(){	return "GiantAmphibian"; }
	@Override public String name(){ return "Giant Amphibian"; }
	@Override public int shortestMale(){return 50;}
	@Override public int shortestFemale(){return 55;}
	@Override public int heightVariance(){return 20;}
	@Override public int lightestWeight(){return 1955;}
	@Override public int weightVariance(){return 405;}
	@Override public long forbiddenWornBits(){return ~(Wearable.WORN_EYES);}
	@Override public String racialCategory(){return "Amphibian";}
	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<25;i++)
				resources.addElement(makeResource
				("some "+name().toLowerCase(),RawMaterial.RESOURCE_FISH));
				for(int i=0;i<15;i++)
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_HIDE));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}
}
