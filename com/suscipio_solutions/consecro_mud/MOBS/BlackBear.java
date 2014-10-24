package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class BlackBear extends StdMOB
{
	@Override public String ID(){return "BlackBear";}
	public BlackBear()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a Black Bear";
		setDescription("A bear, husky with black fur.");
		setDisplayText("A black bear ambles around.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 45));
		setWimpHitPoint(2);

		basePhyStats.setWeight(250 + Math.abs(randomizer.nextInt() % 55));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,16);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,18);
		baseCharStats().setMyRace(CMClass.getRace("Bear"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(6);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(3);
		basePhyStats().setArmor(80);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
