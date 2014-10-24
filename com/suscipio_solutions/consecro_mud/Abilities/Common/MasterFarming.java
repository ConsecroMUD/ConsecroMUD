package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterFarming extends Farming
{
	@Override public String ID() { return "MasterFarming"; }
	private final static String localizedName = CMLib.lang().L("Master Farming");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MPLANT","MFARM","MFARMING","MASTERPLANT","MASTERFARM","MASTERFARMING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(113,mob,level,37);
	}
	@Override protected int baseYield() { return 3; }
}
