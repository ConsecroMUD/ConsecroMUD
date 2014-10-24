package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Minotaur extends StdMOB
{
	@Override public String ID(){return "Minotaur";}
	public Minotaur()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a minotaur";
		setDescription("A tall humanoid with the head of a bull, and the body of a very muscular man.  It\\`s covered in red fur.");
		setDisplayText("A minotaur glares at you.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(0);
		basePhyStats.setWeight(350 + Math.abs(randomizer.nextInt() % 55));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,4 + Math.abs(randomizer.nextInt() % 5));
		baseCharStats().setStat(CharStats.STAT_STRENGTH,18);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,15);
		baseCharStats().setMyRace(CMClass.getRace("Minotaur"));
		baseCharStats().getMyRace().startRacing(this,false);

		final Weapon mainWeapon=CMClass.getWeapon("BattleAxe");
		if(mainWeapon!=null)
		{
			mainWeapon.wearAt(Wearable.WORN_WIELD);
			this.addItem(mainWeapon);
		}

		basePhyStats().setDamage(12);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(6);
		basePhyStats().setArmor(70);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		addBehavior(CMClass.getBehavior("Aggressive"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
