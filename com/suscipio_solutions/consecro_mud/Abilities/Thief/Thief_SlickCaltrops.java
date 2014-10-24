package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Thief_SlickCaltrops extends Thief_Caltrops
{
	@Override public String ID() { return "Thief_SlickCaltrops"; }
	private final static String localizedName = CMLib.lang().L("Slick Caltrops");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SLICKCALTROPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String caltropTypeName(){return CMLib.lang().L("slick ");}
	@Override
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> avoid(s) some @x1caltrops on the floor.",caltropTypeName()));
		else

		{
			final Ability A=CMClass.getAbility("Slip");
			if((A!=null)&&(A.castingQuality(invoker(),mob)==Ability.QUALITY_MALICIOUS))
			{
				mob.location().show(invoker(),mob,this,CMMsg.MSG_OK_ACTION,L("The @x1caltrops on the ground cause <T-NAME> to slip!",caltropTypeName()));
				if(A.invoke(invoker(),mob,true,adjustedLevel(invoker(),0)))
				{
					if(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS))
						CMLib.combat().postDamage(invoker(),mob,null,CMLib.dice().roll(5,6,6*adjustedLevel(invoker(),0)),
								CMMsg.MASK_MALICIOUS|CMMsg.TYP_JUSTICE,Weapon.TYPE_PIERCING,"The "+caltropTypeName()+"caltrops on the ground <DAMAGE> <T-NAME>.");
				}
			}
		}
		// does not set sprung flag -- as this trap never goes out of use
	}
}
