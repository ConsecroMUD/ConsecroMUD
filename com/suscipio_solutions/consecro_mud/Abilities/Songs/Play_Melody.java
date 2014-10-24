package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Play_Melody extends Play
{
	@Override public String ID() { return "Play_Melody"; }
	private final static String localizedName = CMLib.lang().L("Melody");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected String songOf(){return CMLib.english().startWithAorAn(name());}

	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if(mob==invoker) return;
		if(invoker()!=null)
			stats.setStat(CharStats.STAT_SAVE_MIND,stats.getStat(CharStats.STAT_SAVE_MIND)
										-(invoker().charStats().getStat(CharStats.STAT_CHARISMA)+(adjustedLevel(invoker(),0)*2)));
	}
}

