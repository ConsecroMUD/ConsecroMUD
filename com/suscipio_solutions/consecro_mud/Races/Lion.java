package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;

public class Lion extends GreatCat
{
	@Override public String ID(){	return "Lion"; }
	@Override public String name(){ return "Lion"; }
	@Override public String racialCategory(){return "Feline";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,0 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}


	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("a "+name().toLowerCase()+" mane",RawMaterial.RESOURCE_FUR));
				for(int i=0;i<4;i++)
					resources.addElement(makeResource
					("a strip of "+name().toLowerCase()+" fur",RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource
				("a pound of "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
