package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class WildEagle extends StdMOB
{
	@Override public String ID(){return "WildEagle";}
	public WildEagle()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="an eagle";
		setDescription("a majestic and very patriotic bird.");
		setDisplayText("A eagle gracefully glides upon the wind currents.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 15));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,11);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,17);
		baseCharStats().setMyRace(CMClass.getRace("Eagle"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(2);
		basePhyStats().setSpeed(3.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_FLYING);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
