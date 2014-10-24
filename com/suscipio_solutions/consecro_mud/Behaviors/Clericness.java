package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;


public class Clericness extends CombatAbilities
{
	@Override public String ID(){return "Clericness";}

	@Override
	public String accountForYourself()
	{
		return "clericliness";
	}

	boolean confirmedSetup=false;

	@Override
	public void startBehavior(PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(!(forMe instanceof MOB)) return;
		final MOB mob=(MOB)forMe;
		combatMode=COMBAT_RANDOM;
		makeClass(mob,getParmsMinusCombatMode(),"Cleric");
		newCharacter(mob);
		//%%%%%att,armor,damage,hp,mana,move
		if((preCastSet==Integer.MAX_VALUE)||(preCastSet<=0))
		{
			setCombatStats(mob,0,15,-10,0,10,-10, true);
			setCharStats(mob);
		}
	}
}
