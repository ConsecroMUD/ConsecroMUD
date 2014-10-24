package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterForaging extends Foraging
{
	@Override public String ID() { return "MasterForaging"; }
	private final static String localizedName = CMLib.lang().L("Master Foraging");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MFORAGE","MFORAGING","MASTERFORAGE","MASTERFORAGING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(113,mob,level,25);
	}
	@Override protected int baseYield() { return 3; }
}

