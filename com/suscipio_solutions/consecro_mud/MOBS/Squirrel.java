package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Squirrel extends StdMOB
{
	@Override public String ID(){return "Squirrel";}
	public Squirrel()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a squirrel";
		setDescription("It\\`s small, cute, and quick with a big expressive tail.");
		setDisplayText("A squirrel darts around.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(4450 + Math.abs(randomizer.nextInt() % 5));
		setWimpHitPoint(2);

		basePhyStats().setDamage(2);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setMyRace(CMClass.getRace("Squirrel"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),11,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
