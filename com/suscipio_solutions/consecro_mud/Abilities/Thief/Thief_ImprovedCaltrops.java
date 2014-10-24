package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Thief_ImprovedCaltrops extends Thief_Caltrops
{
	@Override public String ID() { return "Thief_ImprovedCaltrops"; }
	private final static String localizedName = CMLib.lang().L("Improved Caltrops");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"IMPROVEDCALTROPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String caltropTypeName(){return CMLib.lang().L("improved ");}
	@Override
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||((CMLib.dice().rollPercentage()-getXLEVELLevel(invoker()))<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> avoid(s) some @x1caltrops on the floor.",caltropTypeName()));
		else
		{
			CMLib.combat().postDamage(invoker(),mob,null,CMLib.dice().roll(8,6,5*adjustedLevel(invoker(),0)),
					CMMsg.MASK_MALICIOUS|CMMsg.TYP_JUSTICE,Weapon.TYPE_PIERCING,"The "+caltropTypeName()+"caltrops on the ground <DAMAGE> <T-NAME>.");
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}
