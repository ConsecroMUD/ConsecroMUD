package com.suscipio_solutions.consecro_mud.CharClasses;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Abjurer extends SpecialistMage
{
	@Override public String ID(){return "Abjurer";}
	private final static String localizedStaticName = CMLib.lang().L("Abjurer");
	@Override public String name() { return localizedStaticName; }
	@Override public int domain(){return Ability.DOMAIN_ABJURATION;}
	@Override public int opposed(){return Ability.DOMAIN_ENCHANTMENT;}
	@Override public int availabilityCode(){return Area.THEME_FANTASY;}
	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Skill_Spellcraft",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Spell_SongShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Spell_ResistBludgeoning",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Spell_MinManaShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Spell_Counterspell",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Spell_ResistPiercing",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Spell_ManaShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Spell_ChantShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Spell_ResistSlashing",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Spell_PrayerShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Spell_ResistIndignities",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Spell_KineticBubble",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Spell_MajManaShield",25,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Spell_AchillesArmor",25,true);
	}
}
