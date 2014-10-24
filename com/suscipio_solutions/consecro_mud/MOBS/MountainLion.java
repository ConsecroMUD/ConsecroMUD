package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class MountainLion extends StdMOB
{
	@Override public String ID(){return "MountainLion";}
	public MountainLion()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a lion";
		setDescription("Mountain Lions have sleek tan fur and watchful eyes.");
		setDisplayText("A mountain lion watches you.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(130 + Math.abs(randomizer.nextInt() % 55));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,13);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,17);
		baseCharStats().setMyRace(CMClass.getRace("GreatCat"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(5);
		basePhyStats().setArmor(80);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
