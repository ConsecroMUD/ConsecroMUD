package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class FireGiant extends StdMOB
{
	@Override public String ID(){return "FireGiant";}
	public FireGiant()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a Fire Giant";
		setDescription("A tall humanoid standing about 18 feet tall, 12 foot chest, coal black skin and fire red-orange hair.");
		setDisplayText("A Fire Giant ponders killing you.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(0);
		basePhyStats.setWeight(6500 + Math.abs(randomizer.nextInt() % 1001));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,8 + Math.abs(randomizer.nextInt() % 3));
		baseCharStats().setStat(CharStats.STAT_STRENGTH,20);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,13);
		baseCharStats().setMyRace(CMClass.getRace("Giant"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(20);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(15);
		basePhyStats().setArmor(-10);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		addBehavior(CMClass.getBehavior("Aggressive"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

	@Override
	public void recoverCharStats()
	{
		super.recoverCharStats();
		charStats().setStat(CharStats.STAT_SAVE_FIRE,charStats().getStat(CharStats.STAT_SAVE_FIRE)+100);
	}


}
