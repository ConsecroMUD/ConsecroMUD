package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Horse extends StdRideable
{
	@Override public String ID(){return "Horse";}
	public Horse()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a horse";
		setDescription("It\\`s a beautiful brown steed.");
		setDisplayText("A horse stands here.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(700 + Math.abs(randomizer.nextInt() % 200));
		setWimpHitPoint(2);

		basePhyStats().setDamage(4);
		setRideBasis(Rideable.RIDEABLE_LAND);
		setRiderCapacity(2);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseCharStats().setMyRace(CMClass.getRace("Horse"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
