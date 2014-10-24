package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterDrilling extends Drilling
{
	@Override public String ID() { return "MasterDrilling"; }
	private final static String localizedName = CMLib.lang().L("Master Drilling");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MDRILL","MDRILLING","MASTERDRILL","MASTERDRILLING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(90,mob,level,25);
	}
	@Override protected int baseYield() { return 3; }
}
