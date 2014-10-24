package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Beaver extends StdMOB
{
	@Override public String ID(){return "Beaver";}
	public Beaver()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a beaver";
		setDescription("It\\`s a small rodent with a large tail and sharp teeth.");
		setDisplayText("A beaver is hard at work.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		basePhyStats().setDamage(4);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		final Ability A=CMClass.getAbility("Chopping");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		
		baseCharStats().setMyRace(CMClass.getRace("GiantRat"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
