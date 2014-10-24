package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Skeleton extends Undead
{
	@Override public String ID(){return "Skeleton";}
	public Skeleton()
	{

		super();
		username="a skeleton";
		setDescription("A walking pile of bones...");
		setDisplayText("a skeleton rattles as it walks.");
		setMoney(0);
		basePhyStats.setWeight(30);

		final Weapon sword=CMClass.getWeapon("Longsword");
		if(sword!=null)
		{
			sword.wearAt(Wearable.WORN_WIELD);
			addItem(sword);
		}

		basePhyStats().setDamage(5);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);
		basePhyStats().setSpeed(1.0);

		baseCharStats().setMyRace(CMClass.getRace("Skeleton"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
