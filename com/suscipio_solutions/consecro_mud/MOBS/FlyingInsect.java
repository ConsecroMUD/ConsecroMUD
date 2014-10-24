package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class FlyingInsect extends StdMOB
{
	@Override public String ID(){return "FlyingInsect";}

	public FlyingInsect()
	{
		super();
		final Random randomizer = new Random(System.currentTimeMillis());

		username="a flying insect";
		setDescription("The small flying bug is too tiny to tell whether it bites or stings.");
		setDisplayText("A flying insect flits around.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(2);

		basePhyStats().setWeight(Math.abs(randomizer.nextInt() % 2));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,1);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,1);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,15);
		baseCharStats().setMyRace(CMClass.getRace("Insect"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(10);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(1);
		basePhyStats().setArmor(90);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_FLYING);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		final Ability A=CMClass.getAbility("Poison_Sting");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		
		addBehavior(CMClass.getBehavior("Mobile"));
		addBehavior(CMClass.getBehavior("CombatAbilities"));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();

	}
}
