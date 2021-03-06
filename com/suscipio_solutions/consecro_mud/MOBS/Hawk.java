package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Hawk extends StdMOB
{
	@Override public String ID(){return "Hawk";}
	public Hawk()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a hawk";
		setDescription("a hunting bird with a narrow face, shark beak, and nasty talons.");
		setDisplayText("An hawk soars overhead.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 6));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,9);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,15);

		basePhyStats().setDamage(1);
		basePhyStats().setSpeed(3.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_FLYING);
		baseCharStats().setMyRace(CMClass.getRace("Hawk"));
		baseCharStats().getMyRace().startRacing(this,false);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
