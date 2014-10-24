package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Play_Background extends Play
{
	@Override public String ID() { return "Play_Background"; }
	private final static String localizedName = CMLib.lang().L("Background");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override
	public void affectCharStats(MOB mob, CharStats stats)
	{
		super.affectCharStats(mob,stats);
		if(invoker()!=null)
		{
			final int cha=invoker().charStats().getStat(CharStats.STAT_CHARISMA)/2;
			final int lvl=adjustedLevel(invoker(),0)/3;
			for(final int i : CharStats.CODES.SAVING_THROWS())
				stats.setStat(i,stats.getStat(i)+lvl+cha);
		}
	}
}

