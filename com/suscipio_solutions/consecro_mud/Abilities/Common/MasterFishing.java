package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterFishing extends Fishing
{
	@Override public String ID() { return "MasterFishing"; }
	private final static String localizedName = CMLib.lang().L("Master Fishing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MFISH","MASTERFISH"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(113,mob,level,37);
	}
	@Override protected int baseYield() { return 3; }
}
