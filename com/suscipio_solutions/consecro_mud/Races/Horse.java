package com.suscipio_solutions.consecro_mud.Races;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;


public class Horse extends Equine
{
	@Override public String ID(){	return "Horse"; }
	@Override public String name(){ return "Horse"; }


	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
				return "foal";
			case Race.AGE_CHILD:
			case Race.AGE_YOUNGADULT:
				switch(gender)
				{
				case 'M': case 'm': return "colt";
				case 'F': case 'f': return "filly";
				default: return "young "+name().toLowerCase();
				}
			case Race.AGE_MATURE:
			case Race.AGE_MIDDLEAGED:
			default:
				switch(gender)
				{
				case 'M': case 'm': return "stud";
				case 'F': case 'f': return "stallion";
				default: return name().toLowerCase();
				}
			case Race.AGE_OLD:
			case Race.AGE_VENERABLE:
			case Race.AGE_ANCIENT:
				switch(gender)
				{
				case 'M': case 'm': return "old male "+name().toLowerCase();
				case 'F': case 'f': return "old female "+name().toLowerCase();
				default: return "old "+name().toLowerCase();
				}
		}
	}
}
