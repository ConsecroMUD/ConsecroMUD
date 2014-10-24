package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Dance_Musette extends Dance
{
	@Override public String ID() { return "Dance_Musette"; }
	private final static String localizedName = CMLib.lang().L("Musette");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		if(invoker==affected) return;

		affectableStats.setSpeed(CMath.div(affectableStats.speed(),2.0+CMath.mul(super.getXLEVELLevel(invoker()),0.30)));
	}
}
