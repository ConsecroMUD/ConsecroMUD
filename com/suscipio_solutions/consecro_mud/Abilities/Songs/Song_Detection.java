package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Song_Detection extends Song
{
	@Override public String ID() { return "Song_Detection"; }
	private final static String localizedName = CMLib.lang().L("Awareness");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_BONUS);
	}
}
