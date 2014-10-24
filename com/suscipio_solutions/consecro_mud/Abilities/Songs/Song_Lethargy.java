package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Song_Lethargy extends Song
{
	@Override public String ID() { return "Song_Lethargy"; }
	private final static String localizedName = CMLib.lang().L("Lethargy");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		if(invoker==affected) return;

		affectableStats.setSpeed(CMath.div(affectableStats.speed(),2.0+CMath.mul(0.25,getXLEVELLevel(invoker()))));
	}
}
