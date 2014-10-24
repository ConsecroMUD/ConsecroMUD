package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;


public class Assassin extends GenMob
{
	@Override public String ID(){return "Assassin";}
	public Assassin()
	{
		super();
		username="an assassin";
		setDescription("He`s all dressed in black, and has eyes as cold as ice.");
		setDisplayText("An assassin stands here.");
		final Race R=CMClass.getRace("Human");
		if(R!=null)
		{
			baseCharStats().setMyRace(R);
			R.startRacing(this,false);
		}
		baseCharStats().setStat(CharStats.STAT_DEXTERITY,18);
		baseCharStats().setStat(CharStats.STAT_GENDER,'M');
		baseCharStats().setStat(CharStats.STAT_WISDOM,18);
		basePhyStats().setSensesMask(basePhyStats().sensesMask()|PhyStats.CAN_SEE_DARK);

		Ability A=CMClass.getAbility("Thief_Hide");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		A=CMClass.getAbility("Thief_Sneak");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		A=CMClass.getAbility("Thief_BackStab");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		A=CMClass.getAbility("Thief_Assassinate");
		if(A!=null)
		{
			A.setProficiency(100);
			addAbility(A);
		}
		Item I=CMClass.getWeapon("Longsword");
		if(I!=null)
		{
			addItem(I);
			I.wearAt(Wearable.WORN_WIELD);
		}
		I=CMClass.getArmor("LeatherArmor");
		if(I!=null)
		{
			addItem(I);
			I.wearIfPossible(this);
		}
		final Weapon d=CMClass.getWeapon("Dagger");
		if(d!=null)
		{
			d.wearAt(Wearable.WORN_HELD);
			addItem(d);
		}


		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
		addBehavior(CMClass.getBehavior("CombatAbilities"));
	}

}
