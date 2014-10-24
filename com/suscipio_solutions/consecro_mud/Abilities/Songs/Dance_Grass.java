package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Dance_Grass extends Dance
{
	@Override public String ID() { return "Dance_Grass"; }
	private final static String localizedName = CMLib.lang().L("Grass");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	public static Ability kick=null;
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_BONUS);
		affectableStats.setArmor(affectableStats.armor()
								-(2*adjustedLevel(invoker(),0)));
	}

}
