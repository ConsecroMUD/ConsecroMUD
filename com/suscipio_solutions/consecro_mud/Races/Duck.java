package com.suscipio_solutions.consecro_mud.Races;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;


public class Duck extends WaterFowl
{
	@Override public String ID(){	return "Duck"; }
	@Override public String name(){ return "Duck"; }

	@Override
	public String makeMobName(char gender, int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
			case Race.AGE_CHILD:
				return "duckling";
			case Race.AGE_YOUNGADULT:
			case Race.AGE_MATURE:
			case Race.AGE_MIDDLEAGED:
			default:
				switch(gender)
				{
				case 'M': case 'm': return "drake";
				case 'F': case 'f': return "duck";
				default: return name().toLowerCase();
				}
			case Race.AGE_OLD:
			case Race.AGE_VENERABLE:
			case Race.AGE_ANCIENT:
				switch(gender)
				{
				case 'M': case 'm': return "old drake";
				case 'F': case 'f': return "old duck";
				default: return "old "+name().toLowerCase();
				}
		}
	}
}
