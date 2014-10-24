package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Transmuter extends SpecialistMage
{
	@Override public String ID(){return "Transmuter";}
	private final static String localizedStaticName = CMLib.lang().L("Transmuter");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_TRANSMUTATION;}
	@Override public int opposed(){return Ability.DOMAIN_CONJURATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().delCharAbilityMapping(ID(),"Spell_MagicMissile");

		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Spell_CauseStink",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Spell_ShrinkMouth",25,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Spell_MassWaterbreath",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Spell_Misstep",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Spell_Sonar",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Spell_Grow",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_LedFoot",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_Toadstool",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_AddLimb",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_PolymorphSelf",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Spell_BigMouth",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_Transformation",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_Clone",25,true);
	}
}
