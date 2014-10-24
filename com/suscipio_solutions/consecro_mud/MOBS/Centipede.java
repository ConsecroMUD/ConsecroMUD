package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Centipede extends StdMOB
{
	@Override public String ID(){return "Centipede";}

	public Centipede()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a centipede";
		setDescription("The centipede is a long worm-like insect with a deadly maw and numerous legs.");
		setDisplayText("A centipede crawls around you.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(2);

		basePhyStats().setWeight(Math.abs(randomizer.nextInt() % 2));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,1);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,1);
		baseCharStats().setMyRace(CMClass.getRace("Centipede"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		final Ability A=CMClass.getAbility("Poison_Sting");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();

		addBehavior(CMClass.getBehavior("CombatAbilities"));
	}
}
