package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Bugbear extends StdMOB
{
	@Override public String ID(){return "Bugbear";}
	public Bugbear()
	{
		super();
		username="a Bugbear";
		setDescription("a 7 foot tall, hairy, yellow-brown, muscular creature with sharp teeth and recessed eyes.");
		setDisplayText("A large Bugbear stands here.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(20);
		basePhyStats.setWeight(300);
		setWimpHitPoint(0);

		final Weapon h=CMClass.getWeapon("Halberd");
		if(h!=null)
		{
			h.wearAt(Wearable.WORN_WIELD);
			addItem(h);
		}

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,6);
		baseCharStats().setStat(CharStats.STAT_CHARISMA,2);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,22);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(3);
		basePhyStats().setArmor(70);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
