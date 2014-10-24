package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Crocodile extends StdMOB
{
	@Override public String ID(){return "Crocodile";}
	public Crocodile()
	{
		super();
		username="a crocodile";
		setDescription("A long green lizard with long ferocious rows of teeth.");
		setDisplayText("A crocodile is waiting patiently for prey.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);

		basePhyStats().setDamage(30);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(5);
		basePhyStats().setArmor(70);

		baseCharStats().setMyRace(CMClass.getRace("Crocodile"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().rollHP(basePhyStats.level(), 20));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
