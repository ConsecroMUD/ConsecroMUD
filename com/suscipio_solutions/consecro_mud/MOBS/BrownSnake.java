package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class BrownSnake extends StdMOB
{
	@Override public String ID(){return "BrownSnake";}
	public BrownSnake()
	{
		super();
		username="a brown snake";
		setDescription("A fearsome creature with long fangs and deadly venom.");
		setDisplayText("A brown snake is not pleased to see you");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);

		addBehavior(CMClass.getBehavior("CombatAbilities"));
		addAbility(CMClass.getAbility("Poison"));

		basePhyStats().setDamage(4);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);

		baseCharStats().setMyRace(CMClass.getRace("Snake"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
