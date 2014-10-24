package com.suscipio_solutions.consecro_mud.MOBS;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class UmberHulk extends StdMOB
{
	@Override public String ID(){return "UmberHulk";}
	Random randomizer = new Random();
	int confuseDown=3;

	public UmberHulk()
	{
		super();

		username="an Umber Hulk";
		setDescription("An 8 foot tall, 5 foot wide mass of meanness just waiting to eat....");
		setDisplayText("A huge Umber Hulk eyes you.");
		CMLib.factions().setAlignment(this,Faction.Align.EVIL);
		setMoney(10);
		basePhyStats.setWeight(350);
		setWimpHitPoint(0);

		baseCharStats().setStat(CharStats.STAT_INTELLIGENCE,8);
		baseCharStats().setStat(CharStats.STAT_CHARISMA,2);
		baseCharStats().setMyRace(CMClass.getRace("UmberHulk"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setAbility(0);
		basePhyStats().setLevel(8);
		basePhyStats().setAttackAdjustment(basePhyStats().attackAdjustment()+20);
		basePhyStats().setDamage(basePhyStats().damage()+12);
		basePhyStats().setArmor(60);
		basePhyStats().setSpeed(2.0);
		basePhyStats().setSensesMask(PhyStats.CAN_SEE_DARK | PhyStats.CAN_SEE_INFRARED);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((!amDead())&&(tickID==Tickable.TICKID_MOB))
		{
			if((--confuseDown)<=0)
			{
				confuseDown=3;
				confuse();
			}
		}
		return super.tick(ticking,tickID);
	}
	public void addNaturalAbilities()
	{
		final Ability confuse=CMClass.getAbility("Spell_Confusion");
		if(confuse==null) return;

	}
	protected boolean confuse()
	{
		if(this.location()==null)
			return true;

	  Ability confuse=CMClass.getAbility("Spell_Confusion");
		confuse.setProficiency(75);
		if(this.fetchAbility(confuse.ID())==null)
		   this.addAbility(confuse);
		else
			confuse =this.fetchAbility(confuse.ID());

		if(confuse!=null) confuse.invoke(this,null,false,0);
		return true;
	}


}
