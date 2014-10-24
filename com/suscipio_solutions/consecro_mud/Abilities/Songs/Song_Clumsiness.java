package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Song_Clumsiness extends Song
{
	@Override public String ID() { return "Song_Clumsiness"; }
	private final static String localizedName = CMLib.lang().L("Clumsiness");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker) return;

		affectableStats.setAttackAdjustment((affectableStats.attackAdjustment()
										-invoker().charStats().getStat(CharStats.STAT_CHARISMA))
										-(adjustedLevel(invoker(),0)*2));
	}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker) return;

		affectableStats.setStat(CharStats.STAT_DEXTERITY,(int)Math.round(CMath.div(affectableStats.getStat(CharStats.STAT_DEXTERITY),2.0)));
	}
}
