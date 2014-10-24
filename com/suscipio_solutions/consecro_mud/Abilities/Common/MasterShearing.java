package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterShearing extends Shearing
{
	@Override public String ID() { return "MasterShearing"; }
	private final static String localizedName = CMLib.lang().L("Master Shearing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MSHEAR","MSHEARING","MASTERSHEAR","MASTERSHEARING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int weight)
	{
		int duration=((weight/(10+getXLEVELLevel(mob))))*2;
		duration = super.getDuration(duration, mob, 1, 25);
		if(duration>100) duration=100;
		return duration;
	}
	@Override protected int baseYield() { return 3; }
}
