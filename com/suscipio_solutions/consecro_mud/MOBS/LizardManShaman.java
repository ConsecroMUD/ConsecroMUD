package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class LizardManShaman extends LizardMan
{
	@Override public String ID(){return "LizardManShaman";}
	protected int spellDown=3;

	public LizardManShaman()
	{
		super();
		username="a Lizard Man";
		setDescription("a 6 foot tall reptilian humanoid.");
		setDisplayText("A mean looking Lizard Man stands here.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(20);
		basePhyStats.setWeight(225);
		setWimpHitPoint(0);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,9);
		baseCharStats().setStat(CharStats.STAT_CHARISMA,2);
		baseCharStats().setStat(CharStats.STAT_STRENGTH,18);

		baseCharStats().setMyRace(CMClass.getRace("LizardMan"));
		basePhyStats().setAbility(0);
		basePhyStats().setDamage(6);
		basePhyStats().setSpeed(3);
		basePhyStats().setLevel(3);
		basePhyStats().setArmor(80);
		baseCharStats().setCurrentClass(CMClass.getCharClass("Cleric"));

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}

	public void addNaturalAbilities()
	{
		final Ability p1 =CMClass.getAbility("Prayer_ProtGood");
		p1.setProficiency(CMLib.dice().roll(5, 10, 50));
		p1.setSavable(false);
		this.addAbility(p1);

		final Ability p2 =CMClass.getAbility("Prayer_CauseLight");
		p2.setProficiency(CMLib.dice().roll(5, 10, 50));
		p2.setSavable(false);
		this.addAbility(p2);

		final Ability p3 =CMClass.getAbility("Prayer_Curse");
		p3.setProficiency(CMLib.dice().roll(5, 10, 50));
		p3.setSavable(false);
		this.addAbility(p3);

		final Ability p4 =CMClass.getAbility("Prayer_Paralyze");
		p4.setProficiency(CMLib.dice().roll(5, 10, 50));
		p4.setSavable(false);
		this.addAbility(p4);

	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((!amDead())&&(tickID==Tickable.TICKID_MOB))
		{
			if (isInCombat())
			{
				if((--spellDown)<=0)
				{
					spellDown=3;
					castSpell();
				}
			}

		}
		return super.tick(ticking,tickID);
	}

	public boolean castSpell()
	{
		Ability prayer = null;
		if(CMLib.dice().rollPercentage() < 70)
			prayer = fetchRandomAbility();
		else
		{
			prayer = CMClass.getAbility("Prayer_CureLight");
			prayer.setProficiency(CMLib.dice().roll(5, 10, 50));
		}

		if(prayer!=null)
			return prayer.invoke(this,null,false,0);
		return false;
	}


}
