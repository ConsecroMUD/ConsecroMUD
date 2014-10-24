package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Play_Carol extends Play
{
	@Override public String ID() { return "Play_Carol"; }
	private final static String localizedName = CMLib.lang().L("Carol");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String songOf(){return CMLib.english().startWithAorAn(name());}
	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if(invoker()!=null)
			stats.setStat(CharStats.STAT_SAVE_MIND,stats.getStat(CharStats.STAT_SAVE_MIND)
								+(adjustedLevel(invoker(),0)*2)
								+invoker().charStats().getStat(CharStats.STAT_CHARISMA));
	}
}
