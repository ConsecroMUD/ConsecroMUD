package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Rabbit extends StdMOB
{
	@Override public String ID(){return "Rabbit";}
	public Rabbit()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a rabbit";
		setDescription("It\\`s small, cute, and fluffy with a cute cotton-ball tail.");
		setDisplayText("A rabbit hops by.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(5 + Math.abs(randomizer.nextInt() % 5));
		setWimpHitPoint(2);

		basePhyStats().setDamage(2);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setMyRace(CMClass.getRace("Rabbit"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
