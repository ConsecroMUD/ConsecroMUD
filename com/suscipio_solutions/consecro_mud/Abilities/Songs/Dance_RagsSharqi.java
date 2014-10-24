package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Dance_RagsSharqi extends Dance
{
	@Override public String ID() { return "Dance_RagsSharqi"; }
	private final static String localizedName = CMLib.lang().L("Rags Sharqi");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public void affectCharState(MOB affectedMOB, CharState affectedState)
	{
		affectedState.setHitPoints(affectedState.getHitPoints()+((adjustedLevel(invoker(),0)+10)*5));
	}
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_DISEASE,affectedStats.getStat(CharStats.STAT_SAVE_DISEASE)+(adjustedLevel(invoker(),0)*2));
	}
}
