package com.suscipio_solutions.consecro_mud.Abilities.Common;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MasterDigging extends Digging
{
	@Override public String ID() { return "MasterDigging"; }
	private final static String localizedName = CMLib.lang().L("Master Gem Digging");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"MGDIG","MGDIGGING","MGEMDIGGING","MASTERGDIG","MASTERGDIGGING","MASTERGEMDIGGING"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected int getDuration(MOB mob, int level)
	{
		return getDuration(150,mob,level,37);
	}
	@Override protected int baseYield() { return 3; }
}

