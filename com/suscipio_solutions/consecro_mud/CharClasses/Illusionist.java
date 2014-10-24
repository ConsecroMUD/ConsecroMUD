package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Illusionist extends SpecialistMage
{
	@Override public String ID(){return "Illusionist";}
	private final static String localizedStaticName = CMLib.lang().L("Illusionist");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_ILLUSION;}
	@Override public int opposed(){return Ability.DOMAIN_DIVINATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}

	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Spell_DispelDivination",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Spell_Torture",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_FeignInvisibility",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Spell_IllusoryDisease",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_Phantasm",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_GreaterInvisibility",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_DivineBeauty",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Spell_AlternateReality",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_EndlessRoad",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_FeelTheVoid",25,true);
	}
}
