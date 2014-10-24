package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class HeavenlyServent extends StdMOB
{
	@Override public String ID(){return "HeavenlyServent";}
	public HeavenlyServent()
	{
		super();

		final Random randomizer = new Random(System.currentTimeMillis());

		username="an immortal servant";
		setDescription("An angelic form in gowns of white, with golden hair, and an ever present smile.");
		setDisplayText("A servant of the Immortals is running errands.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		addBehavior(CMClass.getBehavior("Mobile"));
		addBehavior(CMClass.getBehavior("MudChat"));

		basePhyStats().setDamage(25);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(10);
		basePhyStats().setArmor(0);
		baseCharStats().setMyRace(CMClass.getRace("Human"));
		baseCharStats().getMyRace().startRacing(this,false);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
