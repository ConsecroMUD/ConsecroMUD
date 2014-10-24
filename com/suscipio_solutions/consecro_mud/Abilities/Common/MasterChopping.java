package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterChopping extends Chopping
{
	@Override public String ID() { return "MasterChopping"; }
	private final static String localizedName = CMLib.lang().L("Master Wood Chopping");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MCHOP","MASTERCHOP","MCHOPPING","MASTERCHOPPING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(100,mob,level,37);
	}
	@Override protected int baseYield() { return 3; }
}
