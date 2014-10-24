package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;

public class Kitten extends Cat
{
	@Override public String ID(){	return "Kitten"; }
	@Override public String name(){ return "Kitten"; }
	@Override public int shortestMale(){return 4;}
	@Override public int shortestFemale(){return 4;}
	@Override public int heightVariance(){return 3;}
	@Override public int lightestWeight(){return 7;}
	@Override public int weightVariance(){return 10;}
	@Override public long forbiddenWornBits(){return ~(Wearable.WORN_HEAD|Wearable.WORN_FEET|Wearable.WORN_EARS|Wearable.WORN_EYES);}
	@Override public String racialCategory(){return "Feline";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,0 ,0 ,1 ,4 ,4 ,1 ,0 ,1 ,1 ,1 ,0 };
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
				("a "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_FUR));
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
			}
		}
		return resources;
	}

	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
			case Race.AGE_CHILD:
				switch(gender)
				{
				case 'M': case 'm': return "boy kitten";
				case 'F': case 'f': return "girl kitten";
				default: return "kitten";
				}
			case Race.AGE_YOUNGADULT:
			case Race.AGE_MATURE:
			case Race.AGE_MIDDLEAGED:
			default:
				switch(gender)
				{
				case 'M': case 'm': return "male cat";
				case 'F': case 'f': return "female cat";
				default: return "cat";
				}
			case Race.AGE_OLD:
			case Race.AGE_VENERABLE:
			case Race.AGE_ANCIENT:
				switch(gender)
				{
				case 'M': case 'm': return "old male cat";
				case 'F': case 'f': return "old female cat";
				default: return "old cat";
				}
		}
	}
}
