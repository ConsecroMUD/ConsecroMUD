package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Teacher extends StdMOB
{
	@Override public String ID(){return "Teacher";}
	public Teacher()
	{
		super();
		username="Cornelius, Knower of All Things";
		setDescription("He looks wise beyond his years.");
		setDisplayText("Cornelius is standing here contemplating your ignorance.");
		CMLib.factions().setAlignment(this,Faction.Align.GOOD);
		setMoney(100);
		basePhyStats.setWeight(150);
		setWimpHitPoint(200);

		Behavior B=CMClass.getBehavior("MOBTeacher");
		if(B!=null) addBehavior(B);
		B=CMClass.getBehavior("MudChat");
		if(B!=null) addBehavior(B);
		B=CMClass.getBehavior("CombatAbilities");
		if(B!=null) addBehavior(B);

		for(final int i : CharStats.CODES.BASECODES())
			baseCharStats().setStat(i,25);
		baseCharStats().setMyRace(CMClass.getRace("Human"));
		baseCharStats().getMyRace().startRacing(this,false);

		basePhyStats().setAbility(10);
		basePhyStats().setLevel(25);
		basePhyStats().setArmor(-500);
		setAttribute(MOB.Attrib.NOTEACH,false);

		baseState.setHitPoints(4999);
		baseState.setMana(4999);
		baseState.setMovement(4999);

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}




}
