package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Gnoll extends StdMOB
{
	@Override public String ID(){return "Gnoll";}
	public Gnoll()
	{
		super();
		username="a Gnoll";
		setDescription("a 7 foot tall creature with a body resembling a large human and the head of a hyena.");
		setDisplayText("A nasty Gnoll stands here.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(20);
		basePhyStats.setWeight(300);
		setWimpHitPoint(0);

		Weapon h=CMClass.getWeapon("MorningStar");
		final Random randomizer = new Random(System.currentTimeMillis());
		final int percentage = randomizer.nextInt() % 100;
		if((percentage & 1) == 0)
		{
		   h = CMClass.getWeapon("Longsword");
		}
		if(h!=null)
		{
			h.wearAt(Wearable.WORN_WIELD);
			addItem(h);
		}

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,6);
		baseCharStats().setStat(CharStats.STAT_CHARISMA,2);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,22);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
