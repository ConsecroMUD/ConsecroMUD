package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Thief_DisablingCaltrops extends Thief_Caltrops
{
	@Override public String ID() { return "Thief_DisablingCaltrops"; }
	private final static String localizedName = CMLib.lang().L("Disabling Caltrops");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"DISABLINGCALTROPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String caltropTypeName(){return CMLib.lang().L("disabling ");}
	@Override
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> avoid(s) some @x1caltrops on the floor.",caltropTypeName()));
		else
		if(mob.curState().getMovement()>6)
		{
			mob.curState().adjMovement(-CMLib.dice().roll(3+getX1Level(mob),6,20),mob.maxState());
			mob.location().show(invoker(),mob,this,CMMsg.MSG_OK_ACTION,L("The @x1caltrops on the ground disable <T-NAME>",caltropTypeName()));
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}
