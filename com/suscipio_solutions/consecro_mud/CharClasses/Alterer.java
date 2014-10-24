package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Alterer extends SpecialistMage
{
	@Override public String ID(){return "Alterer";}
	private final static String localizedStaticName = CMLib.lang().L("Alterer");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_ALTERATION;}
	@Override public int opposed(){return Ability.DOMAIN_EVOCATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().delCharAbilityMapping(ID(),"Spell_Shield");

		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Spell_MassFeatherfall",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_IncreaseGravity",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Spell_SlowProjectiles",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Spell_MassSlow",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_Timeport",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_GravitySlam",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Spell_AlterSubstance",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_Duplicate",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_Wish",25,true);
	}
}
