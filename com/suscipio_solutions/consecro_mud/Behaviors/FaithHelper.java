package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;


public class FaithHelper extends StdBehavior
{
	@Override public String ID(){return "FaithHelper";}

	protected boolean mobKiller=false;

	@Override
	public String accountForYourself()
	{
		if(parms.length()>0)
			return "worshippers of "+parms+" protecting";
		else
			return "worshipper protecting";
	}

	@Override
	public void startBehavior(PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(forMe instanceof MOB)
		{
			if(parms.length()>0)
				((MOB)forMe).setWorshipCharID(parms.trim());
		}
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
		&&(observer.getWorshipCharID().length()>0)
		&&(CMLib.flags().canBeSeenBy(source,observer))
		&&(CMLib.flags().canBeSeenBy(target,observer))
		&&(!BrotherHelper.isBrother(source,observer,false)))
		{
			if(observer.getWorshipCharID().equalsIgnoreCase(target.getWorshipCharID()))
			{
				String reason="THAT`S MY FRIEND!! CHARGE!!";
				if((observer.getWorshipCharID().equals(target.getWorshipCharID()))
				&&(!observer.getWorshipCharID().equals(source.getWorshipCharID())))
					reason="BELIEVERS OF "+observer.getWorshipCharID().toUpperCase()+" UNITE! CHARGE!";
				Aggressive.startFight(observer,source,true,false,reason);
			}
		}
	}
}
