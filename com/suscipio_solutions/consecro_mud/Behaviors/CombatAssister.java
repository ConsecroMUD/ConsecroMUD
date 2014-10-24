package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class CombatAssister extends StdBehavior
{
	@Override public String ID(){return "CombatAssister";}

	@Override
	public String accountForYourself()
	{
		if(getParms().length()>0)
			return "protecting of "+CMLib.masking().maskDesc(getParms(),true);
		else
			return "protecting of others";
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB mob=msg.source();
		final MOB monster=(MOB)affecting;
		final MOB target=(MOB)msg.target();

		if((mob!=monster)
		&&(target!=monster)
		&&(mob!=target)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(!monster.isInCombat())
		&&(CMLib.flags().canBeSeenBy(mob,monster))
		&&(CMLib.flags().canBeSeenBy(target,monster))
		&&(CMLib.masking().maskCheck(getParms(),target,false)))
			Aggressive.startFight(monster,mob,true,false,null);
	}
}
