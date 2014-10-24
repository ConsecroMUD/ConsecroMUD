package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Thief_DazzlingCaltrops extends Thief_Caltrops
{
	@Override public String ID() { return "Thief_DazzlingCaltrops"; }
	private final static String localizedName = CMLib.lang().L("Dazzling Caltrops");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"DAZZLINGCALTROPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String caltropTypeName(){return CMLib.lang().L("dazzling ");}

	@Override
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> avoid(s) looking at some @x1caltrops on the floor.",caltropTypeName()));
		else
		if(mob.curState().getMana()>6)
		{
			mob.curState().adjMana(-CMLib.dice().roll(3+getX1Level(mob),8,20),mob.maxState());
			mob.location().show(invoker(),mob,this,CMMsg.MSG_OK_ACTION,L("The @x1caltrops on the ground sparkle and confuse <T-NAME>",caltropTypeName()));
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}
