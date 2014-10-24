package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Zombie extends Undead
{
	@Override public String ID(){return "Zombie";}
	public Zombie()
	{

		super();
		username="a zombie";
		setDescription("decayed and rotting, a dead body has been brought back to life...");
		setDisplayText("a skeleton slowly moves about.");
		setMoney(10);
		basePhyStats.setWeight(30);
		baseCharStats().setMyRace(CMClass.getRace("Undead"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(8);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);
		basePhyStats().setSpeed(1.0);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
