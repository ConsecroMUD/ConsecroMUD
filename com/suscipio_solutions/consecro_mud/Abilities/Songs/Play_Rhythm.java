package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Play_Rhythm extends Play
{
	@Override public String ID() { return "Play_Rhythm"; }
	private final static String localizedName = CMLib.lang().L("Rhythm");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}

	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if(mob==invoker) return;
		if(invoker()!=null)
			stats.setStat(CharStats.STAT_SAVE_MAGIC,stats.getStat(CharStats.STAT_SAVE_MAGIC)
									-(invoker().charStats().getStat(CharStats.STAT_CHARISMA)
											+(adjustedLevel(invoker(),0)*2)));
	}
}

