package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Tarantella extends Dance
{
	@Override public String ID() { return "Dance_Tarantella"; }
	private final static String localizedName = CMLib.lang().L("Tarantella");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	protected int ticks=1;
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_POISON,affectedStats.getStat(CharStats.STAT_SAVE_POISON)+(adjustedLevel(invoker(),0)*2));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		if((++ticks)>=(15-getXLEVELLevel(invoker())))
		{
			final List<Ability> offenders=CMLib.flags().flaggedAffects(mob,Ability.ACODE_POISON);
			if(offenders!=null)
				for(int a=0;a<offenders.size();a++)
					offenders.get(a).unInvoke();
		}

		return true;
	}

}
