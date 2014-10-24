package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class RaceHelper extends StdBehavior
{
	@Override public String ID(){return "RaceHelper";}

	@Override
	public String accountForYourself()
	{
		return "same-race protecting";
	}

	@Override
	public void executeMsg(Environmental affecting, CMMsg msg)
	{
		super.executeMsg(affecting,msg);
		if((msg.target()==null)||(!(msg.target() instanceof MOB))) return;
		final MOB source=msg.source();
		final MOB observer=(MOB)affecting;
		final MOB target=(MOB)msg.target();

		if((target==null)||(observer==null)) return;
		if((source!=observer)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(!observer.isInCombat())
		&&(target!=observer)
		&&(source!=target)
		&&(CMLib.flags().canBeSeenBy(source,observer))
		&&(CMLib.flags().canBeSeenBy(target,observer))
		&&(!BrotherHelper.isBrother(source,observer,false)))
		{
			if(observer.charStats().getMyRace().ID().equalsIgnoreCase(target.charStats().getMyRace().ID()))
			{
				String reason="THAT`S MY FRIEND!! CHARGE!!";
				if((observer.charStats().getMyRace().ID().equals(target.charStats().getMyRace().ID()))
				&&(!observer.charStats().getMyRace().ID().equals(source.charStats().getMyRace().ID())))
					reason=observer.charStats().getMyRace().ID().toUpperCase()+"s UNITE! CHARGE!";
				Aggressive.startFight(observer,source,true,false,reason);
			}
		}
	}
}
