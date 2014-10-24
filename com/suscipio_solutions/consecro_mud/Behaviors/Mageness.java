package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;


public class Mageness extends CombatAbilities
{
	@Override public String ID(){return "Mageness";}

	@Override
	public String accountForYourself()
	{
		return "magliness";
	}

	protected void getSomeMoreMageAbilities(MOB mob)
	{
		for(int a=0;a<((mob.basePhyStats().level())+5);a++)
		{
			Ability addThis=null;
			int tries=0;
			while((addThis==null)&&((++tries)<10))
			{
				addThis=CMClass.randomAbility();
				if((CMLib.ableMapper().qualifyingLevel(mob,addThis)<0)
				||(!CMLib.ableMapper().qualifiesByLevel(mob,addThis))
				||(((addThis.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)&&(!addThis.appropriateToMyFactions(mob)))
				||(mob.fetchAbility(addThis.ID())!=null)
				||((addThis.abstractQuality()!=Ability.QUALITY_MALICIOUS)
				   &&(addThis.abstractQuality()!=Ability.QUALITY_BENEFICIAL_SELF)
				   &&(addThis.abstractQuality()!=Ability.QUALITY_BENEFICIAL_OTHERS)))
					addThis=null;
			}
			if(addThis!=null)
			{
				addThis=(Ability)addThis.newInstance();
				addThis.setSavable(false);
				addThis.setProficiency(CMLib.ableMapper().getMaxProficiency(addThis.ID())/2);
				mob.addAbility(addThis);
				addThis.autoInvocation(mob);
			}
		}
	}

	@Override
	public void startBehavior(PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(!(forMe instanceof MOB)) return;
		final MOB mob=(MOB)forMe;
		combatMode=COMBAT_RANDOM;
		makeClass(mob,getParmsMinusCombatMode(),"Mage");
		newCharacter(mob);
		getSomeMoreMageAbilities(mob);
		//%%%%%att,armor,damage,hp,mana,move
		if((preCastSet==Integer.MAX_VALUE)||(preCastSet<=0))
		{
			setCombatStats(mob,-10,-10,-10,-15,50,-50, true);
			setCharStats(mob);
		}
	}
}
