package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterButchering extends Butchering
{
	@Override public String ID() { return "MasterButchering"; }
	private final static String localizedName = CMLib.lang().L("Master Butchering");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MBUTCHERING","MASTERBUTCHERING","MSKIN","MASTERSKIN"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int weight)
	{
		int duration=(int)Math.round(((weight/(10+getXLEVELLevel(mob))))*2.5);
		duration = super.getDuration(duration, mob, 1, 7);
		if(duration>100) duration=100;
		return duration;
	}
	@Override protected int baseYield() { return 3; }
}
