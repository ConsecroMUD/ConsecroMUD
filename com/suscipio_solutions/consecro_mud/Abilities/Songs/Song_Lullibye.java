package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Song_Lullibye extends Song
{
	@Override public String ID() { return "Song_Lullibye"; }
	private final static String localizedName = CMLib.lang().L("Lullaby");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}

	boolean asleep=false;
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker) return;
		if(asleep)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SLEEPING);
	}


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return true;
		if(mob==invoker) return true;
		final boolean oldasleep=asleep;
		if(CMLib.dice().rollPercentage()>(50-(2*getXLEVELLevel(invoker()))))
			asleep=true;
		else
			asleep=false;
		if(asleep!=oldasleep)
		{
			if(oldasleep)
			{
				if(CMLib.flags().isSleeping(mob))
					mob.phyStats().setDisposition(mob.phyStats().disposition()-PhyStats.IS_SLEEPING);
				mob.location().show(mob,null,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> wake(s) up."));
			}
			else
			{
				mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> fall(s) asleep."));
				mob.phyStats().setDisposition(mob.phyStats().disposition()|PhyStats.IS_SLEEPING);
			}
		}

		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(msg.source()==invoker)
			return true;

		if(msg.source()!=affected)
			return true;


		if((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
		&&((msg.targetMinor()==CMMsg.TYP_STAND)||(msg.sourceMinor()==CMMsg.TYP_SIT))&&(asleep))
			return false;
		return true;
	}
}
