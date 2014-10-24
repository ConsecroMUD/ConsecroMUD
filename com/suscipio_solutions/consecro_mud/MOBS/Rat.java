package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Rat extends StdMOB
{
	@Override public String ID(){return "Rat";}
	public Rat()
	{
		super();
		username="a rat";
		setDescription("A small furry rodent with a long, leathery tail.");
		setDisplayText("A rat is here nibbling on something.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(2);

		basePhyStats().setDamage(1);

		baseCharStats().setMyRace(CMClass.getRace("Rat"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
