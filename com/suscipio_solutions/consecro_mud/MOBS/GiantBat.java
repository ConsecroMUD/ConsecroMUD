package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GiantBat extends StdMOB
{
	@Override public String ID(){return "GiantBat";}
	public GiantBat()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a giant bat";
		setDescription("It is a giant version of your common bat.");
		setDisplayText("A giant bat flies nearby.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 100));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,16);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,17);

		basePhyStats().setDamage(8);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(4);
		basePhyStats().setArmor(80);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_FLYING);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
