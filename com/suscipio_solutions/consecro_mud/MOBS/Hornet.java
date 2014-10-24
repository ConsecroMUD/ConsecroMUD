package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Hornet extends StdMOB
{
	@Override public String ID(){return "Hornet";}
	public Hornet()
	{
		super();

		username="a hornet";
		setDescription("It\\`s a small mean flying insect with a nasty stinger on its butt.");
		setDisplayText("A hornet flits around here.");
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
		final Ability A=CMClass.getAbility("Poison_Sting");
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
