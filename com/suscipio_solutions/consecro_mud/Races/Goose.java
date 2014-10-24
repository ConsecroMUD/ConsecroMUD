package com.suscipio_solutions.consecro_mud.Races;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;


public class Goose extends WaterFowl
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
				return "gosling";
			case Race.AGE_YOUNGADULT:
			case Race.AGE_MATURE:
			case Race.AGE_MIDDLEAGED:
			default:
				switch(gender)
				{
				case 'M': case 'm': return "gander";
				case 'F': case 'f': return "goose";
				default: return name().toLowerCase();
				}
			case Race.AGE_OLD:
			case Race.AGE_VENERABLE:
			case Race.AGE_ANCIENT:
				switch(gender)
				{
				case 'M': case 'm': return "old gander";
				case 'F': case 'f': return "old goose";
				default: return "old "+name().toLowerCase();
				}
		}
	}
}
