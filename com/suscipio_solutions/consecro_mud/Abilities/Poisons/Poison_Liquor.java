package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Poison_Liquor extends Poison_Alcohol
{
	@Override public String ID() { return "Poison_Liquor"; }
	private final static String localizedName = CMLib.lang().L("Liquor");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"LIQUORUP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_POISON;}

	@Override protected int alchoholContribution(){return 2;}
	@Override protected int level(){return 2;}

	@Override
	public void unInvoke()
	{
		MOB mob=null;
		if((affected!=null)&&(affected instanceof MOB))
		{
			mob=(MOB)affected;
			if((CMLib.dice().rollPercentage()<(drunkness*10))&&(!((MOB)affected).isMonster()))
			{
				final Ability A=CMClass.getAbility("Disease_Migraines");
				if(A!=null) A.invoke(mob,mob,true,0);
			}
			CMLib.commands().postStand(mob,true);
		}
		super.unInvoke();
		if((mob!=null)&&(!mob.isInCombat()))
			mob.location().show(mob,null,CMMsg.MSG_SLEEP,L("<S-NAME> curl(s) up on the ground and fall(s) asleep."));
	}
}
