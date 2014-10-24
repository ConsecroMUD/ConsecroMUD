package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Evoker extends SpecialistMage
{
	@Override public String ID(){return "Evoker";}
	private final static String localizedStaticName = CMLib.lang().L("Evoker");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_EVOCATION;}
	@Override public int opposed(){return Ability.DOMAIN_ALTERATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Spell_ContinualLight",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Spell_Shockshield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_IceLance",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Spell_Ignite",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Spell_ForkedLightning",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Spell_Levitate",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_IceStorm",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_Shove",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_Blademouth",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_LimbRack",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_MassDisintegrate",25,true);
	}
}
