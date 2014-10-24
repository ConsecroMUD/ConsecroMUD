package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class FrostGiant extends StdMOB
{
	@Override public String ID(){return "FrostGiant";}
	public FrostGiant()
	{
		super();
		username="a frost giant";
		setDescription("A tall blueish humanoid standing about 16 feet tall and very smelly.");
		setDisplayText("A frost giant looks down at you.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(0);
		basePhyStats.setWeight(3500 + CMLib.dice().roll(1, 1000, 0));


		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,10 + CMLib.dice().roll(1, 6, 0));
		baseCharStats().setStat(CharStats.STAT_STRENGTH,29);
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,9);
		baseCharStats().setMyRace(CMClass.getRace("Giant"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setDamage(19);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(12);
		basePhyStats().setArmor(0);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		Ability A=CMClass.getAbility("Immunities");
		if(A!=null)
		{
			A.setMiscText("COLD");
			addNonUninvokableEffect(A);
		}
		A=CMClass.getAbility("Chant_FeelHeat");
		if(A!=null)
			addNonUninvokableEffect(A);
		
		addBehavior(CMClass.getBehavior("Aggressive"));

		Weapon w=CMClass.getWeapon("BattleAxe");
		if(w!=null)
		{
			w.wearAt(Wearable.WORN_WIELD);
			this.addItem(w);
		}
		
		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

}
