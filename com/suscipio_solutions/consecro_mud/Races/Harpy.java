package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public class Harpy extends GreatBird
{
	@Override public String ID(){	return "Harpy"; }
	@Override public String name(){ return "Harpy"; }
	@Override public int shortestMale(){return 59;}
	@Override public int shortestFemale(){return 59;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 160;}
	@Override public int weightVariance(){return 80;}
	@Override public long forbiddenWornBits(){return Wearable.WORN_HELD|Wearable.WORN_WIELD|Wearable.WORN_FEET;}
	@Override public String racialCategory(){return "Avian";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,0 ,0 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,1 ,2 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,1,4,20,50,75,100,110,120};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{ affectableStats.setStat(CharStats.STAT_GENDER,'F');}

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some greasy "+name().toLowerCase()+" claws",RawMaterial.RESOURCE_BONE));
				for(int i=0;i<2;i++)
					resources.addElement(makeResource
					("some dirty "+name().toLowerCase()+" feathers",RawMaterial.RESOURCE_FEATHERS));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_POULTRY));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
