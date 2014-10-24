package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Bull extends StdMOB
{
	@Override public String ID(){return "Bull";}
	public Bull()
	{
		super();
		username="a bull";
		setDescription("A large lumbering beast that looks too slow to get out of your way.");
		setDisplayText("An old bull doesn`t look happy to see you.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(7);
		basePhyStats().setArmor(90);
		baseCharStats().setStat(CharStats.STAT_GENDER, 'M');
		baseCharStats().setMyRace(CMClass.getRace("Bull"));
		baseCharStats().getMyRace().startRacing(this,false);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
