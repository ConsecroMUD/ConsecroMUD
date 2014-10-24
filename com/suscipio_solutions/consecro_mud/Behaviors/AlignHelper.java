package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class AlignHelper extends StdBehavior
{
	@Override public String ID(){return "AlignHelper";}

	@Override
	public String accountForYourself()
	{
		return "same-aligned protecting";
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB source=msg.source();
		final MOB observer=(MOB)affecting;
		final MOB target=(MOB)msg.target();

		if((source!=observer)
		&&(target!=observer)
		&&(source!=target)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(!observer.isInCombat())
		&&(CMLib.flags().canBeSeenBy(source,observer))
		&&(CMLib.flags().canBeSeenBy(target,observer))
		&&(!BrotherHelper.isBrother(source,observer,false))
		&&( (CMLib.flags().isEvil(target)&&CMLib.flags().isEvil(observer))
			||(CMLib.flags().isNeutral(target)&&CMLib.flags().isNeutral(observer))
			||(CMLib.flags().isGood(target)&&CMLib.flags().isGood(observer))))
		{
			Aggressive.startFight(observer,source,true,false,CMLib.flags().getAlignmentName(observer)+" PEOPLE UNITE! CHARGE!");
		}
	}
}
