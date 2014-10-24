package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterMining extends Mining
{
	@Override public String ID() { return "MasterMining"; }
	private final static String localizedName = CMLib.lang().L("Master Mining");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MMINE","MMINING","MASTERMINE","MASTERMINING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(125,mob,level,37);
	}
	@Override protected int baseYield() { return 3; }

}
