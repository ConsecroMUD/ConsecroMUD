package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Enchanter extends SpecialistMage
{
	@Override public String ID(){return "Enchanter";}
	private final static String localizedStaticName = CMLib.lang().L("Enchanter");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_ENCHANTMENT;}
	@Override public int opposed(){return Ability.DOMAIN_ABJURATION;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().delCharAbilityMapping(ID(),"Spell_ResistMagicMissiles");

		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Spell_Fatigue",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Spell_ManaBurn",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Spell_MindLight",25,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Spell_Alarm",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Spell_MindFog",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Spell_Enthrall",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_Brainwash",0,"",false,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Spell_AweOther",0,"",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_LowerResists",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Spell_MassHold",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_RogueLimb",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_Permanency",true);
	}
}
