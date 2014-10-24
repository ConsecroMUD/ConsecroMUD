package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Song_Knowledge extends Song
{
	@Override public String ID() { return "Song_Knowledge"; }
	private final static String localizedName = CMLib.lang().L("Knowledge");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setStat( CharStats.STAT_WISDOM, ( affectableStats.getStat( CharStats.STAT_WISDOM ) + 2 + getXLEVELLevel( invoker() ) ) );
		affectableStats.setStat( CharStats.STAT_INTELLIGENCE, ( affectableStats.getStat( CharStats.STAT_INTELLIGENCE ) + 2 + getXLEVELLevel( invoker() ) ) );
	}
}
