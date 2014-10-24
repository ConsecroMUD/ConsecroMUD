package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class MOBHelper extends StdBehavior
{
	@Override public String ID(){return "MOBHelper";}

	@Override
	public String accountForYourself()
	{
		return "friend protecting";
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB attacker=msg.source();
		final MOB monster=(MOB)affecting;
		final MOB victim=(MOB)msg.target();

		if((attacker!=monster)
		&&(victim!=monster)
		&&(attacker!=victim)
		&&(!monster.isInCombat())
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(CMLib.flags().canBeSeenBy(attacker,monster))
		&&(CMLib.flags().canBeSeenBy(victim,monster))
		&&(victim.isMonster()))
			Aggressive.startFight(monster,attacker,true,false,null);
	}
}
