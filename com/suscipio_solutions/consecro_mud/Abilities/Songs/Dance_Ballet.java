package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Dance_Ballet extends Dance
{
	@Override public String ID() { return "Dance_Ballet"; }
	private final static String localizedName = CMLib.lang().L("Ballet");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setStat(CharStats.STAT_DEXTERITY,
				(affectableStats.getStat(CharStats.STAT_DEXTERITY)+(super.adjustedLevel(invoker(),0)/3)));
	}
}
