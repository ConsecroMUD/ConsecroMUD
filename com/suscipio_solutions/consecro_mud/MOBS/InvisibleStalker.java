package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class InvisibleStalker extends StdMOB
{
	@Override public String ID(){return "InvisibleStalker";}
	public InvisibleStalker()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="an Invisible Stalker";
		setDescription("A shimmering blob of energy.");
		setDisplayText("An invisible stalker hunts here.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(10 + Math.abs(randomizer.nextInt() % 10));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,12 + Math.abs(randomizer.nextInt() % 3));
		baseCharStats().setStat(CharStats.STAT_STRENGTH,20);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,13);

		basePhyStats().setDamage(16);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(4);
		basePhyStats().setArmor(0);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_INVISIBLE);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		addBehavior(CMClass.getBehavior("Aggressive"));
		addBehavior(CMClass.getBehavior("Mobile"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
