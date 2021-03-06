package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public class Ape extends Monkey
{
	@Override public String ID(){	return "Ape"; }
	@Override public String name(){ return "Ape"; }
	@Override public int shortestMale(){return 52;}
	@Override public int shortestFemale(){return 50;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 150;}
	@Override public int weightVariance(){return 80;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Primate";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setRacialStat(CharStats.STAT_STRENGTH,16);
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY,15);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE,1);
	}
	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				for(int i=0;i<3;i++)
					resources.addElement(makeResource
					("a strip of "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource
				("an "+name().toLowerCase()+" nose",RawMaterial.RESOURCE_HIDE));
				for(int i=0;i<3;i++)
					resources.addElement(makeResource
					("a pound of "+name().toLowerCase()+" flesh",RawMaterial.RESOURCE_MEAT));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
