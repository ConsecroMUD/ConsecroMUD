package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Worm extends StdMOB
{
	@Override public String ID(){return "Worm";}

	public Worm()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a worm";
		setDescription("A long squirmy slithering thing.");
		setDisplayText("A worm is squirming around.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(2);

		basePhyStats().setWeight(Math.abs(randomizer.nextInt() % 2));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,1);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,1);
		baseCharStats().setMyRace(CMClass.getRace("Worm"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		addBehavior(CMClass.getBehavior("Mobile"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();

		if(numAllAbilities()>0)
			addBehavior(CMClass.getBehavior("CombatAbilities"));
	}
}
