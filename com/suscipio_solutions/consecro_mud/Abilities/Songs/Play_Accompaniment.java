package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Play_Accompaniment extends Play
{
	@Override public String ID() { return "Play_Accompaniment"; }
	private final static String localizedName = CMLib.lang().L("Accompaniment");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}

	@Override
	public void affectPhyStats(Physical E, PhyStats stats)
	{
		super.affectPhyStats(E,stats);
		if((E instanceof MOB)&&(E!=invoker())&&(((MOB)E).charStats().getCurrentClass().baseClass().equals("Bard")))
		{
			int lvl=adjustedLevel(invoker(),0)/10;
			if(lvl<1) lvl=1;
			stats.setLevel(stats.level()+lvl);
		}
	}
	@Override
	public void affectCharStats(MOB E, CharStats stats)
	{
		super.affectCharStats(E,stats);
		if((E != null)&&(E!=invoker())&&(stats.getCurrentClass().baseClass().equals("Bard")))
		{
			int lvl=adjustedLevel(invoker(),0)/10;
			if(lvl<1) lvl=1;
			stats.setClassLevel(stats.getCurrentClass(),stats.getCurrentClassLevel()+lvl);
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.getGroupMembers(new HashSet<MOB>()).size()<2)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

}
