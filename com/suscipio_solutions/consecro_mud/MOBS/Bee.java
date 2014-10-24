package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Bee extends StdMOB
{
	@Override public String ID(){return "Bee";}
	public Bee()
	{
		super();

		username="a bee";
		setDescription("It\\`s a small buzzing insect with a nasty stinger on its butt.");
		setDisplayText("A bee buzzes around here.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		basePhyStats.setWeight(1);
		setWimpHitPoint(2);

		addBehavior(CMClass.getBehavior("Follower"));
		addBehavior(CMClass.getBehavior("CombatAbilities"));
		basePhyStats().setDamage(1);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		basePhyStats().setDisposition(PhyStats.IS_FLYING);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(80);

		baseCharStats().setMyRace(CMClass.getRace("Insect"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));
		final Ability A=CMClass.getAbility("Poison_BeeSting");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
