package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Song_Valor extends Song
{
	@Override public String ID() { return "Song_Valor"; }
	private final static String localizedName = CMLib.lang().L("Valor");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker!=null)
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
											+invoker().charStats().getStat(CharStats.STAT_CHARISMA)
											+super.adjustedLevel(invoker(),0));
	}
}
